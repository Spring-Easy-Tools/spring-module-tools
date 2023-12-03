package ru.virgil.spring.tools.security.cors

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security.cors")
data class CorsProperties(
    /**
     * С каких адресов принимает запросы CORS
     * */
    var origins: List<String> = ArrayList(),
    var exposedHeaders: List<String> = listOf("X-Auth-Token"),
)
