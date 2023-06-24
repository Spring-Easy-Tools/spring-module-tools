package ru.virgil.spring.tools.image

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "images")
data class ImageProperties(val cleanOnShutdown: Boolean = true)
