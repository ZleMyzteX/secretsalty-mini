package er.codes.secretsaltyminimal.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        // Allow requests from your frontend
        configuration.allowedOrigins = listOf(
            "http://localhost:5173",  // Vite default dev server
            "http://localhost:3000",  // Alternative port
            "https://secret.saltybots.com"  // Production
        )

        // Allow common HTTP methods
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")

        // Allow common headers and Authorization header for JWT
        configuration.allowedHeaders = listOf("*")

        // Allow credentials (cookies, authorization headers, etc.)
        configuration.allowCredentials = true

        // Cache preflight response for 1 hour
        configuration.maxAge = 3600L

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
