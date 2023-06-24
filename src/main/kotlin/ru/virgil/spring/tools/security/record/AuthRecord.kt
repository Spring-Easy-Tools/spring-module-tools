package ru.virgil.spring.tools.security.record

import ru.virgil.spring.tools.util.data.Created
import ru.virgil.spring.tools.util.data.Identified
import ru.virgil.spring.tools.util.data.Updated
import java.time.LocalDateTime
import java.util.*


data class AuthRecord(
    override val uuid: UUID = UUID.randomUUID(),
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override val updatedAt: LocalDateTime = LocalDateTime.now(),
    val credentials: String? = null,
    val principal: String? = null,
) : Identified, Created, Updated
