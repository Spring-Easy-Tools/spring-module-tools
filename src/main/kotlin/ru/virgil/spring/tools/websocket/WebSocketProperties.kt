package ru.virgil.spring.tools.websocket

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.config.MessageBrokerRegistry

@ConfigurationProperties(prefix = "web-socket")
data class WebSocketProperties(
    var enabled: Boolean = false,
    /** Это путь для установки соединения WebSocket */
    var stompEndpoint: String = "/ws",
    /** Это пути, которые будут перехватываться контролерами с аннотациями [MessageMapping] */
    var appDestinationPrefixes: List<String> = listOf("/app"),
    /**
     * Это пути для доставки сообщений конкретному пользователю через специальную систему именования
     * @see MessageBrokerRegistry.setUserDestinationPrefix
     * */
    var userDestinationPrefix: String = "/user",
    var serverWillSendHeartbeatMs: Long = 0,
    var clientShouldSendHeartbeatMs: Long = 0,
)
