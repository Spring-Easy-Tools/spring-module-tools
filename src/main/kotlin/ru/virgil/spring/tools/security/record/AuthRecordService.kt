package ru.virgil.spring.tools.security.record

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

private const val recordLiveMinutes: Long = 15

@Service
class AuthRecordService {

    private val authRecords: MutableMap<UUID, AuthRecord> = HashMap()

    fun getAuthRecord(uuid: UUID): AuthRecord? = authRecords[uuid]

    fun createAuthRecord(): AuthRecord {
        val authRecord = AuthRecord()
        authRecords[authRecord.uuid] = authRecord
        return authRecord
    }

    fun editAuthRecord(uuid: UUID, authRecord: AuthRecord): AuthRecord {
        authRecords[uuid] = authRecords[uuid]!!.copy(
            uuid = uuid,
            updatedAt = LocalDateTime.now(),
            credentials = authRecord.credentials,
            principal = authRecord.principal
        )
        return authRecords[uuid]!!
    }

    fun deleteAuthRecord(uuid: UUID) {
        authRecords.remove(uuid)
    }

    @Scheduled(cron = "* */$recordLiveMinutes * * * *")
    private fun cleanupRecords() {
        val timeThreshold = LocalDateTime.now().minusMinutes(recordLiveMinutes)
        val filteredKeys = authRecords.filter { (_, value) -> value.createdAt < timeThreshold }
            .map { it.key }
            .toSet()
        authRecords.keys.removeAll(filteredKeys)
    }
}
