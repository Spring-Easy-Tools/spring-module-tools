package ru.virgil.spring.tools.security.oauth.memory

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import ru.virgil.spring.tools.security.oauth.SecurityUserService

@Service
class InMemorySecurityUserService : SecurityUserService {

    val securityUsers: MutableSet<InMemorySecurityUser> = mutableSetOf()

    override fun loadUserByUsername(username: String): InMemorySecurityUser? =
        securityUsers.lastOrNull { it.springUsername == username }

    override fun loadByFirebaseUserId(firebaseUserId: String): InMemorySecurityUser? =
        securityUsers.lastOrNull { it.firebaseUserId == firebaseUserId }

    override fun registerByFirebaseUserId(firebaseUserId: String, authToken: Jwt): UserDetails {
        val newFirebaseUser = InMemorySecurityUser(firebaseUserId, mutableSetOf("ROLE_USER"))
        securityUsers.add(newFirebaseUser)
        return newFirebaseUser
    }
}
