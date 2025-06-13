package ru.virgil.spring.tools.image

import ru.virgil.spring.tools.entity.Identified
import ru.virgil.spring.tools.entity.Owned
import java.nio.file.Path

interface PrivateImageInterface : Owned, Identified {

    var fileLocation: Path
}
