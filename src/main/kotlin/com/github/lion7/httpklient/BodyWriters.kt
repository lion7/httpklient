package com.github.lion7.httpklient

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.lion7.httpklient.soap.SoapMessageBodyWriter
import com.github.lion7.httpklient.soap.SoapMessageFactory
import com.github.lion7.httpklient.writers.ByteArrayBodyWriter
import com.github.lion7.httpklient.writers.EmptyBodyWriter
import com.github.lion7.httpklient.writers.FileBodyWriter
import com.github.lion7.httpklient.writers.InputStreamBodyWriter
import com.github.lion7.httpklient.writers.JsonBodyWriter
import com.github.lion7.httpklient.writers.MultipartBodyWriter
import com.github.lion7.httpklient.writers.StringBodyWriter
import com.github.lion7.httpklient.writers.XmlBodyWriter
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import javax.xml.bind.JAXBContext
import javax.xml.soap.SOAPMessage
import javax.xml.validation.Schema

object BodyWriters {

    @JvmStatic
    fun empty() = EmptyBodyWriter

    @JvmStatic
    @JvmOverloads
    fun ofInputStream(inputStream: InputStream, contentType: String = MediaTypes.APPLICATION_OCTET_STREAM) = InputStreamBodyWriter(inputStream, contentType)

    @JvmStatic
    @JvmOverloads
    fun ofByteArray(bytes: ByteArray, contentType: String = MediaTypes.APPLICATION_OCTET_STREAM) = ByteArrayBodyWriter(bytes, contentType)

    @JvmStatic
    @JvmOverloads
    fun ofString(string: String, contentType: String = MediaTypes.TEXT_PLAIN) = StringBodyWriter(string, contentType)

    @JvmStatic
    @JvmOverloads
    fun ofFormData(formData: Map<String, String>, contentType: String = MediaTypes.APPLICATION_X_WWW_FORM_URLENCODED) =
        StringBodyWriter(formData.entries.joinToString("&"), contentType)

    @JvmStatic
    @JvmOverloads
    fun ofPath(path: Path, contentType: String = MediaTypes.APPLICATION_OCTET_STREAM) = FileBodyWriter(path, contentType)

    @JvmStatic
    @JvmOverloads
    fun ofFile(file: File, contentType: String = MediaTypes.APPLICATION_OCTET_STREAM) = FileBodyWriter(file.toPath(), contentType)

    @JvmStatic
    @JvmOverloads
    fun ofClasspathResource(
        name: String,
        contentType: String = MediaTypes.APPLICATION_OCTET_STREAM,
        resolvingClass: Class<*> = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).callerClass
    ): FileBodyWriter {
        val uri = resolvingClass.getResource(name).toURI()
        val path = try {
            Paths.get(uri)
        } catch (e: FileSystemNotFoundException) {
            FileSystems.newFileSystem(uri, emptyMap<String, Any>())
            Paths.get(uri)
        }
        return FileBodyWriter(path, contentType)
    }

    @JvmStatic
    fun ofMultipart(vararg parts: Part) = MultipartBodyWriter(parts.asIterable())

    @JvmStatic
    fun ofSoapMessage(message: SOAPMessage) = SoapMessageBodyWriter(message)

    @JvmStatic
    @JvmOverloads
    fun <V : Any> ofJson(value: V, objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules(), contentType: String = MediaTypes.APPLICATION_JSON_UTF_8) =
        JsonBodyWriter(value, objectMapper::writeValue, contentType)

    @JvmStatic
    @JvmOverloads
    fun <V : Any> ofJson(value: V, serializer: (OutputStream, V) -> Unit, contentType: String = MediaTypes.APPLICATION_JSON_UTF_8) =
        JsonBodyWriter(value, serializer, contentType)

    @JvmStatic
    @JvmOverloads
    fun <V : Any> ofXml(
        element: V,
        jaxbContext: JAXBContext = JAXBContext.newInstance(element.javaClass),
        schema: Schema? = null,
        contentType: String = MediaTypes.APPLICATION_XML_UTF_8
    ) =
        XmlBodyWriter(element, jaxbContext, schema, contentType)

    @JvmStatic
    @JvmOverloads
    fun <V : Any> ofSoap(element: V, jaxbContext: JAXBContext = JAXBContext.newInstance(element.javaClass), mtomEnabled: Boolean = false) =
        SoapMessageBodyWriter(SoapMessageFactory.createMessage(element, jaxbContext, mtomEnabled))
}
