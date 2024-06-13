package ru.virgil.spring.tools.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices
import ru.virgil.spring.tools.security.oauth.OAuthTokenHandler

@Configuration
@EnableMethodSecurity
@EnableWebSecurity(debug = false)
class SecurityConfig(
    private val securityProperties: SecurityProperties,
    private val oAuthTokenHandler: OAuthTokenHandler,
    private val rememberMeServices: SpringSessionRememberMeServices,
) {

    private fun HttpSecurity.configureSessions() = this
        // TODO: Нужно ли? Это вытащил из офф-документации интеграции
        //  https://docs.spring.io/spring-session/reference/spring-security.html
        .rememberMe {
            it.rememberMeServices(rememberMeServices)
        }
        // TODO: Нужно ли? Это вытащено из баелдунга
        //  https://www.baeldung.com/spring-security-session
        .sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            // TODO: Разобраться как работает защита от фиксации
            //   https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html#ns-session-fixation
            it.sessionFixation().migrateSession()
        }

    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        val propertyIgnoredPaths: List<String> = securityProperties.anonymousPaths
        return httpSecurity
            .configureSessions()
            // .cors {}
            .csrf {
                // TODO: Обязательно включить, [Baeldung](https://www.baeldung.com/spring-security-csrf)
                //   Возможно не нужно, т.к. не используются куки
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
            .logout {
                it.invalidateHttpSession(true)
            }
            .build()
    }
}
