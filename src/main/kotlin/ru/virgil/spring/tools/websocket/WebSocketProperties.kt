package ru.virgil.spring.tools.websocket

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "web-socket")
data class WebSocketProperties(
    var enabled: Boolean = false,
    var stompEndpoint: String = "/ws",
    var appDestinationPrefixes: List<String> = listOf("/app"),
    var userDestinationPrefix: String = "/user",
    // TODO: В последствии выключить, а то палится токен
    var allowAuthUriQueryParameter: Boolean = false,
)
