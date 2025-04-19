package ru.virgil.spring.tools.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

object Security {

    val defaultPublicPaths = arrayOf("/error", "/favicon.ico")

    fun getPrincipal(): Any? {
        val authentication = getAuthentication()
        return authentication.principal
    }

    fun getAuthentication(): Authentication {
        val context = SecurityContextHolder.getContext()
        val authentication = context.authentication
        return authentication
    }

    fun getSimpleCreator() = getAuthentication().name!!

    fun getUserDetailsCreator() = getPrincipal() as UserDetails

    @Deprecated("Из старой авторизации")
    fun getSecurityToken() = SecurityContextHolder.getContext().authentication!!
}
