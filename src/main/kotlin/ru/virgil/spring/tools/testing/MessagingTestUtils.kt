package ru.virgil.spring.tools.testing

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import org.awaitility.constraint.AtMostWaitConstraint
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.awaitility.kotlin.withPollInterval
import org.awaitility.pollinterval.FibonacciPollInterval
import org.springframework.messaging.Message
import java.time.Duration

object MessagingTestUtils {

    private val defaultTimeout = AtMostWaitConstraint.TEN_SECONDS.maxWaitTime

    inline fun <reified T : Any> Message<*>.deserializeFromMessagingTemplate(objectMapper: ObjectMapper): T {
        val decodedPayload = (this.payload as ByteArray).decodeToString()
        return objectMapper.readValue<T>(decodedPayload)
    }

    inline fun <reified T : Any> Message<*>.deserializeFromMessagingAnnotation(objectMapper: ObjectMapper): T {
        val decodedPayload = (this.payload as ByteArray).decodeToString()
        val payloadMap = objectMapper.readValue<Map<String, *>>(decodedPayload)
        return objectMapper.convertValue(payloadMap["payload"]!!)
    }

    fun <T : Any> waitForResult(timeout: Duration = defaultTimeout, predicate: () -> T?): T {
        lateinit var result: T
        await withPollInterval FibonacciPollInterval() atMost timeout until {
            val predicateResult = predicate.invoke()
            if (predicateResult != null) {
                result = predicateResult
            }
            predicateResult != null
        }
        return result
    }
}
