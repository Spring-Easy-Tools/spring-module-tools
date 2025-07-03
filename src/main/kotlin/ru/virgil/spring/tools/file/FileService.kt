package ru.virgil.spring.tools.file

import jakarta.annotation.PostConstruct
import org.apache.commons.io.FileUtils
import org.apache.tika.mime.MimeType
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.util.FileSystemUtils
import ru.virgil.spring.tools.file.type.FileTypeConfig
import ru.virgil.spring.tools.file.type.FileTypeService
import ru.virgil.spring.tools.security.oauth.getPrincipal
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

typealias ImageException = Exception

@Suppress("MemberVisibilityCanBePrivate")
abstract class FileService<File : PrivateFile>(
    protected val resourceLoader: ResourceLoader,
    protected val privateFileRepository: PrivateFileRepository<File>,
    protected val fileTypeService: FileTypeService,
    protected val properties: FileProperties,
) {

    fun getPrivate(owner: UserDetails = getPrincipal(), uuid: UUID): Resource {
        val privateImage = privateFileRepository.findByCreatedByAndUuid(owner, uuid).orElseThrow()
        return FileSystemResource(privateImage.fileLocation)
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
        owner: UserDetails = getPrincipal(),
    ): File {
        val userImageFolder = properties.privatePath.resolve(owner.username)
        val uuid = UUID.randomUUID()
        val fileExtension = fileTypeService.getMimeType(content, fileTypeConfig).getExtensionWithoutDot()
        val generatedFileName = "$name-$uuid.$fileExtension"
        val imageFilePath = userImageFolder
            .resolve(fileExtension)
            .resolve(generatedFileName)
            .normalize()
        Files.createDirectories(imageFilePath.parent)
        Files.write(imageFilePath, content)
        val privateImage = createPrivateImageFile(uuid, owner, imageFilePath)
        return privateFileRepository.save(privateImage)
    }

    protected abstract fun createPrivateImageFile(
        uuid: UUID,
        owner: UserDetails = getPrincipal(),
        imageFilePath: Path,
    ): File

    @PostConstruct
    fun preparePublicWorkDirectory() {
        copyInWorkPath(properties.publicPath)
    }

    @PostConstruct
    fun prepareProtectedWorkDirectory() {
        copyInWorkPath(properties.protectedPath)
    }

    protected fun compareDirectories(sourceDirectory: java.io.File, destinationDirectory: java.io.File) {
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
        FileSystemUtils.deleteRecursively(properties.privatePath)
        FileSystemUtils.deleteRecursively(properties.protectedPath)
        FileSystemUtils.deleteRecursively(properties.publicPath)
    }

    private fun MimeType.getExtensionWithoutDot(): String {
        return extension.substring(startIndex = 1)
    }
}
