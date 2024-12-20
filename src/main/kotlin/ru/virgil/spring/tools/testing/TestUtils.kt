package ru.virgil.spring.tools.testing

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.convertValue
import io.exoquery.fansi.Console.GREEN
import io.exoquery.fansi.Console.RESET
import io.exoquery.pprint
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.MvcResult
import ru.virgil.spring.tools.util.logging.Logger

private const val ERROR_VALUE = "ERROR"

@Component
class TestUtils(private val objectMapper: ObjectMapper) {

    private val logger = Logger.inject(this::class.java)

    private data class RequestResult(
        val method: String,
        val uri: String,
        val params: Map<String, *>,
        val status: Int,
        val requestContent: Map<*, *>,
        val responseContent: Map<*, *>,
    )

    fun printResponse(mvcResult: MvcResult) {
        val result = RequestResult(
            mvcResult.request.method ?: ERROR_VALUE,
            mvcResult.request.requestURI ?: ERROR_VALUE,
            mvcResult.request.parameterMap.mapValues { it.value.joinToString() },
            mvcResult.response.status,
            extractRequestBodyMap(mvcResult),
            extractResponseBodyMap(mvcResult),
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
    }

    private fun extractRequestBodyMap(mvcResult: MvcResult): Map<*, *> {
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

    private fun extractResponseBodyMap(mvcResult: MvcResult): Map<*, *> {
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
