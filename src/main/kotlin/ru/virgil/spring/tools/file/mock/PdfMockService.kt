package ru.virgil.spring.tools.file.mock

import net.datafaker.Faker
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import ru.virgil.spring.tools.file.FileProperties
import ru.virgil.spring.tools.file.FileService
import ru.virgil.spring.tools.file.PrivateFile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URI
import java.net.URL

abstract class PdfMockService<FileEntity : PrivateFile>(
    fileService: FileService<FileEntity>,
    properties: FileProperties,
    faker: Faker,
) : TxtMockService<FileEntity>(fileService, properties, faker) {

    override fun getDefaultContentUrl(): URL {
        return URI("https://upload.wikimedia.org/wikipedia/commons/4/41/Ve%C4%8De_%C4%8Cehova.pdf").toURL()
    }

    override fun createFallbackContent() = createLocalPdfMock()

    override fun getDefaultPartName() = properties.defaultFileName

    /**
     * TODO: генераторы не нужны? Просто использовать локальный ресурс?
     */
    fun createLocalPdfMock(): ByteArrayInputStream {
        val document = PDDocument()
        val outputStream = document.use {
            val page = PDPage()
            document.addPage(page)
            val contentStream = PDPageContentStream(document, page)
            contentStream.use {
                contentStream.beginText()
                contentStream.newLineAtOffset(100f, 700f)
                contentStream.setFont(PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12f);
                // Похоже кириллица в шрифтах не поддерживается
                contentStream.showText("Mocked PDF Document for testing")
                contentStream.endText()
            }
            ByteArrayOutputStream().also { document.save(it) }
        }
        return ByteArrayInputStream(outputStream.toByteArray())
    }
}
