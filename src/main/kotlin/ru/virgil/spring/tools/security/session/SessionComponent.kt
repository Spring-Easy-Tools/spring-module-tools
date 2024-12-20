package ru.virgil.spring.tools.security.session

import org.springframework.context.annotation.Bean
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices
import org.springframework.session.web.http.HttpSessionIdResolver
import org.springframework.stereotype.Component

@Component
class SessionComponent(
    private val sessionProperties: SessionProperties,
) {

    @Bean
    fun provideRememberMeServices() = SpringSessionRememberMeServices().also {
        it.setAlwaysRemember(true)
    }

    @Bean
    fun httpSessionIdResolver(): HttpSessionIdResolver = HeaderAndQueryHttpSessionIdResolver(
        sessionProperties.headerName,
        if (sessionProperties.enableWebsocketQueryParam) {
            sessionProperties.queryParamName
        } else {
            null
        }
    )

}
