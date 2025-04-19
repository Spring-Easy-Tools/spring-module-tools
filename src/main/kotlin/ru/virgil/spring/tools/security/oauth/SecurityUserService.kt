package ru.virgil.spring.tools.security.oauth

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.Jwt
import ru.virgil.spring.tools.Deprecation
import ru.virgil.spring.tools.security.Security.getPrincipal
import ru.virgil.spring.tools.security.Security.getSecurityToken

@Deprecated(Deprecation.NEW_NATIVE_AUTH)
 // todo: выключить и перейти на дефолт
interface SecurityUserService  {

    val principal
        get() = getPrincipal()

    val token
        get() = getSecurityToken()

     fun loadUserByUsername(username: String): UserDetails?

    fun loadByFirebaseUserId(firebaseUserId: String): UserDetails?

    fun registerByFirebaseUserId(firebaseUserId: String, authToken: Jwt): UserDetails
}
