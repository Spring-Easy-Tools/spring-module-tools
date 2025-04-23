package ru.virgil.spring.tools.security.mock

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.provisioning.UserDetailsManager

abstract class MockSecurityContextFactory(
    private val userDetailsManager: UserDetailsManager,
) {

    fun mockSecurityContext(userDetailsProvider: () -> UserDetails): SecurityContext {
        val userDetails = userDetailsProvider.invoke()
        if (!userDetailsManager.userExists(userDetails.username)) {
            userDetailsManager.createUser(userDetails)
        }
        val securityContext = SecurityContextHolder.createEmptyContext()
        val authenticationToken =
            UsernamePasswordAuthenticationToken(userDetails, userDetails.password, userDetails.authorities)
        securityContext.authentication = authenticationToken
        return securityContext
    }
}
