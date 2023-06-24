package ru.virgil.spring.tools.security.oauth

import org.springframework.security.core.context.SecurityContextHolder
import ru.virgil.spring.tools.security.internal.InternalAuthenticationToken

@Deprecated("Use first class functions?")
object SecurityUserFunctions {

    fun getPrincipal() = when (val auth = SecurityContextHolder.getContext().authentication) {
        is InternalAuthenticationToken -> auth.securityUser
        else -> throw SecurityException("Неизвестный токен в контексте безопасности")
    }

    fun getSecurityToken() = SecurityContextHolder.getContext().authentication!!
}
