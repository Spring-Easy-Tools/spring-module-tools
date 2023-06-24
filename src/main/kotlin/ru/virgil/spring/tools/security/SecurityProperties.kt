package ru.virgil.spring.tools.security

import org.springframework.boot.context.properties.ConfigurationProperties
import ru.virgil.spring.tools.DeprecationMessages

@ConfigurationProperties(prefix = "security")
data class SecurityProperties(
    /**
     * По этим путям можно будет заходить анонимно
     */
    var anonymousPaths: List<String> = ArrayList(),
    // TODO: Больше не нужно? Как отключать в Resource Server? Как вообще работают сессии в Resource Server?
    /**
     * Использовать для сессий заголовок X-Auth-Token вместо Cookies
     */
    @Deprecated(DeprecationMessages.sessionsNotWorkingYet)
    var useXAuthToken: Boolean = false,
)
