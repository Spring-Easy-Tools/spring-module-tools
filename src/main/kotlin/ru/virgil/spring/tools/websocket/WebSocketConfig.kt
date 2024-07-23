package ru.virgil.spring.tools.websocket

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.session.Session
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration
import ru.virgil.spring.tools.security.cors.CorsProperties

@Configuration
@EnableWebSocketMessageBroker
open class WebSocketConfig(
    private val corsProperties: CorsProperties,
    private val webSocketProperties: WebSocketProperties,
) : AbstractSessionWebSocketMessageBrokerConfigurer<Session>() {

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        super.configureMessageBroker(registry)
        if (webSocketProperties.enabled.not()) return
        val taskScheduler =
            if (webSocketProperties.useDefaultScheduler) DefaultManagedTaskScheduler() else buildCustomTaskScheduler()
        registry.enableSimpleBroker("/")
            .setHeartbeatValue(
                longArrayOf(
                    webSocketProperties.serverWillSendHeartbeatMs,
                    webSocketProperties.clientShouldSendHeartbeatMs
                )
            )
            .setTaskScheduler(taskScheduler)
        registry.setApplicationDestinationPrefixes(*webSocketProperties.appDestinationPrefixes.toTypedArray())
        registry.setUserDestinationPrefix(webSocketProperties.userDestinationPrefix)
    }

    private fun buildCustomTaskScheduler(): ThreadPoolTaskScheduler {
        val taskScheduler = ThreadPoolTaskScheduler()
        taskScheduler.poolSize = webSocketProperties.schedulerPoolSize
        taskScheduler.setThreadNamePrefix("wss-heartbeat-thread-")
        taskScheduler.initialize()
        return taskScheduler
    }

    override fun configureStompEndpoints(registry: StompEndpointRegistry) {
        if (webSocketProperties.enabled.not()) return
        registry.addEndpoint(webSocketProperties.stompEndpoint)
            .allowCorsOrigins()
        registry.addEndpoint(webSocketProperties.stompEndpoint)
            .allowCorsOrigins()
            .withSockJS()
    }

    /**
     * Разрешенные источники для Web Socket синхронизированы с источниками CORS
     * */
    private fun StompWebSocketEndpointRegistration.allowCorsOrigins(): StompWebSocketEndpointRegistration =
        this.setAllowedOrigins(corsProperties.origins.joinToString())
}
