package ru.virgil.spring.tools.security.mock

import org.mockito.Mockito
import org.springframework.security.authentication.AuthenticationEventPublisher
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import ru.virgil.spring.tools.security.internal.InternalAuthenticationToken
import ru.virgil.spring.tools.security.oauth.SecurityUser
import ru.virgil.spring.tools.security.oauth.SecurityUserService

abstract class MockSecurityContextFactory(
    private val securityUserService: SecurityUserService,
    private val authenticationEventPublisher: AuthenticationEventPublisher,
) {

    fun <Authorities> createSecurityContext(
        firebaseUserId: String,
        authorities: Collection<Authorities>,
    ): SecurityContext {
        val authToken = Mockito.mock(Jwt::class.java)
        // TODO: Везде использовать UserDetailsInterface?
        val firebaseUser = securityUserService.loadByFirebaseUserId(firebaseUserId) as SecurityUser?
            ?: securityUserService.registerByFirebaseUserId(firebaseUserId, authToken) as SecurityUser
        firebaseUser.springAuthorities += authorities.map { it.toString() }.toMutableSet()
        val securityContext = SecurityContextHolder.createEmptyContext()
        val internalAuthenticationToken = InternalAuthenticationToken(firebaseUser, authToken)
        securityContext.authentication = internalAuthenticationToken
        authenticationEventPublisher.publishAuthenticationSuccess(internalAuthenticationToken)
        return securityContext
    }
}
