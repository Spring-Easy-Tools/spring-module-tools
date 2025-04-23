package ru.virgil.spring.tools.security.user

import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.stereotype.Component

@Component
class PasswordEncoderProvider {

    @Bean
    fun providePasswordEncoder() = PasswordEncoderFactories.createDelegatingPasswordEncoder()!!
}