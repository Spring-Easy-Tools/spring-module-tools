package ru.virgil.spring.tools.file

import net.datafaker.Faker
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.nio.charset.StandardCharsets

@Suppress("MemberVisibilityCanBePrivate")
abstract class DocumentMockService<FileEntity : PrivateFile>(
    fileService: FileService<FileEntity>,
    properties: FileProperties,
    protected val faker: Faker,
) : FileMockService<FileEntity>(fileService, properties) {

    override fun getDefaultContentUrl(): URL {
        return URI("https://en.wikipedia.org/wiki/Help:Download_as_PDF").toURL()
    }

    override fun createFallbackContent(): InputStream {
        val fallbackContent = """
            # Mock Document

            This is a fallback document created for testing purposes.

            ## Lorem Ipsum

            ${faker.lorem().paragraph(5)}

            ## Random Facts

            - ${faker.chuckNorris().fact()}
            - ${faker.hobbit().quote()}
            - ${faker.shakespeare().romeoAndJulietQuote()}

            Generated at: ${java.time.LocalDateTime.now()}
        """.trimIndent()
        return ByteArrayInputStream(fallbackContent.toByteArray(StandardCharsets.UTF_8))
    }

    override fun getDefaultPartName(): String {
        return  properties.defaultFileName
    }

    /**
     * Создать MockMultipartFile по ссылке на текстовый файл
     */
    fun mockAsMultipart(textPath: String): org.springframework.mock.web.MockMultipartFile {
        return try {
            val textUrl = URI(textPath).toURL()
            mockAsMultipart(textUrl, textUrl.file)
        } catch (e: Exception) {
            logger.error("Failed to create MockMultipartFile from URL: $textPath", e)
            mockAsMultipart()
        }
    }

    /**
     * Создать MockMultipartFile с простым текстовым контентом
     */
    fun mockAsMultipart(textContent: String, fileName: String): org.springframework.mock.web.MockMultipartFile {
        val inputStream = ByteArrayInputStream(textContent.toByteArray(StandardCharsets.UTF_8))
        return mockAsMultipart(inputStream, fileName)
    }
}
