package ru.virgil.spring.tools.security.oauth

import org.springframework.security.core.context.SecurityContextHolder
import ru.virgil.spring.tools.security.token.AuthenticatedToken

fun getPrincipal() = when (val auth = SecurityContextHolder.getContext().authentication) {
    is AuthenticatedToken -> auth.securityUser
    else -> throw SecurityException("Неизвестный токен в контексте безопасности")
}

fun getSecurityToken() = SecurityContextHolder.getContext().authentication!!

@Deprecated("Use first class functions?")
object SecurityUserFunctions {

    fun getPrincipal() = when (val auth = SecurityContextHolder.getContext().authentication) {
        is AuthenticatedToken -> auth.securityUser
        else -> throw SecurityException("Неизвестный токен в контексте безопасности")
    }

    fun getSecurityToken() = SecurityContextHolder.getContext().authentication!!
}
