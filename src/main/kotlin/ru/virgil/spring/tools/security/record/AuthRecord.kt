package ru.virgil.spring.tools.security.record

import ru.virgil.spring.tools.util.data.Identified
import java.util.*


data class AuthRecord(
    override var uuid: UUID = UUID.randomUUID(),
    val credentials: String? = null,
    val principal: String? = null,
) : Identified
