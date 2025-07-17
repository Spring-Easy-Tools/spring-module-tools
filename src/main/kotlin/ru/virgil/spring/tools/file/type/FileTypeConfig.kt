package ru.virgil.spring.tools.file.type

// todo: просто передавать список или vararg в параметр FileTypeService?
interface FileTypeConfig {

    val allowedMimeTypeRegexes: List<Regex>
}
