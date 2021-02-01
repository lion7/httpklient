package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.HttpKlient
import com.github.lion7.httpklient.HttpRequest
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

class SocketHttpKlient constructor(configure: HttpKlient.Options.Builder.() -> Unit = {}) : AbstractRawHttpKlient() {

    override val options: HttpKlient.Options = HttpKlient.Options.Builder().apply(configure).build()

    override fun connect(request: HttpRequest): Pair<OutputStream, InputStream> {
        val socket = Socket()
        socket.soTimeout = options.readTimeout.toMillis().toInt()
        socket.connect(InetSocketAddress(request.uri.host, request.uri.port), options.connectTimeout.toMillis().toInt())
        return socket.getOutputStream() to socket.getInputStream()
    }
}
