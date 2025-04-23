package ru.virgil.spring.tools.security.user

import jakarta.annotation.PostConstruct
import jdk.jfr.Experimental
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.UserDetailsManager
import ru.virgil.spring.tools.security.Security
import ru.virgil.spring.tools.util.Http.orNotFound
import ru.virgil.spring.tools.util.Http.thenConflict
import kotlin.jvm.optionals.getOrNull

@Experimental
abstract class JpaUserDetailsManager(
    protected val repository: JpaRepository<UserDetails, String>,
    protected val defaultUserProperties: DefaultUserProperties?,
    protected val passwordEncoder: PasswordEncoder,
) : UserDetailsManager {

    @PostConstruct
    fun initDefaultUser() {
        if (defaultUserProperties == null) return
        if (!userExists(defaultUserProperties.name!!)) {
            createUser(mapPropertiesToUsed(defaultUserProperties))
        }
    }

    abstract fun mapPropertiesToUsed(defaultUserProperties: DefaultUserProperties): UserDetails

    override fun createUser(user: UserDetails) {
        repository.findByIdOrNull(user.username).thenConflict()
        repository.save(user)
    }

    override fun updateUser(user: UserDetails) {
        repository.findByIdOrNull(user.username).orNotFound()
        repository.save(user)
    }

    override fun deleteUser(username: String) = repository.deleteById(username)

    override fun changePassword(oldPassword: String, newPassword: String) {
        val principal = Security.getAuthentication().principal as UserDetails
        if (!passwordEncoder.matches(oldPassword, principal.password)) {
            throw SecurityException("Old password is incorrect")
        }
        if (passwordEncoder.matches(newPassword, principal.password)) {
            throw SecurityException("New password must be different from the old password")
        }
        applyNewPassword(principal, passwordEncoder.encode(newPassword))
        updateUser(principal)
    }

    abstract fun applyNewPassword(principal: UserDetails, encodedNewPassword: String)

    override fun userExists(username: String) = repository.existsById(username)

    override fun loadUserByUsername(username: String) = repository.findById(username).getOrNull()
}
