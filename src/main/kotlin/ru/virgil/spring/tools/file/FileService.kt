package ru.virgil.spring.tools.file

import jakarta.annotation.PostConstruct
import org.apache.commons.io.FileUtils
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.util.FileSystemUtils
import ru.virgil.spring.tools.file.type.FileTypeConfig
import ru.virgil.spring.tools.file.type.FileTypeService
import ru.virgil.spring.tools.security.oauth.getPrincipal
import ru.virgil.spring.tools.util.logging.Logger.inject
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
abstract class FileService<FileEntity : PrivateFile>(
    protected val resourceLoader: ResourceLoader,
    protected val privateFileRepository: PrivateFileRepository<FileEntity>,
    protected val fileTypeService: FileTypeService,
    protected val properties: FileProperties,
) {

    private val logger = inject(this.javaClass)

    fun getPrivate(owner: UserDetails = getPrincipal(), uuid: UUID): Resource {
        val privateFile = privateFileRepository.findByCreatedByAndUuid(owner, uuid).orElseThrow()
        return FileSystemResource(privateFile.fileLocation)
    }

    fun getProtected(name: String): Resource {
        return FileSystemResource(properties.protectedPath.resolve(name))
    }

    fun getPublic(name: String): Resource {
        return FileSystemResource(properties.publicPath.resolve(name))
    }

    protected fun savePrivate(
        content: ByteArray,
        fileTypeConfig: FileTypeConfig,
        name: String? = null,
        owner: UserDetails = getPrincipal(),
    ): FileEntity {
        val filename = if (name.isNullOrBlank()) {
            getDefaultFilename()
        } else {
            name
        }
        val userFilesFolder = properties.privatePath.resolve(owner.username)
        val uuid = UUID.randomUUID()
        val fileExtension = getFileExtension(content, fileTypeConfig)
        val filenameWithExtension = "$filename.$fileExtension"
        val filePath = userFilesFolder
            .resolve(fileExtension)
            .resolve(filenameWithExtension)
            .normalize()
        Files.createDirectories(filePath.parent)
        Files.write(filePath, content)
        val privateFile = createPrivateFile(uuid, owner, filePath)
        return privateFileRepository.save(privateFile)
    }

    protected abstract fun createPrivateFile(
        uuid: UUID,
        owner: UserDetails = getPrincipal(),
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
        val sourceFiles = listOf(*Optional.ofNullable(sourceDirectory.list())
            .orElseThrow { IOException("Source directory is empty") })
        val destinationFiles = listOf(*Optional.ofNullable(destinationDirectory.list())
            .orElseThrow { IOException("Destination directory is empty") })
        if (HashSet(destinationFiles).containsAll(sourceFiles).not()) {
            throw IOException("No files in working directory")
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
        FileSystemUtils.deleteRecursively(properties.privatePath)
        FileSystemUtils.deleteRecursively(properties.protectedPath)
        FileSystemUtils.deleteRecursively(properties.publicPath)
    }

    private fun getFileExtension(content: ByteArray, fileTypeConfig: FileTypeConfig): String {
        return fileTypeService.getExpectedExtension(content, fileTypeConfig).substring(startIndex = 1)
    }

    private fun getDefaultFilename(): String {
        return "${properties.defaultFileName}-${UUID.randomUUID()}"
    }
}
