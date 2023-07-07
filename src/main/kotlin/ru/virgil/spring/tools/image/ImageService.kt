package ru.virgil.spring.tools.image

import jakarta.annotation.PostConstruct
import org.apache.commons.io.FileUtils
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.util.FileSystemUtils
import ru.virgil.spring.tools.security.oauth.getPrincipal
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

typealias ImageException = Exception

const val defaultImageName = "image"

val privateImagePath: Path = Path.of("image", "private", "user")
val protectedImagePath: Path = Path.of("image", "protected")
val publicImagePath: Path = Path.of("image", "public")

@Suppress("MemberVisibilityCanBePrivate")
abstract class ImageService<Image : PrivateImageInterface>(
    protected val resourceLoader: ResourceLoader,
    protected val privateImageRepository: PrivateImageRepositoryInterface<Image>,
    protected val fileTypeService: FileTypeService,
) {

    fun getPrivate(owner: UserDetails = getPrincipal(), uuid: UUID): Resource {
        val privateImage = privateImageRepository.findByCreatedByAndUuid(owner, uuid).orElseThrow()
        return FileSystemResource(privateImage.fileLocation)
    }

    fun getProtected(name: String): Resource = FileSystemResource(protectedImagePath.resolve(name))

    fun getPublic(name: String): Resource = FileSystemResource(publicImagePath.resolve(name))

    fun savePrivate(content: ByteArray, name: String = defaultImageName, owner: UserDetails = getPrincipal()): Image {
        val userImageFolder = privateImagePath.resolve(owner.username)
        val uuid = UUID.randomUUID()
        val fileExtension = fileTypeService.getImageMimeType(content).replace("image/", "")
        val generatedFileName = "$name-$uuid.$fileExtension"
        val imageFilePath = userImageFolder.resolve(generatedFileName).normalize()
        Files.createDirectories(imageFilePath.parent)
        Files.write(imageFilePath, content)
        val privateImage = createPrivateImageFile(uuid, owner, imageFilePath)
        return privateImageRepository.save(privateImage)
    }

    protected abstract fun createPrivateImageFile(
        uuid: UUID,
        owner: UserDetails = getPrincipal(),
        imageFilePath: Path,
    ): Image

    @PostConstruct
    fun preparePublicWorkDirectory() = copyInWorkPath(publicImagePath)

    @PostConstruct
    fun prepareProtectedWorkDirectory() = copyInWorkPath(protectedImagePath)

    protected fun compareDirectories(sourceDirectory: File, destinationDirectory: File) {
        val sourceFiles = listOf(*Optional.ofNullable(sourceDirectory.list())
            .orElseThrow { ImageException() })
        val destinationFiles = listOf(*Optional.ofNullable(destinationDirectory.list())
            .orElseThrow { ImageException() })
        if (HashSet(destinationFiles).containsAll(sourceFiles).not()) {
            throw ImageException("No files in working directory")
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

    fun cleanFolders() {
        FileSystemUtils.deleteRecursively(privateImagePath)
        FileSystemUtils.deleteRecursively(protectedImagePath)
        FileSystemUtils.deleteRecursively(publicImagePath)
    }
}
