package ru.virgil.spring.tools.asserting

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.equality.shouldBeEqualToUsingFields
import org.springframework.stereotype.Component

/**
 * Помогает делать всякие сложные ассерты.
 */
@Component
class AssertUtils(private val objectMapper: ObjectMapper) {

    /**
     * @deprecated Use the top-level infix function instead: `full shouldContainAllFieldsFrom partial`
     */
    @Deprecated(
        "Use top-level infix function: full shouldContainAllFieldsFrom partial",
        ReplaceWith("full.shouldContainAllFieldsFrom(partial)")
    )
    fun partialEquals(full: Any, partial: Any) {
        val fullJsonNode = objectMapper.valueToTree<JsonNode>(full)
        val fullFields = fullJsonNode.toList()
        val partialJsonNode = objectMapper.valueToTree<JsonNode>(partial)
        val partialFields = partialJsonNode.filterNotNull()
            .filter { !it.isNull }
            .toList()
        fullFields shouldContainAll partialFields
    }

    companion object {

        /**
         * Проверяет, что объект-получатель (receiver) соответствует объекту [partial], учитывая только свойства, присутствующие в [partial].
         *
         * Это поверхностное сравнение (shallow comparison), использующее [io.kotest.matchers.equality.shouldBeEqualToUsingFields],
         * фактически выполняя проверку `this shouldBeEqualToUsingFields partial, *partial.properties`.
         *
         * Свойства, существующие только в объекте-получателе, игнорируются. [AssertionError] выбрасывается, если какое-либо свойство из [partial]
         * не соответствует свойству в объекте-получателе, или если [partial] содержит свойства, отсутствующие в объекте-получателе.
         *
         * Подробные примеры использования смотрите в тестовом классе `ru.virgil.spring.tools.asserting.AssertUtilsTest`.
         */
        infix fun Any.shouldContainAllFieldsFrom(partial: Any) {
            val partialProps = partial::class.members
                .filterIsInstance<kotlin.reflect.KProperty<*>>()
            this.shouldBeEqualToUsingFields(partial, *partialProps.toTypedArray())
        }
    }
}
