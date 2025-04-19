package ru.virgil.spring.tools.security

import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.*

@Component
class SimpleCreatorAuditorAware : AuditorAware<String> {

    override fun getCurrentAuditor() = Optional.of(Security.getSimpleCreator())
}
