package ru.virgil.spring.tools.file

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.util.*

@NoRepositoryBean
interface PrivateFileRepository<FileEntity : PrivateFile> : JpaRepository<FileEntity, UUID> {

    fun findByCreatedByAndUuid(creator: String, fileUuid: UUID): FileEntity?
}
