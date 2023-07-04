package ru.virgil.spring.tools.security.oauth

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.jwt.Jwt
import ru.virgil.spring.tools.security.oauth.SecurityUserFunctions.getPrincipal
import ru.virgil.spring.tools.security.oauth.SecurityUserFunctions.getSecurityToken

interface SecurityUserService : UserDetailsService {

    val principal
        get() = getPrincipal()

    val token
        get() = getSecurityToken()

    override fun loadUserByUsername(username: String): UserDetails?

    fun loadByFirebaseUserId(firebaseUserId: String): UserDetails?

    fun registerByFirebaseUserId(firebaseUserId: String, authToken: Jwt): UserDetails
}
