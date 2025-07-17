package ru.virgil.spring.tools.file

import jakarta.annotation.PostConstruct
import org.apache.commons.io.FileUtils
import org.apache.tika.mime.MimeType
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.util.FileSystemUtils
import ru.virgil.spring.tools.file.type.FileTypeConfig
import ru.virgil.spring.tools.file.type.FileTypeService
import ru.virgil.spring.tools.security.Security.getCreator
import ru.virgil.spring.tools.util.Http.orNotFound
import ru.virgil.spring.tools.util.logging.Logger.inject
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.jvm.java

@Suppress("MemberVisibilityCanBePrivate")
abstract class FileService<FileEntity : PrivateFile>(
    protected val resourceLoader: ResourceLoader,
    protected val privateFileRepository: PrivateFileRepository<FileEntity>,
    protected val fileTypeService: FileTypeService,
    protected val properties: FileProperties,
) {

    private val logger = inject(this.javaClass)

    fun getPrivate(creator: String = getCreator(), uuid: UUID): Resource {
        val privateFile = privateFileRepository.findByCreatedByAndUuid(creator, uuid)
            .orNotFound(clazz = PrivateFile::class.java)
        return FileSystemResource(privateFile.fileLocation)
    }

    fun getProtected(name: String): Resource {
        return FileSystemResource(properties.protectedPath.resolve(name))
    }

    fun getPublic(name: String): Resource {
        return FileSystemResource(properties.publicPath.resolve(name))
    }

    fun savePrivate(
        content: ByteArray,
        fileTypeConfig: FileTypeConfig,
        name: String = properties.defaultFileName,
        creator: String = getCreator(),
    ): FileEntity {
        val userFilesFolder = properties.privatePath.resolve(creator)
        val uuid = UUID.randomUUID()
        val fileExtension = fileTypeService.getMimeType(content, fileTypeConfig).getExtensionWithoutDot()
        val generatedFileName = "$name-$uuid.$fileExtension"
        val filePath = userFilesFolder
            .resolve(fileExtension)
            .resolve(generatedFileName)
            .normalize()
        Files.createDirectories(filePath.parent)
        Files.write(filePath, content)
        val privateFile = createPrivateFile(uuid, creator, filePath)
        return privateFileRepository.save(privateFile)
    }

    protected abstract fun createPrivateFile(
        uuid: UUID,
        creator: String = getCreator(),
        filePath: Path,
    ): FileEntity

    @PostConstruct
    fun preparePublicWorkDirectory() {
        copyInWorkPath(properties.publicPath)
    }

    @PostConstruct
    fun prepareProtectedWorkDirectory() {
        copyInWorkPath(properties.protectedPath)
    }

    protected fun compareDirectories(sourceDirectory: File, destinationDirectory: File) {
        val sourceFileNames = sourceDirectory.list()?.toSet()
            ?: throw IOException("Source directory listing failed or is null: ${sourceDirectory.path}")
        val destinationFileNames = destinationDirectory.list()?.toSet()
            ?: throw IOException("Destination directory listing failed or is null: ${destinationDirectory.path}")
        if (!destinationFileNames.containsAll(sourceFileNames)) {
            val missingFiles = sourceFileNames - destinationFileNames
            throw IOException("Destination directory ${destinationDirectory.path} is missing files from source directory ${sourceDirectory.path}. Missing: $missingFiles")
        }
    }

    protected fun copyInWorkPath(workPath: Path) = try {
        val resourceClassPath = Paths.get("static").resolve(workPath)
        val resource = resourceLoader.getResource("classpath:$resourceClassPath${File.separator}")
        val destination = workPath.toFile()
        Files.createDirectories(workPath)
        if (resource.exists()) {
            val source = resource.file
            FileUtils.copyDirectory(source, destination)
            compareDirectories(source, destination)
        } else {
            logger.warn("Resource $resourceClassPath does not exist, created empty directory: $workPath")
        }
    } catch (e: IOException) {
        logger.error("Error copying files to work path: ${e.message}", e)
        throw e
    }

    fun cleanFolders() {
        Files.list(properties.workingPath).use { paths ->
            paths.forEach { FileSystemUtils.deleteRecursively(it) }
        }
    }

    private fun MimeType.getExtensionWithoutDot(): String {
        return extension.substring(startIndex = 1)
    }
}
