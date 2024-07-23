package ru.virgil.spring.tools.image

import jakarta.annotation.PreDestroy
import net.datafaker.Faker
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.core.userdetails.UserDetails
import java.io.BufferedInputStream
import java.io.IOException
import java.net.URI
import java.net.URL


@Suppress("MemberVisibilityCanBePrivate")
abstract class ImageMockService<Image : PrivateImageInterface>(
    protected val imageService: ImageService<Image>,
    protected val properties: ImageProperties,
) {

    private val faker = Faker()

    fun mockImage(owner: UserDetails, imageName: String = properties.defaultFileName): Image = try {
        imageService.savePrivate(mockAsMultipart().bytes, imageName, owner)
    } catch (e: IOException) {
        throw ImageException(e)
    }

    fun mockAsMultipart(
        imageUrl: URL = URI(faker.avatar().image()).toURL(),
        imageName: String = properties.defaultFileName,
    ): MockMultipartFile = try {
        val inputStream = BufferedInputStream(imageUrl.openStream())
        MockMultipartFile(imageName, inputStream)
    } catch (e: IOException) {
        throw ImageException(e)
    }

    @PreDestroy
    fun preDestroy() {
        if (properties.cleanOnShutdown) {
            imageService.cleanFolders()
        }
    }
}
