package ru.virgil.spring.tools.dto

import java.time.ZonedDateTime

interface TimedDto {

    var createdAt: ZonedDateTime?
    var updatedAt: ZonedDateTime?
}
