package ru.virgil.spring.tools.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security")
data class SecurityProperties(
    /**
     * По этим путям можно будет заходить анонимно
     */
    var anonymousPaths: List<String> = ArrayList(),
    // TODO: Включить по дефолту?
    var allowAuthUriQueryParameter: Boolean = false,
)
