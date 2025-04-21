package ru.virgil.spring.tools.security.oauth

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.provisioning.UserDetailsManager

interface OAuthToUserMapper<UserRequest, UniversalUser>
        where UserRequest : OAuth2UserRequest, UniversalUser : UserDetails, UniversalUser : OAuth2User {

    val userDetailsManager: UserDetailsManager

    fun findOrMapUser(userRequest: UserRequest): UniversalUser =
        findExistingUser(userRequest) ?: mapUser(userRequest).also { userDetailsManager.createUser(it) }

    fun findExistingUser(userRequest: UserRequest): UniversalUser?

    fun mapUser(userRequest: UserRequest): UniversalUser
}
