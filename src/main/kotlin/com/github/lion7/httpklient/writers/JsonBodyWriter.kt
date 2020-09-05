package com.github.lion7.httpklient.writers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.lion7.httpklient.BodyWriter
import java.io.OutputStream

class JsonBodyWriter<T : Any>(private val value: T, private val objectMapper: ObjectMapper = jacksonObjectMapper()) :
    BodyWriter {

    override val contentType: String = "application/json; charset=\"UTF-8\""
    override fun write(outputStream: OutputStream) = objectMapper.writeValue(outputStream, value)
}