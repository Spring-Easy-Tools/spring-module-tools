package ru.virgil.spring.tools.security

import org.springframework.boot.context.properties.ConfigurationProperties
import ru.virgil.spring.tools.DeprecationMessages


@ConfigurationProperties(prefix = "security")
data class SecurityProperties(
    // TODO: Вставлять источники CORS из свойств
    /**
     * С каких адресов принимает запросы CORS
     * */
    var corsOrigins: List<String> = ArrayList(),
    /**
     * К каким частям API применяется CORS
     * */
    var corsPathPattern: String = "/**",
    /**
     * По этим путям можно будет заходить анонимно
     */
    var anonymousPaths: List<String> = ArrayList(),
    /**
     * Использовать для сессий заголовок X-Auth-Token вместо Cookies
     */
    @Deprecated(DeprecationMessages.sessionsNotWorkingYet)
    var useXAuthToken: Boolean = false,
    // TODO: Больше не нужно? Как отключать в Resource Server?
)
