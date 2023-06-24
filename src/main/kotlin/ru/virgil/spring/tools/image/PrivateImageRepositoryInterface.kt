package ru.virgil.spring.tools.image

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@NoRepositoryBean
interface PrivateImageRepositoryInterface<Image : PrivateImageInterface> : CrudRepository<Image, UUID> {

    fun findByCreatedByAndUuid(createdBy: UserDetails, imageUuid: UUID): Optional<Image>
}
