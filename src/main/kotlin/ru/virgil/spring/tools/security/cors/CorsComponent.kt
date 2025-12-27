package ru.virgil.spring.tools.security.cors

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Component
class CorsComponent(
    val corsProperties: CorsProperties,
) {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = corsProperties.origins
        configuration.allowedMethods = corsProperties.allowedMethods
        configuration.allowedHeaders = corsProperties.allowedHeaders
        configuration.exposedHeaders = corsProperties.exposedHeaders
        configuration.allowCredentials = corsProperties.allowCredentials
        val corsConfigurationSource = UrlBasedCorsConfigurationSource()
        corsProperties.paths.forEach { corsConfigurationSource.registerCorsConfiguration(it, configuration) }
        return corsConfigurationSource
    }
}
