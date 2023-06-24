package ru.virgil.spring.tools.security.oauth


import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.virgil.spring.tools.security.cors.GlobalCors
import ru.virgil.spring.tools.security.oauth.firebase.FirebaseService
import ru.virgil.spring.tools.security.token.AuthenticatedToken

@GlobalCors
@RequestMapping("/oauth")
@RestController
class OAuthController(val firebaseService: FirebaseService) {

    @GetMapping("/firebase")
    fun register(authenticationToken: AuthenticatedToken) {
        // todo: проводить регистрацию именно тут, а не в конвертере. Конвертер должен передавать чистый токен
        // firebaseService.register(authenticationToken.jwt)
        // todo: или проводить авторизацию в конвертере?
        // return "Success Firebase UAuth"
    }
}
