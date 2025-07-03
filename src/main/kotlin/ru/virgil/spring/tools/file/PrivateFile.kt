package ru.virgil.spring.tools.file

import ru.virgil.spring.tools.util.data.Identified
import ru.virgil.spring.tools.util.data.Owned
import java.nio.file.Path

interface PrivateFile : Owned, Identified {

    var fileLocation: Path
}
