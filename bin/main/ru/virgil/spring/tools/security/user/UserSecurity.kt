package ru.virgil.spring.tools.security.user

object UserSecurity {

    fun String?.checkSecretHashed(): String? {
        if (this != null && !Regex("""^\{\w+}""").containsMatchIn(this)) {
            throw SecurityException("Password must be hashed and contain a prefix like {bcrypt}, {noop}, etc.")
        }
        return this
    }
}
