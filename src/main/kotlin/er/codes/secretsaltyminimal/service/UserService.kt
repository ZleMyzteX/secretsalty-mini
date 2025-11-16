package er.codes.secretsaltyminimal.service

import er.codes.secretsaltyminimal.dto.toUserDto
import er.codes.secretsaltyminimal.jooq.tables.daos.UsersDao
import er.codes.secretsaltyminimal.jooq.tables.pojos.Users
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.concurrent.atomic.AtomicInteger
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service

@Service
class UserService(
    private val usersDao: UsersDao,
) {
    private val log = KotlinLogging.logger {}

    private val userCount = AtomicInteger(0)

    fun getUserById(userId: String): Users {
        return getOrCreateUserById(userId)
    }

    fun userExists(userId: String): Boolean {
        return usersDao.existsById(userId)
    }

    fun getUsersByIds(userIds: Set<String>) = usersDao.fetchByUserId(*userIds.toTypedArray())

    fun getAllUsers() = usersDao.findAll().map { it.toUserDto() }

    fun updateUsername(userId: String, username: String) {
        val user = getOrCreateUserById(userId)
        user.username = username
        usersDao.update(user)
    }

    fun updateWishes(
        userId: String,
        wishes: String
    ) {
        val user = getOrCreateUserById(userId)
        user.wishes = wishes
        usersDao.update(user)
    }

    private fun getOrCreateUserById(userId: String): Users {
        return usersDao.findById(userId) ?: run {
            val newUser = Users(
                userId = userId,
                username = "NewUser${userCount.incrementAndGet()}",
            )
            try {
                log.info { "Creating new user: $newUser" }
                usersDao.insert(newUser)
                newUser
            } catch (e: DuplicateKeyException) {
                // Another thread created it, fetch again
                usersDao.findById(userId)!!
            }
        }
    }

}