package ru.virgil.spring.tools.security.oauth.memory

import ru.virgil.spring.tools.Deprecation
import ru.virgil.spring.tools.security.oauth.SecurityUser
import java.util.*

@Deprecated(Deprecation.NEW_NATIVE_AUTH)
open class InMemorySecurityUser(
    override val firebaseUserId: String,
    override val springAuthorities: MutableCollection<String>,
    override val springUsername: String = UUID.randomUUID().toString(),
    override val springPassword: String = UUID.randomUUID().toString(),
    override val springAccountNonExpired: Boolean = true,
    override val springAccountNonLocked: Boolean = true,
    override val springCredentialsNonExpired: Boolean = true,
    override val springEnabled: Boolean = true,
) : SecurityUser
