package ru.virgil.spring.tools.image

import jakarta.annotation.PostConstruct
import org.apache.commons.io.FileUtils
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.util.FileSystemUtils
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

typealias ImageException = Exception

abstract class ImageService<Image : PrivateImageInterface>(
    @Suppress("MemberVisibilityCanBePrivate")
    protected val resourceLoader: ResourceLoader,
    @Suppress("MemberVisibilityCanBePrivate")
    protected val privateImageRepository: PrivateImageRepositoryInterface<Image>,
    @Suppress("MemberVisibilityCanBePrivate")
    protected val fileTypeService: FileTypeService,
) {

    fun getPrivate(owner: UserDetails, imageUuid: UUID): Resource {
        val privateImage = privateImageRepository.findByCreatedByAndUuid(owner, imageUuid).orElseThrow()
        return FileSystemResource(privateImage.fileLocation)
    }

    fun getProtected(imageName: String): Resource = FileSystemResource(BASE_PROTECTED_IMAGE_PATH.resolve(imageName))

    fun getPublic(imageName: String): Resource = FileSystemResource(BASE_PUBLIC_IMAGE_PATH.resolve(imageName))

    @Throws(IOException::class)
    fun savePrivate(content: ByteArray, imageName: String, owner: UserDetails): Image {
        val imagePath = BASE_PRIVATE_IMAGE_PATH.resolve(owner.username)
        val mimeType = fileTypeService.getImageMimeType(content)
        val imageUuid = UUID.randomUUID()
        val fileExtension = mimeType.replace("image/", "")
        val generatedFileName = "$imageName-$imageUuid.$fileExtension"
        val imageFilePath = imagePath.resolve(generatedFileName).normalize()
        Files.createDirectories(imageFilePath.parent)
        Files.write(imageFilePath, content)
        var privateImage = createPrivateImageFile(imageUuid, owner, imageFilePath)
        privateImage = privateImageRepository.save(privateImage)
        return privateImage
    }

    protected abstract fun createPrivateImageFile(uuid: UUID, owner: UserDetails, imageFilePath: Path): Image

    @PostConstruct
    fun preparePublicWorkDirectory() = copyInWorkPath(BASE_PUBLIC_IMAGE_PATH)

    @PostConstruct
    fun prepareProtectedWorkDirectory() = copyInWorkPath(BASE_PROTECTED_IMAGE_PATH)

    @Throws(IOException::class)
    protected fun compareDirectories(source: File, destination: File) {
        val listOfFilesInA = mutableListOf(*Optional.ofNullable(source.list())
            .orElseThrow { ImageException() })
        val listOfFilesInB = mutableListOf(*Optional.ofNullable(destination.list())
            .orElseThrow { ImageException() })
        if (!HashSet(listOfFilesInB).containsAll(listOfFilesInA)) {
            val ioException = IOException("No files in working directory")
            throw ImageException(ioException)
        }
    }

    protected fun copyInWorkPath(workPath: Path) = try {
        val resourceClassPath = Paths.get("static").resolve(workPath)
        val resource = resourceLoader.getResource("classpath:$resourceClassPath${File.separator}")
        val source = resource.file
        val destination = workPath.toFile()
        FileUtils.copyDirectory(source, destination)
        compareDirectories(source, destination)
    } catch (e: IOException) {
        throw ImageException(e)
    }

    @Throws(IOException::class)
    fun cleanFolders() {
        FileSystemUtils.deleteRecursively(BASE_PRIVATE_IMAGE_PATH)
        FileSystemUtils.deleteRecursively(BASE_PROTECTED_IMAGE_PATH)
        FileSystemUtils.deleteRecursively(BASE_PUBLIC_IMAGE_PATH)
    }

    companion object {

        val BASE_PRIVATE_IMAGE_PATH = Path.of("image", "private", "users")
        val BASE_PROTECTED_IMAGE_PATH = Path.of("image", "protected")
        val BASE_PUBLIC_IMAGE_PATH = Path.of("image", "public")
    }
}
