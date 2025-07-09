package ru.virgil.spring.tools.file

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import net.datafaker.Faker
import org.springframework.stereotype.Component
import java.awt.Color
import java.io.InputStream
import java.net.URI
import java.net.URL

@Suppress("MemberVisibilityCanBePrivate")
abstract class ImageMockService<FileEntity : PrivateFile>(
    fileService: FileService<FileEntity>,
    properties: FileProperties,
    protected val faker: Faker,
) : FileMockService<FileEntity>(fileService, properties) {

    override fun getDefaultContentUrl(): URL {
        return URI(faker.avatar().image()).toURL()
    }

    override fun createFallbackContent(): InputStream {
        return ImmutableImage.create(256, 256)
            .fill(Color.CYAN)
            .bytes(PngWriter())
            .inputStream()
    }

    override fun getDefaultPartName(): String {
        return properties.defaultFileName
    }
}
