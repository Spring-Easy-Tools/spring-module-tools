package ru.virgil.spring.tools.security.session

import jakarta.servlet.http.HttpServletRequest
import org.springframework.session.web.http.HeaderHttpSessionIdResolver

class HeaderAndQueryHttpSessionIdResolver(
    private val queryParamName: String = QUERY_PARAM_X_AUTH_TOKEN,
    headerName: String = HEADER_X_AUTH_TOKEN,
) : HeaderHttpSessionIdResolver(headerName) {

    override fun resolveSessionIds(request: HttpServletRequest): List<String> {
        val headerResult = super.resolveSessionIds(request)
        if (headerResult.isNotEmpty()) {
            return headerResult
        }
        if (request.getHeader(HEADER_UPGRADE_KEY) != HEADER_UPGRADE_VALUE_WEBSOCKET) {
            return emptyList()
        }
        val queryValue = request.getParameter(QUERY_PARAM_X_AUTH_TOKEN)
        return if (queryValue == null) {
            emptyList()
        } else {
            listOf(queryValue)
        }
    }

    companion object {
        private const val HEADER_UPGRADE_KEY = "Upgrade"
        private const val HEADER_UPGRADE_VALUE_WEBSOCKET = "websocket"
        private const val QUERY_PARAM_X_AUTH_TOKEN = "xauthtoken"
        private const val HEADER_X_AUTH_TOKEN = "X-Auth-Token"
    }
}