package ru.virgil.spring.tools.util

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.server.ResponseStatusException
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Suppress("MemberVisibilityCanBePrivate", "unused")
object Http {

    @PublishedApi
    internal val currentHttpRequest: HttpServletRequest?
        get() {
            val requestAttributes = RequestContextHolder.getRequestAttributes()
            return if (requestAttributes is ServletRequestAttributes) requestAttributes.request else null
        }

    @PublishedApi
    internal val currentRequestString: String
        get() {
            val request = currentHttpRequest
            return if (request == null) "Unknown request" else "${request.method} ${request.requestURI}"
        }

    fun throwNotFound(clazz: Class<*>, comment: Any? = null, cause: Throwable?): ResponseStatusException {
        val message = when (comment) {
            null -> "${clazz.simpleName} is not found at $currentRequestString"
            else -> "${clazz.simpleName} is not found: $comment at $currentRequestString"
        }
        return ResponseStatusException(NOT_FOUND, message, cause)
    }

    inline fun <reified T : Any> Optional<T>.orNotFound(comment: Any? = null): T = this.getOrNull().orNotFound(comment)

    fun <T> T?.orNotFound(clazz: Class<T>, comment: Any? = null): T {
        val message = when (comment) {
            null -> "${clazz.simpleName} is not found at $currentRequestString"
            else -> "${clazz.simpleName} is not found: $comment at $currentRequestString"
        }
        return this ?: throw ResponseStatusException(NOT_FOUND, message)
    }

    inline fun <reified T> T?.orNotFound(comment: Any? = null): T {
        val message = when (comment) {
            null -> "${T::class.simpleName} is not found at $currentRequestString"
            else -> "${T::class.simpleName} is not found: $comment at $currentRequestString"
        }
        return this ?: throw ResponseStatusException(NOT_FOUND, message)
    }

    inline fun <reified T : Any> Optional<T>.orBadRequest(comment: Any? = null): T = this.getOrNull().orBadRequest(comment)

    inline fun <reified T> T?.orBadRequest(comment: Any? = null): T {
        val message = when (comment) {
            null -> "Request has missed ${T::class.simpleName} parameter at $currentRequestString"
            else -> "Request has missed ${T::class.simpleName} parameter: $comment at $currentRequestString"
        }
        return this ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, message)
    }

    inline fun <reified T : Any> Optional<T>.thenConflict(comment: Any? = null) = this.getOrNull().thenConflict(comment)

    inline fun <reified T> T?.thenConflict(comment: Any? = null) {
        val message = when (comment) {
            null -> "${T::class.simpleName} already exists at $currentRequestString"
            else -> "${T::class.simpleName} already exists at $currentRequestString: $comment"
        }
        if (this != null) throw ResponseStatusException(HttpStatus.CONFLICT, message)
    }

    fun <T> T?.thenConflict(clazz: Class<T>, comment: Any? = null) {
        val message = when (comment) {
            null -> "${clazz.simpleName} already exists at $currentRequestString"
            else -> "${clazz.simpleName} already exists at $currentRequestString: $comment"
        }
        if (this != null) throw ResponseStatusException(HttpStatus.CONFLICT, message)
    }
}
