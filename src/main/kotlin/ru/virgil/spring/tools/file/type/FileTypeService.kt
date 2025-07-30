package ru.virgil.spring.tools.file.type

import org.apache.tika.Tika
import org.apache.tika.mime.MimeType
import org.apache.tika.mime.MimeTypes
import org.springframework.stereotype.Component

@Suppress("RedundantModalityModifier")
@Component
open class FileTypeService : Tika() {

    @Deprecated("перейти полностью на проверку расширения")
    // TODO: аргументы скорее всего не подходят для этого случая, т.к. можно передать пустой список?
    fun checkMimeType(content: ByteArray, vararg allowedMimeTypes: Regex): MimeType {
        return checkMimeType(content, allowedMimeTypes.toList())
    }

    @Deprecated("перейти полностью на проверку расширения")
    // todo: подтянуть обновление со списком расширений вместо регексов
    fun checkMimeType(content: ByteArray, allowedMimeTypes: List<Regex>): MimeType {
        val mimeTypeName = detect(content)
        if (!allowedMimeTypes.any { it.matches(mimeTypeName) }) {
            throw UnsupportedOperationException("File mime type not allowed: $mimeTypeName")
        }
        return MimeTypes.getDefaultMimeTypes().forName(mimeTypeName)
    }

    fun checkExtension(content: ByteArray, vararg allowedExtensions: String): String {
        return checkExtension(content, allowedExtensions.toList())
    }

    /**
     * Расширения можно задавать с точкой или без нее. Для этого создается dot-augmented список.
     * */
    fun checkExtension(content: ByteArray, allowedExtensions: List<String>): String {
        val detectedExtension = getMimeType(content).extension
        val dotAugmentedExtensions = allowedExtensions.filter { it.contains(".").not() }.map { ".$it" }
        if (detectedExtension !in (allowedExtensions + dotAugmentedExtensions)) {
            throw UnsupportedOperationException("File extension not allowed: $detectedExtension")
        }
        return detectedExtension
    }

    @Deprecated(
        "Use getExpectedExtension(content, allowedExtensions: List<String>) instead",
        ReplaceWith("getExpectedExtension(content, fileTypeConfig.allowedExtensions)")
    )
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
        return MimeTypes.getDefaultMimeTypes().forName(mimeTypeName)
    }
}
