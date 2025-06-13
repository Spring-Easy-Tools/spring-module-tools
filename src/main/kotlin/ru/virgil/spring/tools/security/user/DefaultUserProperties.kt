package ru.virgil.spring.tools.security.user

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "spring.security.user")
data class DefaultUserProperties(
    var name: String? = null,
    var password: String? = null,
    var roles: List<String>? = null,
)
