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
class TestUtils(protected val objectMapper: ObjectMapper) {

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
            mvcResult.request.parameterMap,
            mvcResult.response.status,
            extractRequestBodyMap(mvcResult),
            extractResponseBodyMap(mvcResult),
        )
        val coloredRequest: String =
            GREEN + result.method + RESET + " ${result.uri} -> " + GREEN + result.status + RESET
        logger.info { coloredRequest }
        if (result.params.isNotEmpty()) {
            logger.info { "HTTP params: ${pprint(result.params)}" }
        }
        if (result.requestContent.isNotEmpty()) {
            logger.info { "Request content: ${pprint(result.requestContent)}" }
        }
        if (result.responseContent.isNotEmpty()) {
            logger.info { "Response content: ${pprint(result.responseContent)}" }
        }
    }

    protected fun extractRequestBodyMap(mvcResult: MvcResult): Map<*, *> {
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

    protected fun extractResponseBodyMap(mvcResult: MvcResult): Map<*, *> {
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
