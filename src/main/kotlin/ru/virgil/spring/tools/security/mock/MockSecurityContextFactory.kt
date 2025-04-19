package ru.virgil.spring.tools.security.mock

import org.springframework.security.authentication.AuthenticationEventPublisher
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.provisioning.UserDetailsManager
import ru.virgil.spring.tools.Deprecation
import ru.virgil.spring.tools.util.logging.Logger

abstract class MockSecurityContextFactory(
    @Deprecated(Deprecation.NEW_NATIVE_AUTH)
    private val authenticationEventPublisher: AuthenticationEventPublisher,
    private val userDetailsManager: UserDetailsManager,
) {

    private val logger = Logger.inject(this::class.java)

    fun mockSecurityContext(userDetailsProvider: () -> UserDetails): SecurityContext {
        val userDetails = userDetailsProvider.invoke()
        if (!userDetailsManager.userExists(userDetails.username)) {
            userDetailsManager.createUser(userDetails)
        }
        val securityContext = SecurityContextHolder.createEmptyContext()
        val authenticationToken =
            UsernamePasswordAuthenticationToken(userDetails, userDetails.password, userDetails.authorities)
        // authenticationToken.isAuthenticated = true
        securityContext.authentication = authenticationToken
        authenticationEventPublisher.publishAuthenticationSuccess(securityContext.authentication)
        return securityContext
    }

    companion object {

        const val mockedName = "mocked-name"
        const val mockedPassword = "{mocked-crypt}mocked-password"
    }
}
