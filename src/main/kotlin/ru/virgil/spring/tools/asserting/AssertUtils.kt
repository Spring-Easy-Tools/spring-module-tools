package ru.virgil.spring.tools.asserting

import tools.jackson.databind.ObjectMapper
import io.kotest.matchers.equality.shouldBeEqualToUsingFields
import org.springframework.stereotype.Component
import kotlin.reflect.full.memberProperties

/**
 * Помогает делать всякие сложные ассерты.
 */
@Component
class AssertUtils(private val objectMapper: ObjectMapper) {

    companion object {

        /**
         * Проверяет, что объект-получатель (receiver) соответствует объекту [partial],
         * учитывая только свойства [partial], у которых значение **не равно null**.
         *
         * Это поверхностное сравнение (shallow comparison), использующее [io.kotest.matchers.equality.shouldBeEqualToUsingFields],
         * фактически выполняя проверку `this shouldBeEqualToUsingFields partial, *partial.properties`.
         *
         * Свойства, существующие только в объекте-получателе, игнорируются.
         *
         * Важно: `null` в [partial] трактуется как «поле не задано» и поэтому **не участвует** в сравнении.
         * Если нужно проверять, что поле должно быть `null`, используйте другой ассерт/подход.
         *
         * [AssertionError] выбрасывается, если какое-либо сравниваемое свойство из [partial] не соответствует свойству
         * в объекте-получателе.
         *
         * Подробные примеры использования смотрите в тестовом классе `ru.virgil.spring.tools.asserting.AssertUtilsTest`.
         */
        infix fun Any.shouldContainAllFieldsFrom(partial: Any) {
            val partialProps = partial::class.memberProperties
                .filter {
                    try {
                        it.call(partial) != null
                    } catch (ignored: Exception) {
                        false
                    }
                }
            this.shouldBeEqualToUsingFields(partial, *partialProps.toTypedArray())
        }

    }
}
