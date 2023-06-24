package ru.virgil.spring.tools.testing.fluent.request

import org.springframework.http.HttpMethod
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.ResultMatcher
import java.net.URI

data class RequestConfig(
    val httpMethod: HttpMethod? = null,
    val uri: URI? = null,
    val dto: Any? = null,
    val expects: MutableSet<ResultMatcher> = mutableSetOf(),
    val file: MockMultipartFile? = null,
)
