package ru.virgil.spring.tools.security.record

import org.springframework.web.bind.annotation.*
import ru.virgil.spring.tools.security.cors.GlobalCors
//import security.cors.DefaultCorsJava
import java.util.*

// TODO: Убрать за ненужностью?
@GlobalCors
@RestController
@RequestMapping("/auth/record")
class AuthRecordController(private val authRecordService: AuthRecordService) {

    @GetMapping("/{uuid}")
    fun get(@PathVariable uuid: UUID) = authRecordService.getAuthRecord(uuid)

    @PostMapping
    fun post() = authRecordService.createAuthRecord()

    @PutMapping("/{uuid}")
    fun put(@PathVariable uuid: UUID, @RequestBody authRecord: AuthRecord) =
        authRecordService.editAuthRecord(uuid, authRecord)

    @DeleteMapping("/{uuid}")
    fun delete(@PathVariable uuid: UUID) = authRecordService.deleteAuthRecord(uuid)
}
