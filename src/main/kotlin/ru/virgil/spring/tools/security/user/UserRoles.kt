package ru.virgil.spring.tools.security.user

import org.springframework.security.core.authority.SimpleGrantedAuthority

object UserRoles {

    fun Enum<*>.toAuthority(): SimpleGrantedAuthority {
        return SimpleGrantedAuthority(this.name)
    }
}
