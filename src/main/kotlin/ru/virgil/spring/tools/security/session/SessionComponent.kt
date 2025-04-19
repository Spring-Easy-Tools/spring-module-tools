package ru.virgil.spring.tools.security.session

// @Component
// class SessionComponent(
//     private val sessionProperties: SessionProperties,
// ) {
//
//     @Bean
//     fun provideRememberMeServices() = SpringSessionRememberMeServices().also {
//         it.setAlwaysRemember(true)
//     }
//
//     @Bean
//     fun httpSessionIdResolver(): HttpSessionIdResolver = HeaderAndQueryHttpSessionIdResolver(
//         sessionProperties.headerName,
//         if (sessionProperties.enableWebsocketQueryParam) {
//             sessionProperties.queryParamName
//         } else {
//             null
//         }
//     )
//
// }
