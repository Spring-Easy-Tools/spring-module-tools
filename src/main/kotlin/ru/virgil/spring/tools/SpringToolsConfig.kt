package ru.virgil.spring.tools

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import ru.virgil.spring.tools.SpringToolsConfig.Companion.BASE_PACKAGE

@Configuration
@ConfigurationPropertiesScan(basePackages = [BASE_PACKAGE])
@EnableScheduling
class SpringToolsConfig {

    companion object {

        const val BASE_PACKAGE = "ru.virgil.spring"
    }
}
