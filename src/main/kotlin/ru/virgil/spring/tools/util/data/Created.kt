package ru.virgil.spring.tools.util.data

import ru.virgil.spring.tools.Deprecation
import java.time.ZonedDateTime

@Deprecated(Deprecation.REDUNDANT)
interface Created {

    val createdAt: ZonedDateTime
}
