package ru.virgil.spring.tools.util.logging

import io.github.oshai.kotlinlogging.KotlinLogging

object Logger {

    fun inject(name: String) = KotlinLogging.logger(name)

    fun inject(anyClass: Class<*>) = KotlinLogging.logger(anyClass.name)
}
