package ru.virgil.spring.tools.file.mock

import jakarta.annotation.PreDestroy
import org.springframework.mock.web.MockMultipartFile
import ru.virgil.spring.tools.file.FileProperties
import ru.virgil.spring.tools.file.FileService
import ru.virgil.spring.tools.file.PrivateFile
import ru.virgil.spring.tools.util.logging.Logger.inject
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.URL
import java.time.Duration

@Suppress("MemberVisibilityCanBePrivate")
abstract class FileMockService<FileEntity : PrivateFile>(
    protected val fileService: FileService<FileEntity>,
    protected val properties: FileProperties,
) {

    protected val logger = inject(this::class)

    private val multipartCache by lazy {
        try {
            mockAsMultipart(
                url = getDefaultContentUrl(),
                fileName = getDefaultPartName(),
            )
        } catch (e: Exception) {
            logger.warn(e) { "Failed to load mock file from URL, falling back to local mock: ${e.message}" }
            createFallbackMock()
        }
    }

    // TODO: might need to change file name for compatibility with controller parameter names
    fun mockAsMultipart(): MockMultipartFile {
        return multipartCache
    }

    fun mockAsMultipart(url: URL, fileName: String): MockMultipartFile = try {
        val connection = url.openConnection()
        val timeout = Duration.ofSeconds(4).toMillis().toInt()
        connection.connectTimeout = timeout
        connection.readTimeout = timeout
        mockAsMultipart(connection.inputStream, fileName)
    } catch (e: Exception) {
        logger.warn(e) { "Failed to create MockMultipartFile from URL: Exception: ${e.message}" }
        throw e
    }

    fun mockAsMultipart(inputStream: InputStream, fileName: String): MockMultipartFile = try {
        BufferedInputStream(inputStream).use { MockMultipartFile(fileName, it) }
    } catch (e: Exception) {
        logger.error(e) { "Error occurred while creating MockMultipartFile for file: $fileName" }
        throw e
    }

    private fun createFallbackMock() = try {
        val contentStream = createFallbackContent()
        mockAsMultipart(
            inputStream = contentStream,
            fileName = getDefaultPartName(),
        )
    } catch (e: Exception) {
        logger.warn(e) { "Failed to create fallback mock: ${e.message}" }
        throw e
    }

    @PreDestroy
    fun cleanUp() {
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
