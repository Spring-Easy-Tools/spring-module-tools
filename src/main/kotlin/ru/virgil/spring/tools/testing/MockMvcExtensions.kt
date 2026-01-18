package ru.virgil.spring.tools.testing

import io.exoquery.fansi.Console.GREEN
import io.exoquery.fansi.Console.RESET
import io.exoquery.pprint
import jakarta.annotation.PostConstruct
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.MockMvcResultHandlersDsl
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.RequestPostProcessor
import ru.virgil.spring.tools.util.logging.Logger
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.node.ArrayNode
import tools.jackson.module.kotlin.convertValue
import tools.jackson.module.kotlin.readValue

@Component
class MockMvcExtensions(private val objectMapper: ObjectMapper) {

    @PostConstruct
    fun configure() {
        MockMvcExtensions.companionObjectMapper = objectMapper
    }

    companion object {

        private val logger = Logger.inject(MockMvcExtensions::class.java)

        @PublishedApi
        internal lateinit var companionObjectMapper: ObjectMapper

        private data class RequestResult(
            val method: String,
            val uri: String,
            val params: Map<String, *>,
            val status: Int,
            val requestContent: Map<*, *>,
            val responseContent: Map<*, *>,
        )

        fun jsonBody(body: Any): RequestPostProcessor = RequestPostProcessor { request ->
            request.contentType = MediaType.APPLICATION_JSON_VALUE
            request.setContent(companionObjectMapper.writeValueAsBytes(body))
            request
        }

        fun MockMvcResultHandlersDsl.printRequest() {
            this.handle { it: MvcResult ->
                val result = extractRequestResult(it)
                val paramsInfo = if (result.params.isNotEmpty()) {
                    result.params.entries.joinToString("&", "?") { "${it.key}=${it.value}" }
                } else ""
                val requestInfo = "$GREEN${result.method}$RESET ${result.uri}$paramsInfo"
                val requestContentInfo = if (result.requestContent.isNotEmpty()) {
                    "Request content: ${pprint(result.requestContent)}"
                } else null
                logger.info {
                    listOf(requestInfo, requestContentInfo)
                        .filterNot { it.isNullOrEmpty() }
                        .joinToString(System.lineSeparator())
                }
            }
        }

        fun MockMvcResultHandlersDsl.printResponse() {
            this.handle { it: MvcResult ->
                val result = extractRequestResult(it)
                val responseInfo = "Response status: $GREEN${result.status}$RESET"
                val responseContentInfo = if (result.responseContent.isNotEmpty()) {
                    "Response content: ${pprint(result.responseContent)}"
                } else null
                logger.info {
                    listOf(responseInfo, responseContentInfo)
                        .filterNot { it.isNullOrEmpty() }
                        .joinToString(System.lineSeparator())
                }
            }
        }

        inline fun <reified T> MvcResult.fromJson(): T {
            val content = this.response.contentAsString
            return companionObjectMapper.readValue(content)
        }

        private fun extractRequestResult(mvcResult: MvcResult): RequestResult {
            return RequestResult(
                mvcResult.request.method ?: "ERROR",
                mvcResult.request.requestURI ?: "ERROR",
                mvcResult.request.parameterMap.mapValues { it.value.joinToString() },
                mvcResult.response.status,
                extractRequestBodyMap(mvcResult),
                extractResponseBodyMap(mvcResult),
            )
        }

        private fun extractRequestBodyMap(mvcResult: MvcResult): Map<*, *> {
            val content = mvcResult.request.contentAsString
            return parseJsonContent(content, mvcResult.request.contentType)
        }

        private fun extractResponseBodyMap(mvcResult: MvcResult): Map<*, *> {
            val content = mvcResult.response.contentAsString
            return parseJsonContent(content, mvcResult.response.contentType)
        }

        private fun parseJsonContent(content: String?, contentType: String?): Map<*, *> {
            return when {
                content.isNullOrEmpty() -> mapOf<String, Any>()
                content.isJson()
                    .not() -> if (contentType != null) mapOf(HttpHeaders.CONTENT_TYPE to contentType) else emptyMap()
                else -> {
                    val jsonNode = companionObjectMapper.readTree(content)
                    if (jsonNode is ArrayNode) {
                        val array: Array<*> = companionObjectMapper.convertValue(jsonNode)
                        array.mapIndexed { index, any -> index to any }.toMap()
                    } else {
                        val map: Map<*, *> = companionObjectMapper.convertValue(jsonNode)
                        map
                    }
                }
            }
        }

        private fun String.isJson(): Boolean {
            try {
                JSONObject(this)
            } catch (_: JSONException) {
                try {
                    JSONArray(this)
                } catch (_: JSONException) {
                    return false
                }
            }
            return true
        }
    }

}

