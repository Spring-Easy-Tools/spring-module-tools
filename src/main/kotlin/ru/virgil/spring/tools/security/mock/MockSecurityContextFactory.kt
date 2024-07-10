package ru.virgil.spring.tools.security.mock

import org.mockito.Mockito
import org.springframework.security.authentication.AuthenticationEventPublisher
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
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

    fun <Authorities : GrantedAuthority> createSecurityContext(
        firebaseUserId: String,
        authorities: Collection<Authorities>,
    ): SecurityContext {
        logger.debug("Mocking user for testing. Firebase user: {}. Authorities: {}", firebaseUserId, authorities)
        val securityContext = SecurityContextHolder.createEmptyContext()
        val authenticatedToken = securityUserService.mockAuthenticatedToken(firebaseUserId, authorities)
        securityContext.authentication = authenticatedToken
        authenticationEventPublisher.publishAuthenticationSuccess(authenticatedToken)
        return securityContext
    }

    companion object {

        fun SecurityUserService.mockSecurityContext(
            firebaseUserId: String = "test-firebase-user",
            authorities: Collection<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_USER")),
        ): AuthenticatedToken {
            val securityContext = SecurityContextHolder.createEmptyContext()
            val authenticatedToken = this.mockAuthenticatedToken(firebaseUserId, authorities)
            securityContext.authentication = authenticatedToken
            return authenticatedToken
        }

        /**
         * Этот метод не заполняет [SecurityContext], просто создает токен.
         *
         * @see mockSecurityContext
         * */
        fun SecurityUserService.mockAuthenticatedToken(
            firebaseUserId: String = "test-firebase-user",
            authorities: Collection<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_USER")),
        ): AuthenticatedToken {
            val jwtAuthToken = Mockito.mock(Jwt::class.java)
            // TODO: Везде использовать UserDetailsInterface?
            val firebaseUser = this.loadByFirebaseUserId(firebaseUserId) as SecurityUser?
                ?: this.registerByFirebaseUserId(firebaseUserId, jwtAuthToken) as SecurityUser
            firebaseUser.springAuthorities += authorities.map { it.toString() }.toMutableSet()
            return AuthenticatedToken(firebaseUser, jwtAuthToken)
        }
    }
}
