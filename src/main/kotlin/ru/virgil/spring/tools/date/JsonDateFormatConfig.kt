package ru.virgil.spring.tools.date

// NOTE: This configuration has been temporarily disabled during Spring Boot 4 upgrade.
// Spring Boot 4 with Jackson 3 auto-configures JSR-310 date/time serialization using ISO-8601 format by default.
// The explicit serializers (LocalDateTimeSerializer, ZonedDateTimeSerializer, etc.) are not directly available
// in Jackson 3 in the same way as Jackson 2.
//
// If custom date/time formatting is still needed, consider:
// 1. Using Spring Boot application properties: spring.jackson.date-format, spring.jackson.serialization.*
// 2. Creating a custom ObjectMapper bean with Jackson 3 API
// 3. Using @JsonFormat annotations on specific fields
//
// Original Jackson 2 code (commented out):
/*
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.time.format.DateTimeFormatter

@Configuration
class JsonDateFormatConfig {

    @Bean
    fun jsonCustomizer() = Jackson2ObjectMapperBuilderCustomizer { builder: Jackson2ObjectMapperBuilder ->
        builder.serializers(ZonedDateTimeSerializer(DateTimeFormatter.ISO_ZONED_DATE_TIME))
        builder.serializers(LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        builder.serializers(LocalTimeSerializer(DateTimeFormatter.ISO_LOCAL_TIME))
    }
}
*/
