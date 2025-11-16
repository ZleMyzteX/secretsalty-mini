package er.codes.secretsaltyminimal.controller

import er.codes.secretsaltyminimal.dto.ExemptionDto
import er.codes.secretsaltyminimal.dto.ExemptionRequest
import er.codes.secretsaltyminimal.dto.UserDto
import er.codes.secretsaltyminimal.service.ExemptionsService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/exemptions")
class ExemptionsController(
    private val service: ExemptionsService,
) {
    private val log = KotlinLogging.logger {}

    @PostMapping
    fun upsertExemptions(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: ExemptionRequest
    ) {
        log.info { "Upserting exemptions for ${jwt.subject} for user ${request.excludedUserId}" }
        service.createExemption(jwt.subject, request.excludedUserId)
    }

    @DeleteMapping
    fun deleteExemption(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: ExemptionRequest
    ) {
        log.info { "Deleting exemption for ${jwt.subject} for user ${request.excludedUserId}" }
        service.deleteExemption(jwt.subject, request.excludedUserId)
    }

    @GetMapping
    fun getExemptionsForCurrentUser(
        @AuthenticationPrincipal jwt: Jwt
    ) : ResponseEntity<List<UserDto>> {
        log.info { "Retrieving exemptions for ${jwt.subject}" }
        return ResponseEntity.ok(service.getExemptionsForUser(jwt.subject))
    }

    @GetMapping("/all")
    fun getAllExemptions() : ResponseEntity<List<ExemptionDto>> {
        log.info { "Retrieving all exemptions" }

        return ResponseEntity.ok(service.getAllExemptions())
    }

}