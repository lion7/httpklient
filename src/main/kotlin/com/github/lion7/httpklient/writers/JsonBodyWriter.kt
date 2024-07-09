package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import org.apache.commons.io.output.CountingOutputStream
import java.io.OutputStream

class JsonBodyWriter<T : Any>(private val value: T, private val serializer: (OutputStream, T) -> Unit, override val contentType: String) : BodyWriter {

    override val contentLength: Long = CountingOutputStream(OutputStream.nullOutputStream()).use {
        write(it)
        it.byteCount
    }

    override fun write(outputStream: OutputStream) =
        serializer(outputStream, value)
}
