package ru.virgil.spring.tools.security.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.Jwt

/**
 * Создается как результат успешной авторизации.
 * */
class AuthenticatedToken(
    val securityUser: UserDetails,
    val jwt: Jwt,
) : AbstractAuthenticationToken(securityUser.authorities) {

    init {
        isAuthenticated = true
        details = jwt
    }

    override fun getCredentials(): Any = jwt

    override fun getPrincipal(): Any = securityUser
}
