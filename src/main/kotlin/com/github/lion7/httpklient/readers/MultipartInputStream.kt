package com.github.lion7.httpklient.readers

import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream

/**
 * A class to handle multipart MIME input streams. See RC 1521.
 * This class handles multipart input streams, as defined by the RFC 1521.
 * It provides a sequential interface to all MIME parts, and for each part
 * it delivers a suitable InputStream for getting its body.
 */
internal class MultipartInputStream(private val inputStream: BufferedInputStream, private val boundary: ByteArray) : InputStream() {
    private var partEnd = false
    private var fileEnd = false

    /**
     * Switch to the next available part of data.
     * One can interrupt the current part, and use this method to switch
     * to next part before current part was totally read.
     * @return A boolean **true** if there next part is ready,
     * or **false** if this was the last part.
     */
    @Throws(IOException::class)
    fun nextInputStream(): Boolean {
        if (fileEnd) {
            return false
        }
        return if (!partEnd) {
            skipToBoundary()
        } else {
            partEnd = false
            true
        }
    }

    /**
     * Read one byte of data from the current part.
     * @return A byte of data, or **-1** if end of part or file.
     * @exception IOException If some IO error occurred.
     */
    @Throws(IOException::class)
    override fun read(): Int {
        if (partEnd || fileEnd) {
            return -1
        }

        val ch: Int = inputStream.read()
        if (ch == -1) {
            fileEnd = true
            return -1
        }

        // check for a boundary
        inputStream.mark(boundary.size + 3)

        if (ch == '\r'.toInt()) {
            // carriage return, check if it is followed by a newline
            if (inputStream.read() == '\n'.toInt()) {
                // newline found, check if it is followed by a hyphen
                if (inputStream.read() != '-'.toInt()) {
                    // no hyphen found, so a boundary cannot follow...
                    inputStream.reset()
                    return ch
                }
            } else {
                // no newline found, so a boundary cannot follow...
                inputStream.reset()
                return ch
            }
        } else if (ch != '-'.toInt()) {
            // not a hyphen, so a boundary cannot follow...
            return ch
        }

        // check that the second hyphen is there
        if (inputStream.read() != '-'.toInt()) {
            // not a hyphen, so a boundary cannot follow...
            inputStream.reset()
            return ch
        }

        for (b in boundary) {
            if (inputStream.read() != b.toInt()) {
                // bytes do not match the boundary
                inputStream.reset()
                return ch
            }
        }

        // If we get to this point a boundary was found!
        // Check if this is a closing boundary.
        // The next 2 characters should be either
        // "\r\n" indicating the beginning of a next part
        // or "--" indicating the end of the multipart body
        val c1 = inputStream.read()
        val c2 = inputStream.read()
        return if (c1 == '\r'.toInt() && c2 == '\n'.toInt()) {
            // normal boundary
            partEnd = true
            -1
        } else if (c1 == '-'.toInt() && c2 == '-'.toInt()) {
            // closing boundary
            fileEnd = true
            -1
        } else if (c1 == -1) {
            // edge case: set `fileEnd` to true if we reached the end of the stream
            fileEnd = true
            -1
        } else {
            -1
        }
    }

    /**
     * Read n bytes of data from the current part.
     * @return the number of bytes data, read or **-1**
     * if end of file.
     * @exception IOException If some IO error occurred.
     */
    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        var got = 0
        while (got < len) {
            val ch = read()
            if (ch == -1) return if (got == 0) -1 else got
            b[off + got++] = (ch and 0xFF).toByte()
        }
        return got
    }

    @Throws(IOException::class)
    override fun skip(n: Long): Long {
        for (i in 0 until n) {
            if (read() == -1) {
                return i
            }
        }
        return n
    }

    @Throws(IOException::class)
    override fun available(): Int {
        return inputStream.available()
    }

    // Skip to next input boundary, set stream at beginning of content:
    // Returns true if boundary was found, false otherwise.
    @Throws(IOException::class)
    private fun skipToBoundary(): Boolean {
        while (read() != -1);
        return if (partEnd) {
            partEnd = false
            true
        } else {
            false
        }
    }
}