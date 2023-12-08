package ru.virgil.spring.tools.websocket

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer

// typealias MessageAuth = MessageMatcherDelegatingAuthorizationManager.Builder

@Configuration
// todo: аннотация не позволяет отключить CSRF. Будет доступно в Spring позднее.
//   https://docs.spring.io/spring-security/reference/servlet/integrations/websocket.html#websocket-sameorigin-disable
// @EnableWebSocketSecurity
open class WebSocketSecurityConfig : AbstractSecurityWebSocketMessageBrokerConfigurer() {

    // @Bean
    // fun messageAuthorizationManager(messages: MessageAuth): AuthorizationManager<Message<*>> = messages
    //     .simpMessageDestMatchers("/test/**", "/test-2/**").denyAll()
    //     .simpSubscribeDestMatchers("/test/**/*-user*", "/test-2/**/*-user*").denyAll()
    //     .anyMessage().authenticated()
    //     .build()

    override fun configureInbound(messages: MessageSecurityMetadataSourceRegistry) {
        super.configureInbound(messages)
        messages.anyMessage().authenticated()
    }

    // TODO: Отключены, т.к. не используем кукис
    override fun sameOriginDisabled(): Boolean = true
}
