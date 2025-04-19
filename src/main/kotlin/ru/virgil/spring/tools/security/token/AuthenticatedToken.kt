package ru.virgil.spring.tools.security.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.Jwt
import ru.virgil.spring.tools.Deprecation

/**
 * Создается как результат успешной авторизации.
 * */
@Deprecated(Deprecation.NEW_NATIVE_AUTH)
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
