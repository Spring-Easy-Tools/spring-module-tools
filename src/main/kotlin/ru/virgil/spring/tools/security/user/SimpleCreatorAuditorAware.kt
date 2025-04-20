package ru.virgil.spring.tools.security.user

import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import ru.virgil.spring.tools.security.Security
import java.util.*

@Component
class SimpleCreatorAuditorAware : AuditorAware<String> {

    override fun getCurrentAuditor() = Optional.of(Security.getSimpleCreator())
}
