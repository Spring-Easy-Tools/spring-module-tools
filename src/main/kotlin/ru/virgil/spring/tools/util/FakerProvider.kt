package ru.virgil.spring.tools.util

import net.datafaker.Faker
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class FakerProvider {

    @Bean
    fun provideFaker() = Faker()
}
