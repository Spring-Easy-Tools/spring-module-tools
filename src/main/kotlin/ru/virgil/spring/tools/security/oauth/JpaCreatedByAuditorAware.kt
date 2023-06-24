package ru.virgil.spring.tools.security.oauth

import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

@Component
class JpaCreatedByAuditorAware : AuditorAware<UserDetails> {

    // TODO: Все ли в порядке, если возвращается Optional.empty()?
    override fun getCurrentAuditor(): Optional<UserDetails> {
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication
        return if (authentication == null) Optional.empty() else Optional.of(authentication.principal as UserDetails)
    }
}
