package ru.virgil.spring.tools.file.type

import org.apache.tika.Tika
import org.apache.tika.mime.MimeType
import org.apache.tika.mime.MimeTypes
import org.springframework.stereotype.Component

@Suppress("RedundantModalityModifier")
@Component
open class FileTypeService : Tika() {

    fun checkExtension(content: ByteArray, vararg allowedExtensions: String): String {
        return checkExtension(content, allowedExtensions.toList())
    }

    /**
     * Extensions can be specified with or without a dot. A dot-augmented list is created for this purpose.
     */
    fun checkExtension(content: ByteArray, allowedExtensions: List<String>): String {
        val detectedExtension = getMimeType(content).extension
        val dotAugmentedExtensions = allowedExtensions.filter { it.contains(".").not() }.map { ".$it" }
        if (detectedExtension !in (allowedExtensions + dotAugmentedExtensions)) {
            throw UnsupportedOperationException("File extension not allowed: $detectedExtension")
        }
        return detectedExtension
    }

    fun getMimeType(content: ByteArray): MimeType {
        val mimeTypeName = detect(content)
        return MimeTypes.getDefaultMimeTypes().forName(mimeTypeName)
    }
}
