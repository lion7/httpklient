package com.github.lion7.httpklient

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.lion7.httpklient.readers.ByteArrayBodyReader
import com.github.lion7.httpklient.readers.DiscardingBodyReader
import com.github.lion7.httpklient.readers.FileBodyReader
import com.github.lion7.httpklient.readers.HttpResponseBodyReader
import com.github.lion7.httpklient.readers.JsonBodyReader
import com.github.lion7.httpklient.readers.MultipartBodyReader
import com.github.lion7.httpklient.readers.StringBodyReader
import com.github.lion7.httpklient.readers.XmlBodyReader
import com.github.lion7.httpklient.soap.SoapBodyReader
import com.github.lion7.httpklient.soap.SoapMessageBodyReader
import org.w3c.dom.Node
import java.io.InputStream
import java.lang.reflect.Type
import javax.xml.bind.JAXBContext
import javax.xml.soap.SOAPMessage

object BodyReaders {

    @JvmStatic
    @JvmOverloads
    fun discarding(accept: String = MediaTypes.ALL) = DiscardingBodyReader(accept)

    @JvmStatic
    fun <T> ofHttpResponse(bodyReader: BodyReader<T>) = HttpResponseBodyReader(bodyReader)

    @JvmStatic
    @JvmOverloads
    fun ofByteArray(accept: String = MediaTypes.ALL) = ByteArrayBodyReader(accept)

    @JvmStatic
    @JvmOverloads
    fun ofString(accept: String = MediaTypes.TEXT) = StringBodyReader(accept)

    @JvmStatic
    @JvmOverloads
    fun ofFile(accept: String = MediaTypes.ALL, extension: String = "tmp") = FileBodyReader(accept, extension)

    @JvmStatic
    @JvmOverloads
    fun ofMultipart(accept: String = MediaTypes.MULTIPART_FORM_DATA) = MultipartBodyReader(accept)

    @JvmStatic
    @JvmOverloads
    fun ofSoapMessage(mtomEnabled: Boolean = true) = SoapMessageBodyReader(mtomEnabled)

    @JvmStatic
    @JvmOverloads
    fun <V : Any> ofJson(type: Class<V>, objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules(), accept: String = MediaTypes.APPLICATION_JSON_UTF_8) =
        JsonBodyReader(accept) {
            objectMapper.readValue(it, object : TypeReference<V>() {
                override fun getType(): Type = type
            })
        }

    @JvmStatic
    @JvmOverloads
    fun <V : Any> ofJson(type: TypeReference<V>, objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules(), accept: String = MediaTypes.APPLICATION_JSON_UTF_8) =
        JsonBodyReader(accept) { objectMapper.readValue(it, type) }

    @JvmStatic
    @JvmOverloads
    fun <V : Any> ofJson(deserializer: (InputStream) -> V, accept: String = MediaTypes.APPLICATION_JSON_UTF_8) =
        JsonBodyReader(accept, deserializer)

    @JvmStatic
    @JvmOverloads
    fun <V : Any> ofXml(type: Class<V>, jaxbContext: JAXBContext = JAXBContext.newInstance(type), accept: String = MediaTypes.APPLICATION_XML_UTF_8) =
        XmlBodyReader(type, jaxbContext, accept)

    @JvmStatic
    @JvmOverloads
    fun <V : Any> ofSoap(
        type: Class<V>,
        jaxbContext: JAXBContext = JAXBContext.newInstance(type),
        nodeExtractor: (SOAPMessage) -> Node = { it.soapBody.firstChild },
        mtomEnabled: Boolean = true
    ) = SoapBodyReader(type, jaxbContext, nodeExtractor, mtomEnabled)

    inline fun <reified V : Any> ofJson(objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules(), accept: String = MediaTypes.APPLICATION_JSON_UTF_8) =
        JsonBodyReader(accept) { objectMapper.readValue(it, object : TypeReference<V>() {}) }

    inline fun <reified V : Any> ofJsonList(objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules(), accept: String = MediaTypes.APPLICATION_JSON_UTF_8) =
        JsonBodyReader(accept) { objectMapper.readValue(it, object : TypeReference<List<V>>() {}) }

    inline fun <reified K : Any, reified V : Any> ofJsonMap(
        objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules(),
        accept: String = MediaTypes.APPLICATION_JSON_UTF_8
    ) = JsonBodyReader(accept) { objectMapper.readValue(it, object : TypeReference<Map<K, V>>() {}) }

    inline fun <reified V : Any> ofXml(jaxbContext: JAXBContext = JAXBContext.newInstance(V::class.java), accept: String = MediaTypes.APPLICATION_XML_UTF_8) =
        XmlBodyReader(V::class.java, jaxbContext, accept)

    inline fun <reified V : Any> ofSoap(
        jaxbContext: JAXBContext = JAXBContext.newInstance(V::class.java),
        noinline nodeExtractor: (SOAPMessage) -> Node = { it.soapBody.firstChild },
        mtomEnabled: Boolean = true
    ) = SoapBodyReader(V::class.java, jaxbContext, nodeExtractor, mtomEnabled)
}
