package ru.virgil.spring.tools.security

import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.*
import org.springframework.security.authentication.AuthenticationEventPublisher
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import ru.virgil.spring.tools.security.cors.CorsProperties
import ru.virgil.spring.tools.security.oauth.OAuthTokenHandler


@Configuration
@EnableMethodSecurity
@EnableWebSecurity(debug = true)
class SecurityConfig(
    val securityProperties: SecurityProperties,
    val corsProperties: CorsProperties,
    val oAuthTokenHandler: OAuthTokenHandler,
) {

    // TODO: Новый метод конфигурации CORS. Позволяет легко брать свойства из properties.yml, но по итогу не работает
//    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfigurationSource = UrlBasedCorsConfigurationSource()
        val globalCorsConfiguration = buildGlobalCorsConfiguration()
        corsProperties.pathPattern.forEach { pattern ->
            corsConfigurationSource.registerCorsConfiguration(pattern, globalCorsConfiguration)
        }
        return corsConfigurationSource
    }

    private fun buildGlobalCorsConfiguration(): CorsConfiguration {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.allowedOrigins = corsProperties.origins
        corsConfiguration.allowedMethods = listOf(OPTIONS, HEAD, GET, POST, PUT, DELETE).map { it.name() }
        corsConfiguration.allowCredentials = true
        return corsConfiguration
    }

    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        val propertyIgnoredPaths: List<String> = securityProperties.anonymousPaths
        return httpSecurity
            .cors {}
            .csrf {
                // TODO: Обязательно включить, [Baeldung](https://www.baeldung.com/spring-security-csrf)
                it.disable()
            }
            .oauth2ResourceServer {
                it.jwt { jwt ->
                    jwt.jwtAuthenticationConverter(oAuthTokenHandler)
                }
            }
            .authorizeHttpRequests {
                it.requestMatchers("/", "/favicon.ico", "/error").permitAll()
                it.requestMatchers(*propertyIgnoredPaths.toTypedArray()).permitAll()
                it.requestMatchers("/**").authenticated()
            }
            .build()
    }

    /**
     * Позволяет подключить удобные коллбеки авторизации:
     * [Spring.io](https://docs.spring.io/spring-security/reference/servlet/authentication/events.html)
     *
     * @see AuthResultHandler
     * */
    @Bean
    fun authenticationEventPublisher(applicationEventPublisher: ApplicationEventPublisher?)
            : AuthenticationEventPublisher = DefaultAuthenticationEventPublisher(applicationEventPublisher)
}
