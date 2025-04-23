package ru.virgil.spring.tools.testing.fluent.request

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.util.MimeType
import org.springframework.util.MimeTypeUtils
import ru.virgil.spring.tools.testing.TestUtils
import java.net.URI

class Requester(
    val objectMapper: ObjectMapper,
    val mockMvc: MockMvc,
    val testUtils: TestUtils,
    var config: RequestConfig = RequestConfig(),
) {

    fun get(function: Requester.() -> String) = setConnectionOptions(function(this), HttpMethod.GET)

    fun post(function: Requester.() -> String) = setConnectionOptions(function(this), HttpMethod.POST)

    fun put(function: Requester.() -> String) = setConnectionOptions(function(this), HttpMethod.PUT)

    fun delete(function: Requester.() -> String) = setConnectionOptions(function(this), HttpMethod.DELETE)

    private fun setConnectionOptions(uri: String, httpMethod: HttpMethod) {
        config = config.copy(httpMethod = httpMethod, uri = URI.create(uri))
    }

    fun send(function: Requester.() -> Any) {
        config = config.copy(dto = function(this))
    }

    fun expect(function: Requester.() -> ResultMatcher) {
        config.expects.add(function(this))
    }

    fun file(function: Requester.() -> MockMultipartFile) {
        config = config.copy(file = function(this))
    }

    inline fun <reified T> makeRequest(): T {
        val config = config.copy()
        val requestBuilder = when {
            config.httpMethod == null || config.uri == null ->
                throw Exception("Нужно указать ${HttpMethod::class.simpleName} и ${URI::class.simpleName}")

            config.file != null && config.dto != null ->
                throw Exception("Нужно отправлять что-то одно: DTO или ${MockMultipartFile::class.simpleName}")

            config.file != null -> MockMvcRequestBuilders.multipart(config.httpMethod, config.uri)
                .file(config.file)

            config.dto != null -> MockMvcRequestBuilders.request(config.httpMethod, config.uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(config.dto))

            else -> MockMvcRequestBuilders.request(config.httpMethod, config.uri)
        }
        if (config.expects.isEmpty()) {
            config.expects.add(status().isOk)
        }
        val result = mockMvc.perform(requestBuilder.with(csrf()))
            .andExpectAll(*config.expects.toTypedArray())
            .andDo { mvcResult: MvcResult -> testUtils.printResponse(mvcResult) }
            .andReturn()
        val mimeType = MimeTypeUtils.parseMimeType(result.response.contentType ?: MimeTypeUtils.TEXT_PLAIN_VALUE)
        return when {
            isJson(mimeType, result) -> objectMapper.readValue(result.response.contentAsString)
            isImage(mimeType) -> result.response.contentAsByteArray as T
            else -> result as T
        }
    }

    fun isImage(mimeType: MimeType) =
        mimeType.isPresentIn(listOf(MimeTypeUtils.IMAGE_GIF, MimeTypeUtils.IMAGE_JPEG, MimeTypeUtils.IMAGE_PNG))

    fun isJson(mimeType: MimeType, result: MvcResult) =
        mimeType.isPresentIn(listOf(MimeTypeUtils.APPLICATION_JSON)) && result.response.contentAsString.isNotBlank()
}
