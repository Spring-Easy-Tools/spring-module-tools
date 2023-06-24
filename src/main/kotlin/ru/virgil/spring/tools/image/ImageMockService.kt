package ru.virgil.spring.tools.image

import jakarta.annotation.PreDestroy
import net.datafaker.Faker
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.core.userdetails.UserDetails
import ru.virgil.spring.tools.image.ImageException
import java.io.BufferedInputStream
import java.io.IOException
import java.net.URL


@EnableConfigurationProperties(ImageProperties::class)
abstract class ImageMockService<Image : PrivateImageInterface>(
    @Suppress("MemberVisibilityCanBePrivate")
    protected val imageService: ImageService<Image>,
    @Suppress("MemberVisibilityCanBePrivate")
    protected val fakerUtils: Faker,
    @Suppress("MemberVisibilityCanBePrivate")
    protected val imageProperties: ImageProperties,
) {

    @Throws(ImageException::class)
    fun mockImage(owner: UserDetails): Image = try {
        imageService.savePrivate(mockAsMultipart().bytes, IMAGE_NAME, owner)
    } catch (e: IOException) {
        throw ImageException(e)
    }

    @Throws(ImageException::class)
    fun mockAsMultipart(): MockMultipartFile = try {
        val bufferedInputStream = BufferedInputStream(URL(fakerUtils.avatar().image()).openStream())
        MockMultipartFile(IMAGE_NAME, bufferedInputStream)
    } catch (e: IOException) {
        throw ImageException(e)
    }

    @PreDestroy
    @Throws(IOException::class)
    fun preDestroy() {
        if (imageProperties.cleanOnDestroy) {
            imageService.cleanFolders()
        }
    }

    companion object {

        const val IMAGE_NAME = "image"
    }
}
