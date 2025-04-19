package ru.virgil.spring.tools.security.cors

import org.springframework.web.bind.annotation.CrossOrigin
import ru.virgil.spring.tools.security.cors.GlobalCors.Companion.allowCredentialsProperty
import ru.virgil.spring.tools.security.cors.GlobalCors.Companion.exposedHeadersProperty
import ru.virgil.spring.tools.security.cors.GlobalCors.Companion.originsProperty

@CrossOrigin(
    origins = [originsProperty],
    allowCredentials = allowCredentialsProperty,
    exposedHeaders = [exposedHeadersProperty],
)
@Retention(AnnotationRetention.RUNTIME)
annotation class GlobalCors {

    companion object {

        const val originsProperty = "\${security.cors.origins}"
        const val exposedHeadersProperty = "\${security.cors.exposed-headers}"
        const val allowCredentialsProperty = "\${security.cors.allow-credentials}"
    }
}
