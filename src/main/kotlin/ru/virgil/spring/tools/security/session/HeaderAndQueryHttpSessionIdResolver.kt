package ru.virgil.spring.tools.security.session

import jakarta.servlet.http.HttpServletRequest
import org.springframework.session.web.http.HeaderHttpSessionIdResolver

class HeaderAndQueryHttpSessionIdResolver(
    headerName: String,
    private val queryParamName: String? = null,
) : HeaderHttpSessionIdResolver(headerName) {

    override fun resolveSessionIds(request: HttpServletRequest): List<String> {
        val headerResult = super.resolveSessionIds(request)
        if (headerResult.isNotEmpty()) {
            return headerResult
        }
        if (queryParamName == null || request.getHeader(HEADER_UPGRADE_KEY) != HEADER_UPGRADE_VALUE_WEBSOCKET) {
            return emptyList()
        }
        val queryValue = request.getParameter(queryParamName)
        return if (queryValue == null) {
            emptyList()
        } else {
            listOf(queryValue)
        }
    }

    companion object {

        private const val HEADER_UPGRADE_KEY = "Upgrade"
        private const val HEADER_UPGRADE_VALUE_WEBSOCKET = "websocket"
    }
}