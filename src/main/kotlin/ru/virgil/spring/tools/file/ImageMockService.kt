package ru.virgil.spring.tools.file

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import jakarta.annotation.PreDestroy
import net.datafaker.Faker
import org.springframework.mock.web.MockMultipartFile
import java.awt.Color
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.URI
import java.net.URL

@Suppress("MemberVisibilityCanBePrivate")
abstract class ImageMockService<Image : PrivateFile>(
    protected val fileService: FileService<Image>,
    protected val properties: FileProperties,
    protected val faker: Faker,
) {

    open val defaultImagePartName = properties.defaultFileName

    private val multipartCache by lazy {
        try {
            mockAsMultipart(
                imageUrl = URI(faker.avatar().image()).toURL(),
                imageName = defaultImagePartName,
            )
        } catch (e: Throwable) {
            System.err.println(e)
            tryLocalMocking()
        }
    }

    fun mockAsMultipart(): MockMultipartFile {
        return multipartCache
    }

    fun mockAsMultipart(imageUrl: URL, imageName: String): MockMultipartFile = try {
        val inputStream = BufferedInputStream(imageUrl.openStream())
        MockMultipartFile(imageName, inputStream)
    } catch (e: Throwable) {
        throw ImageException(e)
    }

    fun mockAsMultipart(imageStream: InputStream, imageName: String): MockMultipartFile = try {
        val inputStream = BufferedInputStream(imageStream)
        MockMultipartFile(imageName, inputStream)
    } catch (e: Throwable) {
        throw ImageException(e)
    }

    private fun tryLocalMocking() = try {
        val imageStream = ImmutableImage.create(256, 256)
            .fill(Color.CYAN)
            .bytes(PngWriter())
            .inputStream()
        mockAsMultipart(
            imageStream = imageStream,
            imageName = defaultImagePartName,
        )
    } catch (e: Throwable) {
        throw ImageException(e)
    }

    @PreDestroy
    fun preDestroy() {
        if (properties.cleanOnShutdown) {
            fileService.cleanFolders()
        }
    }
}
