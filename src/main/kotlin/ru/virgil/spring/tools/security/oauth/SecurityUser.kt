package ru.virgil.spring.tools.security.oauth

import ru.virgil.spring.tools.Deprecation

@Deprecated(Deprecation.NEW_NATIVE_AUTH)
interface SecurityUser : UserDetailsKt {

    val firebaseUserId: String
}
