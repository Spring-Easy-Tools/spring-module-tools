package ru.virgil.spring.tools.security.oauth

import io.exoquery.pprint
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.oauth2.jwt.Jwt
import ru.virgil.spring.tools.Deprecation
import ru.virgil.spring.tools.security.oauth.firebase.FirebaseService
import ru.virgil.spring.tools.security.token.ForbiddenToken
import ru.virgil.spring.tools.util.logging.Logger

@Deprecated(Deprecation.NEW_NATIVE_AUTH)
// @Component
class OAuthTokenHandler(
    val firebaseService: FirebaseService,
    val httpServletRequest: HttpServletRequest,
) : Converter<Jwt, AbstractAuthenticationToken> {

    private val logger = Logger.inject(this::class.java)

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        logger.debug { "Converting JWT token: ${pprint(jwt.claims)}" }
        return when {
            jwt.claims.contains("firebase") -> {
                logger.debug { "We have firebase token" }
                if (httpServletRequest.requestURI.contains("oauth/firebase")) {
                    logger.debug { "Registering. We have registration URI: ${pprint(httpServletRequest.requestURI)}" }
                    firebaseService.registerOrLogin(jwt)
                } else {
                    logger.debug { "Log in. We have average URI: ${pprint(httpServletRequest.requestURI)}" }
                    firebaseService.login(jwt)
                }
            }

            else -> ForbiddenToken(jwt.subject, "Unknown ${jwt.javaClass.simpleName} token")
        }
    }
}
