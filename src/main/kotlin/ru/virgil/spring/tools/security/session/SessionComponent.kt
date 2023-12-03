package ru.virgil.spring.tools.security.session

import org.springframework.context.annotation.Bean
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices
import org.springframework.session.web.http.HeaderHttpSessionIdResolver
import org.springframework.session.web.http.HttpSessionIdResolver
import org.springframework.stereotype.Component

@Component
class SessionComponent {

    @Bean
    fun provideRememberMeServices() = SpringSessionRememberMeServices().also {
        it.setAlwaysRemember(true)
    }

    @Bean
    fun httpSessionIdResolver(): HttpSessionIdResolver = HeaderHttpSessionIdResolver.xAuthToken()

}
