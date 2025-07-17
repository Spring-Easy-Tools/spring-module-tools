package ru.virgil.spring.tools.file.type

import org.apache.tika.Tika
import org.apache.tika.mime.MimeType
import org.apache.tika.mime.MimeTypes
import org.springframework.stereotype.Component

@Suppress("RedundantModalityModifier")
@Component
open class FileTypeService : Tika() {

    fun checkMimeType(content: ByteArray, vararg allowedMimeTypes: Regex): MimeType {
        return checkMimeType(content, allowedMimeTypes.toList())
    }

    fun checkMimeType(content: ByteArray, allowedMimeTypes: List<Regex>): MimeType {
        val mimeTypeName = detect(content)
        if (!mimeTypeName.matchesAny(allowedMimeTypes)) {
            throw UnsupportedOperationException("File mime type not allowed: $mimeTypeName")
        }
        return ALL_MIME_TYPES.forName(mimeTypeName)
    }

    // todo: подтянуть обновление со списком расширений вместо регексов
    @Deprecated("Use checkMimeType instead", ReplaceWith("checkMimeType(content, fileTypeConfig.allowedMimeTypeRegexes)"))
    fun getMimeType(content: ByteArray, fileTypeConfig: FileTypeConfig): MimeType {
        val mimeTypeName = detect(content)
        if (!mimeTypeName.matchesAny(fileTypeConfig.allowedMimeTypeRegexes)) {
            throw UnsupportedOperationException("File mime type not allowed: $mimeTypeName")
        }
        return ALL_MIME_TYPES.forName(mimeTypeName)
    }

    private fun String.matchesAny(regexes: Collection<Regex>): Boolean {
        regexes.forEach {
            if (this.matches(regex = it)) {
                return true
            }
        }
        return false
    }

    companion object {

        private val ALL_MIME_TYPES = MimeTypes.getDefaultMimeTypes()
    }
}
