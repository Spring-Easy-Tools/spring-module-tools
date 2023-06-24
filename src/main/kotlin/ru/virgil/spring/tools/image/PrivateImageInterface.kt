package ru.virgil.spring.tools.image

import ru.virgil.spring.tools.util.data.Identified
import ru.virgil.spring.tools.util.data.Owned
import java.nio.file.Path

interface PrivateImageInterface : Owned, Identified {

    var fileLocation: Path
}
