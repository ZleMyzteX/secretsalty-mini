package er.codes.secretsaltyminimal.controller

import er.codes.secretsaltyminimal.dto.UserDto
import er.codes.secretsaltyminimal.service.AssignmentService
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/assignments")
class AssignmentController(
    private val service: AssignmentService
) {
    private val log = KotlinLogging.logger {}

    @GetMapping("/mine")
    fun getAssignmentForCurrentUser(
        @AuthenticationPrincipal jwt: Jwt
    ) : ResponseEntity<UserDto?> {
        log.info { "Retrieving assignment for ${jwt.subject}" }
        val assignment = service.getAssignmentForUser(jwt.subject)
        return if (assignment != null) {
            ResponseEntity.ok(assignment)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/generate")
    fun generateAssignments(
        @AuthenticationPrincipal jwt: Jwt
    ) : ResponseEntity<Unit> {
        log.info { "Drawing assignments. Triggered by ${jwt.subject}" }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                service.drawAssignments()
                log.info { "Assignments generated successfully" }
            } catch (e: Exception) {
                log.error(e) { "Failed to generate assignments" }
            }
        }

        return ResponseEntity.ok().build()
    }

}