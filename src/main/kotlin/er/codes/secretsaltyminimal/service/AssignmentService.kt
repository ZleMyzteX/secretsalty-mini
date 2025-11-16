package er.codes.secretsaltyminimal.service

import er.codes.secretsaltyminimal.dto.UserDto
import er.codes.secretsaltyminimal.dto.toUserDto
import er.codes.secretsaltyminimal.jooq.tables.daos.AssignmentsDao
import er.codes.secretsaltyminimal.jooq.tables.daos.ExemptionsDao
import er.codes.secretsaltyminimal.jooq.tables.daos.UsersDao
import er.codes.secretsaltyminimal.jooq.tables.pojos.Assignments
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class AssignmentService(
    private val usersDao: UsersDao,
    private val exemptionsDao: ExemptionsDao,
    private val assignmentsDao: AssignmentsDao,
    private val userService: UserService
) {
    private val log = KotlinLogging.logger {}

    companion object {
        const val MAX_RETRY_ATTEMPTS = 2_000_000
        const val ASSIGNMENT_TIMEOUT_MS = 180_000 // 180 seconds
    }

    fun drawAssignments() {
        val users = usersDao.findAll()
        val exemptions = exemptionsDao.findAll()

        require(users.size >= 3) {
            "At least 3 members are required to draw Secret Santa assignments"
        }

        require(users.size in 3..100) {
            "Maximum 100 participants supported"
        }

        val userIds = users.map { it.userId!! }

        val exemptionsByUser = exemptions.groupBy { it.userId!! }
            .mapValues { (_, exs) -> exs.map { it.excludedUserId!! }.toSet() }

        validateSolutionIsPossible(userIds, exemptionsByUser)

        val validAssignment = findValidAssignment(userIds, exemptionsByUser)
            ?: throw IllegalStateException("Failed to find a valid assignment after $MAX_RETRY_ATTEMPTS attempts")

        persistAssignments(validAssignment)

        log.info { "Successfully created gift assignments for ${userIds.size} participants" }
    }

    /**
     * Validates that a solution is theoretically possible given the exemptions.
     * Checks if the constraint graph allows for a Hamiltonian cycle.
     */
    private fun validateSolutionIsPossible(
        userIds: List<String>,
        exemptionsByUser: Map<String, Set<String>>
    ) {
        // For each user, check that they have at least one valid option to give to
        // and at least one valid option to receive from
        for (userId in userIds) {
            val exemptions = exemptionsByUser[userId] ?: emptySet()
            val validOptions = userIds.filter { it != userId && it !in exemptions }

            require(validOptions.isNotEmpty()) {
                "User $userId has no valid options to give gifts to (all others are exempted)"
            }
        }

        // Additional check: verify no circular exemptions that block all paths
        // Using a simplified check: if there are N users and each has N-1 exemptions,
        // then a solution is impossible
        for (userId in userIds) {
            val exemptions = exemptionsByUser[userId] ?: emptySet()
            require(exemptions.size < userIds.size - 1) {
                "User $userId cannot participate (too many exemptions: ${exemptions.size}/${userIds.size - 1})"
            }
        }
    }

    /**
     * Finds a valid gift assignment using randomized retry strategy.
     * Returns a map of giver -> receiver, forming a complete Hamiltonian cycle.
     */
    private fun findValidAssignment(
        userIds: List<String>,
        exemptionsByUser: Map<String, Set<String>>
    ): Map<String, String>? {
        val startTime = System.currentTimeMillis()
        var attempts = 0

        while (attempts < MAX_RETRY_ATTEMPTS) {
            // Check timeout
            if (System.currentTimeMillis() - startTime > ASSIGNMENT_TIMEOUT_MS) {
                log.warn { "Assignment search timeout reached after $attempts attempts" }
                return null
            }

            val assignment = mutableMapOf<String, String>()
            val remaining = userIds.toMutableSet()
            val cycle = mutableListOf<String>()

            // Try to build a valid cycle starting from a random user
            var current = remaining.random()
            cycle.add(current)
            remaining.remove(current)
            val startUser = current

            var success = true
            while (remaining.isNotEmpty() && success) {
                val exemptions = exemptionsByUser[current] ?: emptySet()
                val validNext = remaining.filter { it !in exemptions }

                if (validNext.isEmpty()) {
                    success = false
                    break
                }

                current = validNext.random()
                cycle.add(current)
                remaining.remove(current)
            }

            if (!success || remaining.isNotEmpty()) {
                attempts++
                continue
            }

            // Check if we can close the cycle: last person must be able to give to first person
            val lastExemptions = exemptionsByUser[current] ?: emptySet()
            if (startUser in lastExemptions) {
                attempts++
                continue
            }

            // Build the assignment map from the cycle
            for (i in cycle.indices) {
                val giver = cycle[i]
                val receiver = cycle[(i + 1) % cycle.size]
                assignment[giver] = receiver
            }

            // Verify the solution
            if (isValidAssignment(assignment, exemptionsByUser, userIds)) {
                log.info { "Found valid assignment after $attempts attempts" }
                return assignment
            }

            attempts++
        }

        log.error { "Could not find valid assignment after $attempts attempts within ${System.currentTimeMillis() - startTime}ms" }
        return null
    }

    /**
     * Verifies that an assignment is valid:
     * - Forms a complete cycle (each person gives exactly once, receives exactly once)
     * - Respects all exemptions
     * - Forms a single connected cycle
     */
    private fun isValidAssignment(
        assignment: Map<String, String>,
        exemptionsByUser: Map<String, Set<String>>,
        userIds: List<String>
    ): Boolean {
        // Check that all users are present as givers and receivers
        val givers = assignment.keys
        val receivers = assignment.values.toSet()

        if (givers.size != userIds.size || receivers.size != userIds.size) {
            return false
        }

        if (!givers.containsAll(userIds) || !receivers.containsAll(userIds)) {
            return false
        }

        // Check that no person gives to themselves
        for ((giver, receiver) in assignment) {
            if (giver == receiver) {
                return false
            }
        }

        // Check that all exemptions are respected
        for ((giver, receiver) in assignment) {
            val exemptions = exemptionsByUser[giver] ?: emptySet()
            if (receiver in exemptions) {
                return false
            }
        }

        // Verify it forms a single cycle (Hamiltonian cycle)
        val visited = mutableSetOf<String>()
        var current = userIds.first()

        repeat(userIds.size) {
            if (current in visited) {
                return false // Cycle detected too early
            }
            visited.add(current)
            current = assignment[current] ?: return false
        }

        // After one full cycle, we should be back at start and have visited everyone
        if (visited.size != userIds.size || current != userIds.first()) {
            return false
        }

        return true
    }

    private fun persistAssignments(assignment: Map<String, String>) {
        val assignmentObjects = assignment.map { (giverId, receiverId) ->
            Assignments(
                giverId = giverId,
                receiverId = receiverId,
            )
        }

        assignmentsDao.insert(assignmentObjects)
    }

    fun getAssignmentForUser(subject: String): UserDto? {
        val assignment = assignmentsDao.fetchByGiverId(subject).firstOrNull()
        assignment?.let {
            return userService.getUserById(it.receiverId!!).toUserDto()
        }
        return null
    }
}

