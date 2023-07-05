package ru.virgil.spring.tools.security.cors

import org.springframework.web.bind.annotation.CrossOrigin


@CrossOrigin(
    origins = [ "http://localhost:4200" /*"\${security.cors.origins}"*/],
    allowCredentials = true.toString()
)
annotation class GlobalCors()