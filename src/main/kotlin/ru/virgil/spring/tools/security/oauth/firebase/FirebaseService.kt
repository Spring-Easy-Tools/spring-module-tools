package ru.virgil.spring.tools.security.oauth.firebase

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.oauth2.jwt.Jwt
import ru.virgil.spring.tools.Deprecation
import ru.virgil.spring.tools.security.oauth.SecurityUserService
import ru.virgil.spring.tools.security.token.AuthenticatedToken
import ru.virgil.spring.tools.security.token.ForbiddenToken
import ru.virgil.spring.tools.util.logging.Logger

// @Component
@Deprecated(Deprecation.NEW_NATIVE_AUTH)
class FirebaseService(val securityUserService: SecurityUserService) {

    private val logger = Logger.inject(this::class.java)

    fun registerOrLogin(jwt: Jwt): AuthenticatedToken {
        logger.debug { "Registering or logging in by firebase user id: ${jwt.extractUserId()}" }
        val loggedInUserDetails = securityUserService.loadByFirebaseUserId(jwt.extractUserId())
        return if (loggedInUserDetails != null) {
            logger.debug { "Logged in user found. Username: ${loggedInUserDetails.username}" }
            AuthenticatedToken(loggedInUserDetails, jwt)
        } else {
            logger.debug { "No registered user. Registering." }
            val registeredUserDetails = securityUserService.registerByFirebaseUserId(jwt.extractUserId(), jwt)
            logger.debug { "Registered user. Username: ${registeredUserDetails.username}" }
            AuthenticatedToken(registeredUserDetails, jwt)
        }
    }

    fun register(jwt: Jwt): AuthenticatedToken {
        val userDetails = securityUserService.registerByFirebaseUserId(jwt.extractUserId(), jwt)
        return AuthenticatedToken(userDetails, jwt)
    }

    fun login(jwt: Jwt): AbstractAuthenticationToken {
        logger.debug { "Logging in by firebase user id: ${jwt.extractUserId()}" }
        val userDetails = securityUserService.loadByFirebaseUserId(jwt.extractUserId())
        return if (userDetails != null) {
            logger.debug { "Logged in user found: ${userDetails.username}" }
            AuthenticatedToken(userDetails, jwt)
        } else {
            logger.debug { "No registered user found." }
            ForbiddenToken(jwt.subject, "Registered Firebase user not found.")
        }
    }

    private fun Jwt.extractUserId(): String = this.claims["user_id"] as String
}
