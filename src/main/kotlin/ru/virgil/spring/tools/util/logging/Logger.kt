package ru.virgil.spring.tools.util.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Logger {

    fun inject(name: String): Logger = LoggerFactory.getLogger(name)

    fun inject(java: Class<*>): Logger = LoggerFactory.getLogger(java)
}
