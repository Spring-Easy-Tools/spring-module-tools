package ru.virgil.spring.tools.testing

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.exoquery.pprint
import org.awaitility.constraint.AtMostWaitConstraint
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.util.AntPathMatcher
import ru.virgil.spring.tools.testing.MessagingTestUtils.awaitResult
import ru.virgil.spring.tools.util.logging.Logger
import java.time.Duration

/**
 * A ChannelInterceptor that caches messages.
 */
@Suppress("MemberVisibilityCanBePrivate")
class MessagingChannelInterceptor(
    private val objectMapper: ObjectMapper,
    val destinationPatterns: ArrayList<String> = ArrayList(),
    val messages: MutableList<Message<*>> = arrayListOf(),
) : ChannelInterceptor {

    private val logger = Logger.inject(this::class.java)

    private val matcher = AntPathMatcher()

    private val defaultTimeout = AtMostWaitConstraint.TEN_SECONDS.maxWaitTime

    fun awaitForMessage(
        predicate: (message: Message<*>) -> Boolean,
        timeout: Duration = defaultTimeout,
    ): Message<*> {
        return awaitResult(timeout) { messages.firstOrNull(predicate) }
    }

    fun awaitForMessage(withText: String, timeout: Duration = defaultTimeout): Message<*> {
        return awaitResult(timeout) { messages.firstOrNull { it.payloadContains(withText) } }
    }

    fun awaitLastMessage(timeout: Duration = defaultTimeout): Message<*> {
        return awaitResult(timeout) { messages.firstOrNull() }
    }

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        val headers = StompHeaderAccessor.wrap(message)
        logger.debug { "Message intercepted at ${pprint(headers.destination)}" }
        logger.trace { "Message headers: ${pprint(message.headers)}" }
        logger.trace { "Message payload: ${pprint(objectMapper.readValue<Map<*, *>>(message.payload as ByteArray))}" }
        if (destinationPatterns.isEmpty()) {
            logger.trace { "Message added to interceptor's poll" }
            messages.add(message)
        } else {
            logger.trace { "Destination patterns added ${pprint(destinationPatterns)}. Checking..." }
            headers.destination?.let { currentDestination ->
                if (destinationPatterns.any { pattern -> matcher.match(pattern, currentDestination) }) {
                    logger.trace { "Message added to interceptor's poll" }
                    messages.add(message)
                }
            }
        }
        return message
    }

    private fun Message<*>.payloadContains(withText: String): Boolean {
        val bytes = this.payload as ByteArray
        return bytes.decodeToString().contains(withText, true)
    }
}
