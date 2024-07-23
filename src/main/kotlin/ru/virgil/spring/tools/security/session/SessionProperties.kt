package ru.virgil.spring.tools.security.session

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "session")
data class SessionProperties(
    var headerName: String = "X-Auth-Token",
    var enableWebsocketQueryParam: Boolean = false,
    var queryParamName: String = "xauthtoken",
)
