package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.HttpKlientOptions
import com.github.lion7.httpklient.HttpRequest
import com.github.lion7.httpklient.HttpResponse
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Proxy

class UrlConnectionHttpKlient(override val options: HttpKlientOptions) : AbstractHttpKlient() {

    override fun <T> exchange(request: HttpRequest, responseHandler: (HttpResponse<BufferedInputStream>) -> HttpResponse<T>): HttpResponse<T> {
        val connection = when {
            options.proxy != null -> request.uri.toURL().openConnection(Proxy(Proxy.Type.HTTP, InetSocketAddress(options.proxy.host, options.proxy.port)))
            else -> request.uri.toURL().openConnection()
        } as HttpURLConnection
        try {
            connection.requestMethod = request.method
            connection.connectTimeout = options.connectTimeout.toMillis().toInt()
            connection.readTimeout = options.readTimeout.toMillis().toInt()
            connection.instanceFollowRedirects = options.followRedirects

            request.headers.mapValues { it.value.joinToString(",") }.forEach { (key, value) ->
                connection.setRequestProperty(key, value)
            }

            if (request.bodyWriter.contentType.isNotEmpty()) {
                connection.doOutput = true
                connection.outputStream.buffered().use { request.bodyWriter.write(it) }
            }

            val statusCode = connection.responseCode
            val statusReason = connection.responseMessage ?: ""
            val headerFields: Map<String?, List<String>> = connection.headerFields

            val responseHeaders = HttpHeaders()
            responseHeaders.mergeMultiMap(headerFields.filterKeys { it != null }.mapKeys { it.key as String })
            val inputStream = try {
                connection.errorStream ?: connection.inputStream
            } catch (e: IOException) {
                InputStream.nullInputStream()
            }
            return inputStream.buffered().use {
                responseHandler(HttpResponse(statusCode, statusReason, responseHeaders, it))
            }
        } finally {
            connection.disconnect()
        }
    }
}
