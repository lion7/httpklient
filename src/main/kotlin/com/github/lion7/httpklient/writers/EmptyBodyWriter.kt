package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import java.io.OutputStream

object EmptyBodyWriter : BodyWriter {

    override val contentType: String = ""

    override fun write(outputStream: OutputStream) {
        // do nothing
    }
}
