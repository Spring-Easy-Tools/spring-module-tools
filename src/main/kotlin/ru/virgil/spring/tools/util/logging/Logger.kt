package ru.virgil.spring.tools.util.logging

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.reflect.KClass

@Suppress("unused")
object Logger {

    fun inject(name: String) = KotlinLogging.logger(name)

    fun inject(anyClass: Class<*>) = inject(anyClass.name)

    fun inject(kotlinClass: KClass<*>) = inject(kotlinClass.java)
}
