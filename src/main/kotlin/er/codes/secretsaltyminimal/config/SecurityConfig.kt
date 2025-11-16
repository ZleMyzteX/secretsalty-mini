package er.codes.secretsaltyminimal.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
class SecurityConfig(
    private val corsConfigurationSource: CorsConfigurationSource
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { csrf -> csrf.disable() } // Disable CSRF for stateless JWT authentication
            .authorizeHttpRequests { authorize ->
                authorize
                    // Public endpoints
                    .requestMatchers("/api/v1/public/**").permitAll()
                    .requestMatchers("/actuator/**").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                    .requestMatchers("/api/v1/users/**").permitAll()

                    .requestMatchers("/api/v1/users/**").authenticated()
                    .requestMatchers("/api/v1/admin/reset").authenticated()

                    // Assignment endpoints - authenticated users only
                    .requestMatchers("/api/v1/assignments/**").authenticated()

                    // All other endpoints require authentication
                    .anyRequest().authenticated()
            }
            .cors { cors ->
                cors.configurationSource(corsConfigurationSource)
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt(withDefaults())
            }
            .build()
    }
}