package ru.virgil.spring.tools.websocket

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
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
// Эта аннотация позволяет создавать бин только если web-socket.enabled=true в настройках.
// Это предотвращает активацию WebSocket-инфраструктуры, если она не нужна (например, при тестировании или в сервисах без WebSocket).
@ConditionalOnProperty(prefix = "web-socket", name = ["enabled"], havingValue = "true")
class WebSocketConfig(
    private val corsProperties: CorsProperties,
    private val webSocketProperties: WebSocketProperties,
) : AbstractSessionWebSocketMessageBrokerConfigurer<Session>() {

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        super.configureMessageBroker(registry)
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
        taskScheduler.poolSize = webSocketProperties.customSchedulerPoolSize
        taskScheduler.setThreadNamePrefix("wss-heartbeat-thread-")
        taskScheduler.initialize()
        return taskScheduler
    }

    override fun configureStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint(webSocketProperties.startConnectionEndpoint)
            .allowCorsOrigins()
        registry.addEndpoint(webSocketProperties.startConnectionEndpoint)
            .allowCorsOrigins()
            .withSockJS()
    }

    /**
     * Разрешенные источники для Web Socket синхронизированы с источниками CORS
     * */
    private fun StompWebSocketEndpointRegistration.allowCorsOrigins() =
        this.setAllowedOrigins(corsProperties.origins.joinToString())
}
