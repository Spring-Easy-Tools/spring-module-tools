package ru.virgil.spring.tools.testing

import io.exoquery.fansi.Console.GREEN
import io.exoquery.fansi.Console.RESET
import io.exoquery.pprint
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActionsDsl
import ru.virgil.spring.tools.util.logging.Logger
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.node.ArrayNode
import tools.jackson.module.kotlin.convertValue
import tools.jackson.module.kotlin.readValue

object MockMvcExtensions {

    private val logger = Logger.inject(MockMvcExtensions::class.java)

    private data class RequestResult(
        val method: String,
        val uri: String,
        val params: Map<String, *>,
        val status: Int,
        val requestContent: Map<*, *>,
        val responseContent: Map<*, *>,
    )

    fun MockHttpServletRequestDsl.jsonBody(body: Any, objectMapper: ObjectMapper) {
        contentType = MediaType.APPLICATION_JSON
        content = objectMapper.writeValueAsString(body)
    }

    inline fun <reified T> ResultActionsDsl.readResponse(objectMapper: ObjectMapper): T {
        val content = andReturn().response.contentAsString
        return objectMapper.readValue(content)
    }

    fun ResultActionsDsl.printResponse(objectMapper: ObjectMapper): ResultActionsDsl {
        val mvcResult = andReturn()
        val result = RequestResult(
            mvcResult.request.method ?: "ERROR",
            mvcResult.request.requestURI ?: "ERROR",
            mvcResult.request.parameterMap.mapValues { it.value.joinToString() },
            mvcResult.response.status,
            extractRequestBodyMap(mvcResult, objectMapper),
            extractResponseBodyMap(mvcResult, objectMapper),
        )
        val paramsInfo = if (result.params.isNotEmpty()) {
            result.params.entries.joinToString("&", "?") { "${it.key}=${it.value}" }
        } else ""
        val requestInfo = "$GREEN${result.method}$RESET ${result.uri}$paramsInfo -> $GREEN${result.status}$RESET"
        val requestContentInfo = if (result.requestContent.isNotEmpty()) {
            "Request content: ${pprint(result.requestContent)}"
        } else null
        val responseContentInfo = if (result.responseContent.isNotEmpty()) {
            "Response content: ${pprint(result.responseContent)}"
        } else null
        logger.info {
            listOf(requestInfo, requestContentInfo, responseContentInfo)
                .filterNot { it.isNullOrEmpty() }
                .joinToString(System.lineSeparator())
        }
        return this
    }

    private fun extractRequestBodyMap(mvcResult: MvcResult, objectMapper: ObjectMapper): Map<*, *> {
        val responseContent = mvcResult.request.contentAsString
        return when {
            responseContent.isNullOrEmpty() -> mapOf<String, Any>()
            responseContent.isJson().not() -> mapOf(HttpHeaders.CONTENT_TYPE to mvcResult.response.contentType)
            else -> {
                val jsonNode = objectMapper.readTree(responseContent)
                if (jsonNode is ArrayNode) {
                    val array: Array<*> = objectMapper.convertValue(jsonNode)
                    array.mapIndexed { index, any -> index to any }.toMap()
                } else {
                    val map: Map<*, *> = objectMapper.convertValue(jsonNode)
                    map
                }
            }
        }
    }

    private fun extractResponseBodyMap(mvcResult: MvcResult, objectMapper: ObjectMapper): Map<*, *> {
        val responseContent = mvcResult.response.contentAsString
        return when {
            responseContent.isJson().not() -> mapOf(HttpHeaders.CONTENT_TYPE to mvcResult.response.contentType)
            else -> {
                val jsonNode = objectMapper.readTree(responseContent)
                if (jsonNode is ArrayNode) {
                    val array: Array<*> = objectMapper.convertValue(jsonNode)
                    array.mapIndexed { index, any -> index to any }.toMap()
                } else {
                    val map: Map<*, *> = objectMapper.convertValue(jsonNode)
                    map
                }
            }
        }
    }

    private fun String.isJson(): Boolean {
        try {
            JSONObject(this)
        } catch (ex: JSONException) {
            try {
                JSONArray(this)
            } catch (ex1: JSONException) {
                return false
            }
        }
        return true
    }
}
