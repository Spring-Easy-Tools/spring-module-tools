package ru.virgil.spring.tools.file.type

interface FileTypeConfig {

    val allowedMimeTypeRegexes: List<Regex>
}
