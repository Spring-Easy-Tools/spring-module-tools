package ru.virgil.spring.tools.file

import org.springframework.boot.context.properties.ConfigurationProperties
import java.nio.file.Path

@ConfigurationProperties(prefix = "files")
data class FileProperties(
    val cleanOnShutdown: Boolean = true,
    val defaultFileName: String = "file",
    val workingPath: Path = Path.of("file"),
    val privatePath: Path = workingPath.resolve("private").resolve("user"),
    val protectedPath: Path = workingPath.resolve("protected"),
    val publicPath: Path = workingPath.resolve("public"),
)
