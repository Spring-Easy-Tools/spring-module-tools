package ru.virgil.spring.tools.file.type

@Deprecated("Есть подозрение, что она избыточная и можно просто передавать список в параметр FileTypeService")
interface FileTypeConfig {

    val allowedExtensions: List<String>
}
