package ru.virgil.spring.tools.util

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object LocalDateTimeUtil {

    // todo: С каждым выходом Spring надо проверять, не исправилось ли. Обещали исправить.
    /**
     * Округляет дату до секунд. Позволяет избежать бага с округлением времени в базе данных.
     *
     * Для этого заменяем [ChronoUnit.MILLIS] на [ChronoUnit.NANOS].
     */
    fun LocalDateTime.databaseTruncate(unit: ChronoUnit = ChronoUnit.MILLIS): LocalDateTime = this.truncatedTo(unit)
}
