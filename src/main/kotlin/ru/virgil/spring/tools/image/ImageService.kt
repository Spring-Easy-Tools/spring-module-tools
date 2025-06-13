package ru.virgil.spring.tools.image

import jakarta.annotation.PostConstruct
import org.apache.commons.io.FileUtils
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.util.FileSystemUtils
import ru.virgil.spring.tools.security.Security.getCreator
import ru.virgil.spring.tools.util.Http.orNotFound
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

typealias ImageException = Exception

@Suppress("MemberVisibilityCanBePrivate")
abstract class ImageService<Image : PrivateImageInterface>(
    protected val resourceLoader: ResourceLoader,
    protected val privateImageRepository: PrivateImageRepositoryInterface<Image>,
    protected val fileTypeService: FileTypeService,
    protected val properties: ImageProperties,
) {

    fun getPrivate(creator: String = getCreator(), uuid: UUID): Resource {
        val privateImage = privateImageRepository.findByCreatedByAndUuid(creator, uuid).orNotFound(clazz = PrivateImageInterface::class.java)
        return FileSystemResource(privateImage.fileLocation)
    }

    fun getProtected(name: String): Resource = FileSystemResource(properties.protectedPath.resolve(name))

    fun getPublic(name: String): Resource = FileSystemResource(properties.publicPath.resolve(name))

    fun savePrivate(
        content: ByteArray,
        name: String = properties.defaultFileName,
        creator: String = getCreator(),
    ): Image {
        val userImageFolder = properties.privatePath.resolve(creator)
        val uuid = UUID.randomUUID()
        val fileExtension = fileTypeService.getImageMimeType(content).replace("image/", "")
        val generatedFileName = "$name-$uuid.$fileExtension"
        val imageFilePath = userImageFolder.resolve(generatedFileName).normalize()
        Files.createDirectories(imageFilePath.parent)
        Files.write(imageFilePath, content)
        val privateImage = createPrivateImageFile(uuid, creator, imageFilePath)
        return privateImageRepository.save(privateImage)
    }

    protected abstract fun createPrivateImageFile(
        uuid: UUID,
        creator: String = getCreator(),
        imageFilePath: Path,
    ): Image

    @PostConstruct
    fun preparePublicWorkDirectory() {
        copyInWorkPath(properties.publicPath)
    }

    @PostConstruct
    fun prepareProtectedWorkDirectory() {
        copyInWorkPath(properties.protectedPath)
    }

    protected fun compareDirectories(sourceDirectory: File, destinationDirectory: File) {
        val sourceFileNames = sourceDirectory.list()?.toSet() ?: throw ImageException("Source directory listing failed or is null: ${sourceDirectory.path}")
        val destinationFileNames = destinationDirectory.list()?.toSet() ?: throw ImageException("Destination directory listing failed or is null: ${destinationDirectory.path}")
        if (!destinationFileNames.containsAll(sourceFileNames)) {
            val missingFiles = sourceFileNames - destinationFileNames
            throw ImageException("Destination directory ${destinationDirectory.path} is missing files from source directory ${sourceDirectory.path}. Missing: $missingFiles")
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
        Files.list(properties.workingPath).use { paths ->
            paths.forEach { FileSystemUtils.deleteRecursively(it) }
        }
    }
}
