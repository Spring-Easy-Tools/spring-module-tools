package ru.virgil.spring.tools.security

import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationEventPublisher
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import ru.virgil.spring.tools.security.oauth.OAuthTokenHandler


@Configuration
@EnableMethodSecurity
@EnableWebSecurity(debug = true)
open class SecurityConfig(
    val securityProperties: SecurityProperties,
    val oAuthTokenHandler: OAuthTokenHandler,
) {

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
                it.jwt().jwtAuthenticationConverter(oAuthTokenHandler)
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
