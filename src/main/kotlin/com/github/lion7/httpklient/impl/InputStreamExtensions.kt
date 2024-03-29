package com.github.lion7.httpklient.impl

import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets.UTF_8

internal fun BufferedInputStream.readUntil(delimiter: Int): ByteArray? {
    val buffer = ByteArrayOutputStream()
    while (true) {
        when (val ch = read()) {
            delimiter -> {
                // delimiter found, return the buffer
                return buffer.toByteArray()
            }
            -1 -> {
                // end of stream, return either the buffer or null if it is empty
                return if (buffer.size() > 0) buffer.toByteArray() else null
            }
            else -> {
                // write the byte to the buffer
                buffer.write(ch)
            }
        }
    }
}

internal fun BufferedInputStream.readLine(): String? {
    val buffer = readUntil('\r'.code)
    // if we found something then we also want to consume the \n following the \r
    if (buffer != null) {
        // mark the current position in the stream so we can go back
        mark(1)
        // read the next character
        val next = read()
        // if it's not -1 or a newline, reset the stream to the marked position
        if (next != -1 && next != '\n'.code) {
            reset()
        }
    }
    return buffer?.toString(UTF_8)
}

internal fun BufferedInputStream.isEndOfStream(): Boolean {
    mark(1)
    val eof = read() == -1
    reset()
    return eof
}
