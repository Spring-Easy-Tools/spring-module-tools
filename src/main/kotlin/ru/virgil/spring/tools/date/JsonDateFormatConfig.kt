package ru.virgil.spring.tools.date

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.time.format.DateTimeFormatter

@Configuration
class JsonDateFormatConfig {

    @Bean
    fun jsonCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer { builder: Jackson2ObjectMapperBuilder ->
            builder.serializers(ZonedDateTimeSerializer(DateTimeFormatter.ISO_ZONED_DATE_TIME))
            builder.serializers(LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        }
    }
}
