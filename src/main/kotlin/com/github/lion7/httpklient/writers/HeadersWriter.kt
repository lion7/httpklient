package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.HttpHeaders
import java.io.Writer

internal object HeadersWriter {

    fun write(headers: HttpHeaders, writer: Writer) {
        headers.forEach { name, values -> values.forEach { writer.write("$name: $it\r\n") } }
        writer.write("\r\n")
    }
}