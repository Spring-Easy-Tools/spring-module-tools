package ru.virgil.spring.tools.websocket

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@ConfigurationProperties(prefix = "web-socket")
data class WebSocketProperties(
    var enabled: Boolean = false,
    /** Это путь для установки соединения WebSocket */
    var startConnectionEndpoint: String = "/ws",
    /** Это пути, которые будут перехватываться контролерами с аннотациями [MessageMapping] */
    var appDestinationPrefixes: List<String> = listOf("/app"),
    /**
     * Это пути для доставки сообщений конкретному пользователю через специальную систему именования
     * @see MessageBrokerRegistry.setUserDestinationPrefix
     * */
    var userDestinationPrefix: String = "/user",
    var serverWillSendHeartbeatMs: Long = 0,
    var clientShouldSendHeartbeatMs: Long = 0,
    /** [DefaultManagedTaskScheduler] пока не работает. Вместо него настраивается кастомный [ThreadPoolTaskScheduler] */
    var useDefaultScheduler: Boolean = false,
    /** Используется только если [useDefaultScheduler] = false */
    var customSchedulerPoolSize: Int = 8,
    var publicDestinations: List<String> = listOf(),
)
