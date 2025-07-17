package ru.virgil.spring.tools.file

import ru.virgil.spring.tools.entity.Identified
import ru.virgil.spring.tools.entity.Owned
import java.nio.file.Path

interface PrivateFile : Owned, Identified {

    var fileLocation: Path
}
