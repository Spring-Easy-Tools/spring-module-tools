package ru.virgil.spring.tools.security.oauth

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.convert.converter.Converter
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import ru.virgil.spring.tools.security.internal.InternalAuthenticationToken
import ru.virgil.spring.tools.security.oauth.firebase.FirebaseService

@Component
class OAuthTokenHandler(
    val firebaseService: FirebaseService,
    val httpServletRequest: HttpServletRequest,
) : Converter<Jwt, InternalAuthenticationToken> {

    override fun convert(jwt: Jwt): InternalAuthenticationToken = when {
        jwt.claims.contains("firebase") -> {
            if (httpServletRequest.requestURI.contains("oauth/firebase")) {
                firebaseService.registerOrLogin(jwt)
            } else {
                firebaseService.login(jwt)
            }
        }
        else -> throw SecurityException("Unknown ${jwt.javaClass.simpleName} token")
    }
}
