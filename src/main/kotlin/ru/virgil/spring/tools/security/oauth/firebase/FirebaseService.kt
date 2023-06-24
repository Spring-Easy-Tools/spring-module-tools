package ru.virgil.spring.tools.security.oauth.firebase

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import ru.virgil.spring.tools.security.internal.InternalAuthenticationToken
import ru.virgil.spring.tools.security.oauth.SecurityUserService

@Component
class FirebaseService(val securityUserService: SecurityUserService) {

    fun registerOrLogin(jwt: Jwt): InternalAuthenticationToken {
        val securityUser = securityUserService.loadByFirebaseUserId(jwt.extractUserId())
            ?: securityUserService.registerByFirebaseUserId(jwt.extractUserId(), jwt)
        return InternalAuthenticationToken(securityUser, jwt)
    }

    fun register(jwt: Jwt): InternalAuthenticationToken {
        val userDetails = securityUserService.registerByFirebaseUserId(jwt.extractUserId(), jwt)
        return InternalAuthenticationToken(userDetails, jwt)
    }

    fun login(jwt: Jwt): InternalAuthenticationToken {
        val userDetails = securityUserService.loadByFirebaseUserId(jwt.extractUserId())!!
        return InternalAuthenticationToken(userDetails, jwt)
    }

    private fun Jwt.extractUserId(): String = this.claims["user_id"] as String
}
