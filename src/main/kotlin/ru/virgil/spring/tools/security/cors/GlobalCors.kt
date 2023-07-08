package ru.virgil.spring.tools.security.cors

import org.springframework.web.bind.annotation.CrossOrigin
import ru.virgil.spring.tools.security.cors.GlobalCors.Companion.originsProperty

@CrossOrigin(
    origins = [originsProperty],
    allowCredentials = true.toString()
)
annotation class GlobalCors {

    companion object {
        const val originsProperty = "\${security.cors.origins}"
    }
}
