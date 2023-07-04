package ru.virgil.spring.tools.security.oauth.firebase

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import ru.virgil.spring.tools.security.oauth.SecurityUserService
import ru.virgil.spring.tools.security.token.AuthenticatedToken
import ru.virgil.spring.tools.security.token.ForbiddenToken

@Component
class FirebaseService(val securityUserService: SecurityUserService) {

    fun registerOrLogin(jwt: Jwt): AuthenticatedToken {
        val securityUser = securityUserService.loadByFirebaseUserId(jwt.extractUserId())
            ?: securityUserService.registerByFirebaseUserId(jwt.extractUserId(), jwt)
        return AuthenticatedToken(securityUser, jwt)
    }

    fun register(jwt: Jwt): AuthenticatedToken {
        val userDetails = securityUserService.registerByFirebaseUserId(jwt.extractUserId(), jwt)
        return AuthenticatedToken(userDetails, jwt)
    }

    fun login(jwt: Jwt): AbstractAuthenticationToken {
        val userDetails = securityUserService.loadByFirebaseUserId(jwt.extractUserId())
        return if (userDetails != null) {
            AuthenticatedToken(userDetails, jwt)
        } else {
            ForbiddenToken(jwt.subject, "Registered Firebase user not found.")
        }
    }

    private fun Jwt.extractUserId(): String = this.claims["user_id"] as String
}
