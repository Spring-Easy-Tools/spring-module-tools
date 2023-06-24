package ru.virgil.spring.tools.security.oauth

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.jwt.Jwt
import ru.virgil.spring.tools.security.internal.InternalAuthenticationToken
import ru.virgil.spring.tools.security.oauth.SecurityUserFunctions.getPrincipal
import ru.virgil.spring.tools.security.oauth.SecurityUserFunctions.getSecurityToken

fun getPrincipal() = when (val auth = SecurityContextHolder.getContext().authentication) {
    is InternalAuthenticationToken -> auth.securityUser
    else -> throw SecurityException("Неизвестный токен в контексте безопасности")
}

fun getSecurityToken() = SecurityContextHolder.getContext().authentication!!

interface SecurityUserService : UserDetailsService {

    val principal
        get() = getPrincipal()

    val token
        get() = getSecurityToken()

    override fun loadUserByUsername(username: String): UserDetails?

    fun loadByFirebaseUserId(firebaseUserId: String): UserDetails?

    fun registerByFirebaseUserId(firebaseUserId: String, authToken: Jwt): UserDetails
}
