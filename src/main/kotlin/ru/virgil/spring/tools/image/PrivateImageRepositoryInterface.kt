package ru.virgil.spring.tools.image

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean
import java.util.*

@NoRepositoryBean
interface PrivateImageRepositoryInterface<Image : PrivateImageInterface> : CrudRepository<Image, UUID> {

    fun findByCreatedByAndUuid(createdBy: String, imageUuid: UUID): Image?
}
