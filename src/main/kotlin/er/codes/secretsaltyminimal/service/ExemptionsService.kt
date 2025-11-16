package er.codes.secretsaltyminimal.service

import er.codes.secretsaltyminimal.dto.ExemptionDto
import er.codes.secretsaltyminimal.dto.UserDto
import er.codes.secretsaltyminimal.dto.toUserDto
import er.codes.secretsaltyminimal.jooq.tables.Exemptions.Companion.EXEMPTIONS
import er.codes.secretsaltyminimal.jooq.tables.daos.ExemptionsDao
import er.codes.secretsaltyminimal.jooq.tables.pojos.Exemptions
import org.jooq.DSLContext
import org.springframework.stereotype.Service

@Service
class ExemptionsService(
    private val exemptionsDao: ExemptionsDao,
    private val userService: UserService,
    private val dslContext: DSLContext
) {

    fun createExemption(fromUserId: String, toUserId: String) {
        require(fromUserId != toUserId) { "You cannot exempt yourself! :/" }
        require(userService.userExists(toUserId)) {
            "The user you are trying to exempt does not exist!"
        }

        val existingExemptions = exemptionsDao.fetchByUserId(fromUserId)
        require(existingExemptions.size <= 3) {
            "You cannot have more than three exemptions! :("
        }

        val alreadyExists = existingExemptions
            .any { it.excludedUserId == toUserId }

        if (!alreadyExists) {
            val fromUser = userService.getUserById(fromUserId)
            val toUser = userService.getUserById(toUserId)

            exemptionsDao.insert(
                Exemptions(
                    userId = fromUser.userId,
                    excludedUserId = toUser.userId,
                )
            )
        }
    }

    fun getExemptionsForUser(userId: String): List<UserDto> {
        val exemptionIds = exemptionsDao.fetchByUserId(userId).mapNotNull { it.excludedUserId }
        val exemptedUsers = userService.getUsersByIds(exemptionIds.toSet())

        return exemptedUsers.map { it.toUserDto() }
    }

    fun getAllExemptions(): List<ExemptionDto> {
        val allExemptions = exemptionsDao.findAll()

        val userIds = allExemptions.flatMap {
            listOf(it.userId!!, it.excludedUserId!!)
        }.toSet()

        val usersMap = userService.getUsersByIds(userIds)
            .associateBy { it.userId }

        return allExemptions.map {
            ExemptionDto(
                fromUser = usersMap[it.userId]!!.toUserDto(),
                toUser = usersMap[it.excludedUserId]!!.toUserDto()
            )
        }
    }

    fun deleteExemption(fromUserId: String, excludedUserId: String) {
        dslContext.deleteFrom(EXEMPTIONS)
            .where(EXEMPTIONS.USER_ID.eq(fromUserId))
            .and(EXEMPTIONS.EXCLUDED_USER_ID.eq(excludedUserId))
            .execute()
    }

}