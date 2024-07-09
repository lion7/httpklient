package com.github.lion7.httpklient

import com.github.lion7.httpklient.impl.LoggingHttpKlient
import com.github.lion7.httpklient.impl.MockHttpKlient
import com.github.lion7.httpklient.impl.SocketHttpKlient
import com.github.lion7.httpklient.impl.TracingHttpKlient
import com.github.lion7.httpklient.impl.UrlConnectionHttpKlient
import io.opentracing.Tracer
import java.io.File
import java.io.OutputStream

object HttpKlients {
    fun urlConnection(configure: HttpKlientOptions.Builder.() -> Unit) = UrlConnectionHttpKlient(HttpKlientOptions.Builder().apply(configure).build())
    fun socket(configure: HttpKlientOptions.Builder.() -> Unit) = SocketHttpKlient(HttpKlientOptions.Builder().apply(configure).build())
    fun mock(configure: HttpKlientOptions.Builder.() -> Unit) = MockHttpKlient(HttpKlientOptions.Builder().apply(configure).build())
    fun tracing(tracer: Tracer, delegate: HttpKlient) = TracingHttpKlient(tracer, delegate)
    fun logging(stream: OutputStream, delegate: HttpKlient) = LoggingHttpKlient(stream, delegate)
    fun logging(logFile: File, delegate: HttpKlient): LoggingHttpKlient {
        val stream = logFile.outputStream()
        Runtime.getRuntime().addShutdownHook(Thread { stream.close() })
        return LoggingHttpKlient(stream, delegate)
    }
}
