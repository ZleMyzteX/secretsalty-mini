package er.codes.secretsaltyminimal.controller

import er.codes.secretsaltyminimal.dto.ChangeUsernameDto
import er.codes.secretsaltyminimal.dto.UserDto
import er.codes.secretsaltyminimal.dto.UserWithWishDto
import er.codes.secretsaltyminimal.dto.WishDto
import er.codes.secretsaltyminimal.dto.toUserWithWishDto
import er.codes.secretsaltyminimal.service.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val service: UserService
) {
    private val log = KotlinLogging.logger {}

    @GetMapping
    fun getCurrentUser(
        @AuthenticationPrincipal jwt: Jwt
    ) : ResponseEntity<UserWithWishDto> {
        log.info { "Received request for user data for ${jwt.subject}" }
        return service.getUserById(jwt.subject).toUserWithWishDto().let { ResponseEntity.ok(it) }
    }

    @GetMapping("/all")
    fun getAllUsers() : ResponseEntity<List<UserDto>> {
        log.info { "Received request for all users" }
        return ResponseEntity.ok(service.getAllUsers())
    }

    @PutMapping
    fun updateUsername(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody newUsername: ChangeUsernameDto
    ) {
        log.info { "Received request to update username for ${jwt.subject} to '$newUsername'" }
        service.updateUsername(jwt.subject, newUsername.username)
    }

    @PostMapping
    fun upsertWishes(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody wishes: WishDto
    ) {
        log.info { "Received request to upsert wishes for ${jwt.subject}" }
        service.updateWishes(jwt.subject, wishes.wish)
    }

}