package ru.virgil.spring.tools.file

import jakarta.annotation.PreDestroy
import org.springframework.mock.web.MockMultipartFile
import ru.virgil.spring.tools.util.logging.Logger.inject
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.URL

@Suppress("MemberVisibilityCanBePrivate")
abstract class FileMockService<FileEntity : PrivateFile>(
    protected val fileService: FileService<FileEntity>,
    protected val properties: FileProperties,
) {

    protected val logger = inject(this.javaClass)

    private val multipartCache by lazy {
        try {
            mockAsMultipart(
                url = getDefaultContentUrl(),
                fileName = getDefaultPartName(),
            )
        } catch (e: Exception) {
            logger.error(e.message, e)
            createFallbackMock()
        }
    }

    fun mockAsMultipart(): MockMultipartFile {
        return multipartCache
    }

    fun mockAsMultipart(url: URL, fileName: String): MockMultipartFile = try {
        val inputStream = BufferedInputStream(url.openStream())
        MockMultipartFile(fileName, inputStream)
    } catch (e: Exception) {
        throw e
    }

    fun mockAsMultipart(inputStream: InputStream, fileName: String): MockMultipartFile = try {
        val bufferedInputStream = BufferedInputStream(inputStream)
        MockMultipartFile(fileName, bufferedInputStream)
    } catch (e: Exception) {
        throw e
    }

    private fun createFallbackMock() = try {
        val contentStream = createFallbackContent()
        mockAsMultipart(
            inputStream = contentStream,
            fileName = getDefaultPartName(),
        )
    } catch (e: Exception) {
        throw e
    }

    @PreDestroy
    fun preDestroy() {
        if (properties.cleanOnShutdown) {
            fileService.cleanFolders()
        }
    }

    /**
     * Получить URL для дефолтного контента
     */
    protected abstract fun getDefaultContentUrl(): URL

    /**
     * Создать fallback контент в случае ошибки получения из внешнего источника
     */
    protected abstract fun createFallbackContent(): InputStream

    /**
     * Получить имя части файла для MockMultipartFile
     */
    protected abstract fun getDefaultPartName(): String
}
