package ru.virgil.spring.tools.testing

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.exoquery.pprint
import org.awaitility.constraint.AtMostWaitConstraint
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.awaitility.kotlin.withPollInterval
import org.awaitility.pollinterval.FibonacciPollInterval
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.util.AntPathMatcher
import ru.virgil.spring.tools.util.logging.Logger
import java.time.Duration
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

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

    @Deprecated("Это старый массив из примера Spring", replaceWith = ReplaceWith("messages"))
    private val messagesQueue = ArrayBlockingQueue<Message<*>>(100)
    private val matcher = AntPathMatcher()

    private val defaultTimeout = AtMostWaitConstraint.TEN_SECONDS.maxWaitTime

    @Deprecated("Это старый метод из примера Spring", replaceWith = ReplaceWith("awaitLastMessage()"))
    /**
     * See [TestChannelInterceptor.java](https://github.com/rstoyanchev/spring-websocket-portfolio/blob/main/src/test/java/org/springframework/samples/portfolio/web/context/TestChannelInterceptor.java)
     * @return the next received message or `null` if the specified time elapses
     */
    @Throws(InterruptedException::class)
    fun awaitMessage(duration: Duration): Message<*>? {
        logger.trace("Awaiting for message ({})...", pprint(duration))
        return messagesQueue.poll(duration.toMillis(), TimeUnit.MILLISECONDS)
    }

    fun awaitForMessage(
        predicate: (message: Message<*>) -> Boolean,
        timeout: Duration = defaultTimeout,
    ): Message<*> {
        await withPollInterval FibonacciPollInterval() atMost timeout until {
            messages.any(predicate)
        }
        return messages.first(predicate)
    }

    fun awaitForMessage(withText: String, timeout: Duration = defaultTimeout): Message<*> {
        await withPollInterval FibonacciPollInterval() atMost timeout until {
            messages.any { it.payloadContains(withText) }
        }
        return messages.first { it.payloadContains(withText) }
    }

    fun awaitLastMessage(timeout: Duration = defaultTimeout): Message<*> {
        await withPollInterval FibonacciPollInterval() atMost timeout until {
            messages.any()
        }
        return messages.first()
    }

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        val headers = StompHeaderAccessor.wrap(message)
        logger.debug("Message intercepted at {}", pprint(headers.destination))
        logger.trace("Message headers: {}", pprint(message.headers))
        logger.trace("Message payload: {}", pprint(objectMapper.readValue<Map<*, *>>(message.payload as ByteArray)))
        // todo: упростить
        if (destinationPatterns.isEmpty()) {
            logger.trace("Message added to interceptor's poll")
            messagesQueue.add(message)
            messages.add(message)
        } else {
            logger.trace("Destination patterns added {}. Checking...", pprint(destinationPatterns))
            if (headers.destination != null) {
                for (pattern in this.destinationPatterns) {
                    if (matcher.match(pattern, headers.destination!!)) {
                        logger.trace("Message added to interceptor's poll")
                        messagesQueue.add(message)
                        messages.add(message)
                        break
                    }
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
