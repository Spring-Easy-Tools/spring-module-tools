package ru.virgil.spring.tools

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

const val toolsBasePackage = "ru.virgil.spring"

@Configuration
@ConfigurationPropertiesScan(basePackages = [toolsBasePackage])
@EnableScheduling
class SpringToolsConfig
