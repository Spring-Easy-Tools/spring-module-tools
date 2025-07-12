package ru.virgil.spring.tools.file.type

import org.apache.tika.Tika
import org.apache.tika.mime.MimeType
import org.apache.tika.mime.MimeTypes
import org.springframework.stereotype.Component

@Suppress("RedundantModalityModifier")
@Component
open class FileTypeService : Tika() {

    fun getExpectedExtension(content: ByteArray, fileTypeConfig: FileTypeConfig): String {
        val mimeType = getMimeType(content)
        val extension = mimeType.extension
        if (extension !in fileTypeConfig.allowedExtensions) {
            throw UnsupportedOperationException("File extension not allowed: $extension")
        }
        return extension
    }

    fun getMimeType(content: ByteArray): MimeType {
        val mimeTypeName = detect(content)
        return ALL_MIME_TYPES.forName(mimeTypeName)
    }

    companion object {
        private val ALL_MIME_TYPES = MimeTypes.getDefaultMimeTypes()
    }
}