// object MockMvcExtensions {
//
//     private val logger = Logger.inject(MockMvcExtensions::class.java)
//     lateinit var objectMapper: ObjectMapper
//         private set
//
//     fun setObjectMapper(mapper: ObjectMapper) {
//         objectMapper = mapper
//     }
//
//     private data class RequestResult(
//         val method: String,
//         val uri: String,
//         val params: Map<String, *>,
//         val status: Int,
//         val requestContent: Map<*, *>,
//         val responseContent: Map<*, *>,
//     )
//
//     fun jsonBody(body: Any): RequestPostProcessor = RequestPostProcessor { request ->
//         request.contentType = MediaType.APPLICATION_JSON_VALUE
//         request.setContent(objectMapper.writeValueAsBytes(body))
//         request
//     }
//
//     fun MockMvcResultHandlersDsl.printRequest() {
//         this.handle { it: MvcResult ->
//             val result = extractRequestResult(it)
//             val paramsInfo = if (result.params.isNotEmpty()) {
//                 result.params.entries.joinToString("&", "?") { "${it.key}=${it.value}" }
//             } else ""
//             val requestInfo = "$GREEN${result.method}$RESET ${result.uri}$paramsInfo"
//             val requestContentInfo = if (result.requestContent.isNotEmpty()) {
//                 "Request content: ${pprint(result.requestContent)}"
//             } else null
//             logger.info {
//                 listOf(requestInfo, requestContentInfo)
//                     .filterNot { it.isNullOrEmpty() }
//                     .joinToString(System.lineSeparator())
//             }
//         }
//     }
//
//     fun MockMvcResultHandlersDsl.printResponse() {
//         this.handle { it: MvcResult ->
//             val result = extractRequestResult(it)
//             val responseInfo = "Response status: $GREEN${result.status}$RESET"
//             val responseContentInfo = if (result.responseContent.isNotEmpty()) {
//                 "Response content: ${pprint(result.responseContent)}"
//             } else null
//             logger.info {
//                 listOf(responseInfo, responseContentInfo)
//                     .filterNot { it.isNullOrEmpty() }
//                     .joinToString(System.lineSeparator())
//             }
//         }
//     }
//
//     inline fun <reified T> MvcResult.fromJson(): T {
//         val content = this.response.contentAsString
//         return objectMapper.readValue(content)
//     }
//
//     private fun extractRequestResult(mvcResult: MvcResult): RequestResult {
//         return RequestResult(
//             mvcResult.request.method ?: "ERROR",
//             mvcResult.request.requestURI ?: "ERROR",
//             mvcResult.request.parameterMap.mapValues { it.value.joinToString() },
//             mvcResult.response.status,
//             extractRequestBodyMap(mvcResult),
//             extractResponseBodyMap(mvcResult),
//         )
//     }
//
//     private fun extractRequestBodyMap(mvcResult: MvcResult): Map<*, *> {
//         val content = mvcResult.request.contentAsString
//         return parseJsonContent(content, mvcResult.request.contentType)
//     }
//
//     private fun extractResponseBodyMap(mvcResult: MvcResult): Map<*, *> {
//         val content = mvcResult.response.contentAsString
//         return parseJsonContent(content, mvcResult.response.contentType)
//     }
//
//     private fun parseJsonContent(content: String?, contentType: String?): Map<*, *> {
//         return when {
//             content.isNullOrEmpty() -> mapOf<String, Any>()
//             content.isJson()
//                 .not() -> if (contentType != null) mapOf(HttpHeaders.CONTENT_TYPE to contentType) else emptyMap()
//             else -> {
//                 val jsonNode = objectMapper.readTree(content)
//                 if (jsonNode is ArrayNode) {
//                     val array: Array<*> = objectMapper.convertValue(jsonNode)
//                     array.mapIndexed { index, any -> index to any }.toMap()
//                 } else {
//                     val map: Map<*, *> = objectMapper.convertValue(jsonNode)
//                     map
//                 }
//             }
//         }
//     }
//
//     private fun String.isJson(): Boolean {
//         try {
//             JSONObject(this)
//         } catch (_: JSONException) {
//             try {
//                 JSONArray(this)
//             } catch (_: JSONException) {
//                 return false
//             }
//         }
//         return true
//     }
// }
