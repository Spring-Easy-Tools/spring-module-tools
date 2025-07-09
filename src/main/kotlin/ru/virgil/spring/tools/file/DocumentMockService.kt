package ru.virgil.spring.tools.file

import net.datafaker.Faker
import org.springframework.stereotype.Component
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
        // Получаем случайную статью из Википедии
        val randomArticle = faker.educator().course().replace(" ", "_")
        return URI("https://en.wikipedia.org/wiki/$randomArticle").toURL()
    }

    override fun createFallbackContent(): InputStream {
        // Создаем простой текстовый документ как fallback
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
        return properties.defaultFileName.let { fileName ->
            // Меняем расширение на .txt если это изображение
            if (fileName.contains(".")) {
                val nameWithoutExtension = fileName.substringBeforeLast(".")
                "$nameWithoutExtension.txt"
            } else {
                "$fileName.txt"
            }
        }
    }

    /**
     * Создать MockMultipartFile с конкретным документом из Википедии
     */
    fun mockAsMultipart(articleTitle: String): org.springframework.mock.web.MockMultipartFile {
        return try {
            val wikipediaUrl = URI("https://en.wikipedia.org/wiki/${articleTitle.replace(" ", "_")}").toURL()
            mockAsMultipart(wikipediaUrl, "${articleTitle.replace(" ", "_")}.html")
        } catch (e: Exception) {
            // Fallback к дефолтному поведению
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
