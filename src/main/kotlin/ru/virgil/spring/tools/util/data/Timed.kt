package ru.virgil.spring.tools.util.data

import java.time.ZonedDateTime

interface Timed {

    val updatedAt: ZonedDateTime
    val createdAt: ZonedDateTime
}
