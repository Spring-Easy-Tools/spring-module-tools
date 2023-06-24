package ru.virgil.spring.tools.security

import org.springframework.context.event.EventListener
import org.springframework.security.authentication.AuthenticationEventPublisher
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.stereotype.Component

/**
 * Такие компоненты могут обрабатывать успешную или неуспешную авторизацию.
 *
 * Вручную можно послать сигнал через [AuthenticationEventPublisher.publishAuthenticationSuccess].
 *
 * [Baeldung](https://www.baeldung.com/spring-events)
 *
 * @see SecurityConfig.authenticationEventPublisher
 */
@Component
class AuthResultHandler {

    @EventListener
    fun onSuccess(success: AuthenticationSuccessEvent?) {

    }

    @EventListener
    fun onFailure(failures: AbstractAuthenticationFailureEvent?) {

    }
}
