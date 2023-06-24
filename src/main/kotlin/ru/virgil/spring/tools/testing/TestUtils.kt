package ru.virgil.spring.tools.testing

import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.MvcResult
import java.util.*
import java.util.stream.Collectors
import kotlin.math.min

private const val ERROR_VALUE = "ERROR"
private const val NONE_VALUE = "NONE"
private const val JSON_OUTPUT_LENGTH = 2048

@Component
class TestUtils(protected val objectMapper: ObjectMapper) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun printResponse(mvcResult: MvcResult) {
        val method = mvcResult.request.method ?: ERROR_VALUE
        val requestURI = mvcResult.request.requestURI ?: ERROR_VALUE
        val requestParams = extractPrettyParams(mvcResult)
        val status = mvcResult.response.status
        val requestContent = extractPrettyRequest(mvcResult)
        val responseContent = extractPrettyResponse(mvcResult)
        logger.info(buildTestResults(method, requestURI, requestParams, status, requestContent, responseContent))
    }

    private fun extractPrettyRequest(mvcResult: MvcResult): String =
        Optional.ofNullable(mvcResult.request.contentAsString)
            .map { makeJsonPretty(it) }
            .orElse(NONE_VALUE)

    protected fun makeJsonPretty(requestContent: String): String {
        var prettyRequestContent = objectMapper.readTree(requestContent).toPrettyString()
        prettyRequestContent = shortenJson(prettyRequestContent)
        return prettyRequestContent
    }

    protected fun extractPrettyResponse(mvcResult: MvcResult): String {
        var responseContent = mvcResult.response.contentAsString
        return when {
            !responseContent.isJson() -> "BODY: content-type -> ${mvcResult.response.contentType}"
            else -> {
                responseContent = objectMapper.readTree(responseContent).toPrettyString()
                responseContent = shortenJson(responseContent)
                responseContent.ifEmpty { NONE_VALUE }
            }
        }
    }

    protected fun shortenJson(requestContent: String): String {
        var shortedRequestContent = requestContent.substring(0, min(JSON_OUTPUT_LENGTH, requestContent.length))
        if (shortedRequestContent.length == JSON_OUTPUT_LENGTH) {
            shortedRequestContent += buildShortenMessage()
        }
        return shortedRequestContent
    }

    protected fun extractPrettyParams(mvcResult: MvcResult): String {
        val parameterMap = mvcResult.request.parameterMap
        var stringParams = parameterMap.keys.stream()
            .map { "$it=${java.lang.String.join(",", *parameterMap[it])}" }
            .collect(Collectors.joining("&", "?", ""))
        if (stringParams == "?") {
            stringParams = ""
        }
        return stringParams
    }

}

private fun buildShortenMessage() = """
...
                    
/// JSON SHORTENED TO $JSON_OUTPUT_LENGTH SYMBOLS ///
                                      
"""

private fun buildTestResults(
    method: String, requestURI: String, requestParams: String, status: Int, requestContent: String,
    responseContent: String,
) = """
                                                    
/// TEST RESULTS ///
            
REQUEST: 
$method $requestURI$requestParams -> $status
            
REQUEST JSON: 
$requestContent
            
RESPONSE JSON: 
$responseContent

"""

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
