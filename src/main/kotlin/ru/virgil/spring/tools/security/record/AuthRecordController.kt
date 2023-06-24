package ru.virgil.spring.tools.security.record

import org.springframework.web.bind.annotation.*
import java.util.*

@CrossOrigin(
    origins = ["http://localhost:4200/"],
    allowCredentials = true.toString()
)
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
