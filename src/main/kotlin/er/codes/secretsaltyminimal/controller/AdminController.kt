package er.codes.secretsaltyminimal.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jooq.DSLContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin")
class AdminController(
    private val dslContext: DSLContext
) {
    private val log = KotlinLogging.logger {}

    @GetMapping("/reset")
    fun reset() {
        log.info { "Resetting database" }
        dslContext.execute("TRUNCATE TABLE assignments CASCADE")
    }

}