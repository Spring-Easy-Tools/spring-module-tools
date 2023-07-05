package ru.virgil.spring.tools

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

//@SpringBootApplication
//@ConfigurationPropertiesScan
// TODO: Удалить?
class SpringToolsApplication

fun main(args: Array<String>) {
    runApplication<SpringToolsApplication>(*args)
}
