package ru.virgil.spring.tools.testing.fluent

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.MockMvc
import ru.virgil.spring.tools.testing.TestUtils
import ru.virgil.spring.tools.testing.fluent.request.RequestDsl
import ru.virgil.spring.tools.testing.fluent.request.Requester

@RequestDsl
@Component
class Fluent(
    private val objectMapper: ObjectMapper,
    private val mockMvcProvider: ObjectProvider<MockMvc>,
    private val testUtils: TestUtils,
) {

    @PublishedApi
    internal val objectMapperAccess = objectMapper

    @PublishedApi
    internal val mockMvcAccess by lazy { mockMvcProvider.getObject() }

    @PublishedApi
    internal val testUtilsAccess = testUtils

    final inline fun <reified T> request(function: Requester.() -> Unit): T {
        val requester = Requester(objectMapperAccess, mockMvcAccess, testUtilsAccess)
        function.invoke(requester)
        return requester.makeRequest()
    }

}
