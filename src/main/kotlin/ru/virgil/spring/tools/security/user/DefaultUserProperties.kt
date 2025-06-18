package ru.virgil.spring.tools.security.user

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.security.user")
data class DefaultUserProperties(
    val name: String? = null,
    val password: String? = null,
    val roles: List<String>? = null,
)
