package ru.virgil.spring.tools.image

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.util.*

@NoRepositoryBean
interface PrivateImageRepositoryInterface<Image : PrivateImageInterface> : JpaRepository<Image, UUID> {

    fun findByCreatedByAndUuid(createdBy: String, imageUuid: UUID): Image?
}
