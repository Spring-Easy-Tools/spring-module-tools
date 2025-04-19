package ru.virgil.spring.tools.security.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import ru.virgil.spring.tools.Deprecation

// TODO: Настроить работу статусов 401 внутри Spring Security
/**
 * Создается как результат неуспешной авторизации, потому что эксепшены не работают.
 *
 * Кидает статус 403.
 * */
@Deprecated(Deprecation.NEW_NATIVE_AUTH)
class ForbiddenToken(
    private val principal: String,
    cause: String,
) : AbstractAuthenticationToken(setOf()) {

    private val unauthorized = "Unauthorized Token"

    init {
        isAuthenticated = false
        details = "$unauthorized: $cause"
    }

    override fun getCredentials(): Any = unauthorized

    override fun getPrincipal(): Any = principal
}
