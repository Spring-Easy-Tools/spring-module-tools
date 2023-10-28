package ru.virgil.spring.tools.security.mock

import org.mockito.Mockito
import org.springframework.security.authentication.AuthenticationEventPublisher
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import ru.virgil.spring.tools.security.oauth.SecurityUser
import ru.virgil.spring.tools.security.oauth.SecurityUserService
import ru.virgil.spring.tools.security.token.AuthenticatedToken
import ru.virgil.spring.tools.util.logging.Logger

abstract class MockSecurityContextFactory(
    private val securityUserService: SecurityUserService,
    private val authenticationEventPublisher: AuthenticationEventPublisher,
) {

    private val logger = Logger.inject(this::class.java)

    fun <Authorities> createSecurityContext(
        firebaseUserId: String,
        authorities: Collection<Authorities>,
    ): SecurityContext {
        logger.debug("Mocking user for testing. Firebase user: {}. Authorities: {}", firebaseUserId, authorities)
        val authToken = Mockito.mock(Jwt::class.java)
        // TODO: Везде использовать UserDetailsInterface?
        val firebaseUser = securityUserService.loadByFirebaseUserId(firebaseUserId) as SecurityUser?
            ?: securityUserService.registerByFirebaseUserId(firebaseUserId, authToken) as SecurityUser
        firebaseUser.springAuthorities += authorities.map { it.toString() }.toMutableSet()
        val securityContext = SecurityContextHolder.createEmptyContext()
        val authenticatedToken = AuthenticatedToken(firebaseUser, authToken)
        securityContext.authentication = authenticatedToken
        authenticationEventPublisher.publishAuthenticationSuccess(authenticatedToken)
        return securityContext
    }
}
