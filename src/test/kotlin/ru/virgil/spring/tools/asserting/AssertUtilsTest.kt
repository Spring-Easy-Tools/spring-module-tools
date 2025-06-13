package ru.virgil.spring.tools.asserting

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import ru.virgil.spring.tools.asserting.AssertUtils.Companion.shouldContainAllFieldsFrom

/**
 * Этот класс демонстрирует юнит-тестирование с использованием [Kotest](https://kotest.io/), гибкого и многофункционального фреймворка для Kotlin.
 *
 * Kotest предоставляет разработчикам Kotlin ряд преимуществ и инструментов для написания тестов:
 * - **Разнообразные стили написания тестов (Specs)**: Kotest поддерживает множество "спецификаций" или стилей,
 *   таких как `StringSpec` (используется здесь, тесты как строки), `FunSpec` (тесты как функции), `BehaviorSpec` (BDD-стиль) и другие.
 *   Это позволяет выбрать наиболее подходящий и читаемый формат для ваших тестов.
 *   Подробнее: [Kotest Test Styles](https://kotest.io/docs/framework/styles.html)
 * - **Мощные и выразительные матчеры (Assertions)**: Вместо стандартных ассертов JUnit, Kotest предлагает богатую библиотеку
 *   интуитивно понятных матчеров, которые делают проверки более читаемыми (например, `shouldBe`, `shouldHaveSize`, `shouldThrow`).
 *   Подробнее: [Kotest Assertions](https://kotest.io/docs/assertions/assertions.html)
 * - **Data-Driven Testing**: Легкость в написании тестов, которые выполняются с различными наборами входных данных.
 *   Подробнее: [Kotest Data Testing](https://kotest.io/docs/framework/datatesting.html)
 * - **Property-Based Testing**: Возможность автоматически генерировать тестовые данные для проверки свойств системы в широком диапазоне сценариев.
 *   Подробнее: [Kotest Property Testing](https://kotest.io/docs/proptest/property-based-testing.html)
 * - **Простота интеграции и расширяемость**: Kotest легко интегрируется с JUnit Platform (что позволяет запускать его стандартными средствами)
 *   и предоставляет возможности для создания собственных матчеров и расширений.
 *
 * В данном конкретном файле `StringSpec` используется для лаконичного определения тестовых случаев для функции `shouldContainAllFieldsFrom`.
 * Этот подход отличается от интеграционных тестов Spring (`@SpringBootTest`), фокусируясь на изолированной проверке конкретной функции,
 * что обеспечивает быстрое выполнение и точную локализацию возможных проблем.
 */
@Suppress("unused")
class AssertUtilsTest : StringSpec({

    data class User(val name: String, val age: Int, val id: String)

    "should pass when all fields in partial match full" {
        val full = User("John", 30, "123")
        val partial = User("John", 30, "123")
        shouldNotThrowAny {
            full shouldContainAllFieldsFrom partial
        }
    }

    "should pass when only some fields in partial are set and match full" {
        val full = User("John", 30, "123")
        val partial = User("John", 30, "")
        shouldThrowAny {
            full shouldContainAllFieldsFrom partial
        }
    }

    "should fail when a field in partial does not match full" {
        val full = User("John", 30, "123")
        val partial = User("Jane", 30, "123")
        shouldThrowAny {
            full shouldContainAllFieldsFrom partial
        }
    }
})
