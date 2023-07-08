package ru.virgil.spring.tools

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationPropertiesScan(basePackages = [toolsBasePackage])
class PropertiesConfig
