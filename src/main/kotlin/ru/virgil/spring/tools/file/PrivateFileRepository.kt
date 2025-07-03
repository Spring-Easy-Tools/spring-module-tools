package ru.virgil.spring.tools.file

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@NoRepositoryBean
interface PrivateFileRepository<File : PrivateFile> : CrudRepository<File, UUID> {

    fun findByCreatedByAndUuid(createdBy: UserDetails, imageUuid: UUID): Optional<File>
}
