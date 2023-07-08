package ru.virgil.spring.tools.security.cors

import org.springframework.web.bind.annotation.CrossOrigin

// TODO: попробовать перечень -> запятые

@CrossOrigin(
    origins = ["\${security.cors.origins}"],
    allowCredentials = true.toString()
)
annotation class GlobalCors()
