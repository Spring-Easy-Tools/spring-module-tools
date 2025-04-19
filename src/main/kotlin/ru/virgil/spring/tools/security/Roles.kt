package ru.virgil.spring.tools.security

import org.springframework.security.core.authority.SimpleGrantedAuthority

object Roles {

    fun Enum<*>.toAuthority(): SimpleGrantedAuthority {
        return SimpleGrantedAuthority(this.name)
    }
}
