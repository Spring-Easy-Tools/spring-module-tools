package ru.virgil.spring.tools.entity

import java.time.ZonedDateTime

interface Timed {

    val updatedAt: ZonedDateTime
    val createdAt: ZonedDateTime
}
