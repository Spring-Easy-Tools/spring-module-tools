package ru.virgil.spring.tools.file

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@NoRepositoryBean
interface PrivateFileRepository<FileEntity : PrivateFile> : CrudRepository<FileEntity, UUID> {

    fun findByCreatedByAndUuid(createdBy: UserDetails, fileUuid: UUID): Optional<FileEntity>
}
