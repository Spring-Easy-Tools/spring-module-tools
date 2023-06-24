package ru.virgil.spring.tools.security.oauth

interface SecurityUser : UserDetailsKt {

    val firebaseUserId: String
}
