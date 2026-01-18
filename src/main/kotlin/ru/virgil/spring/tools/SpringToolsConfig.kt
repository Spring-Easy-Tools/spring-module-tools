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

        /**
         * TODO: Переделать на AutoConfiguration.
         * Сейчас используется scanBasePackages, что требует от всех проектов находиться в пакете ru.virgil.spring.
         * Для создания полноценной библиотеки нужно:
         * 1. Создать файл src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
         * 2. Перечислить в нем конфигурационные классы (WebSocketConfig, CorsComponent, SpringToolsConfig).
         * 3. Убрать scanBasePackages из клиентских приложений.
         */
        const val BASE_PACKAGE = "ru.virgil.spring"
    }
}
