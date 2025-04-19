package ru.virgil.spring.tools.security.record

import ru.virgil.spring.tools.Deprecation
import ru.virgil.spring.tools.util.data.Created
import ru.virgil.spring.tools.util.data.Identified
import ru.virgil.spring.tools.util.data.Updated
import java.time.ZonedDateTime
import java.util.*


@Deprecated(Deprecation.NEW_NATIVE_AUTH)
data class AuthRecord(
    override val uuid: UUID = UUID.randomUUID(),
    override val createdAt: ZonedDateTime = ZonedDateTime.now(),
    override val updatedAt: ZonedDateTime = ZonedDateTime.now(),
    val credentials: String? = null,
    val principal: String? = null,
) : Identified, Created, Updated
