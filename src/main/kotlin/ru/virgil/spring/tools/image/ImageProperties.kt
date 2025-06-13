package ru.virgil.spring.tools.image

import org.springframework.boot.context.properties.ConfigurationProperties
import java.nio.file.Path

@ConfigurationProperties(prefix = "images")
class ImageProperties {

    val cleanOnShutdown: Boolean = true
    val defaultFileName = "image"
    val workingPath: Path = Path.of("image")
    val privatePath: Path = workingPath.resolve("private").resolve("user")
    val protectedPath: Path = workingPath.resolve("protected")
    val publicPath: Path = workingPath.resolve("public")
}
