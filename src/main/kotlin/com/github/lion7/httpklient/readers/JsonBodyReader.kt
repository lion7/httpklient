package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpResponse
import java.io.InputStream

class JsonBodyReader<T : Any>(override val accept: String, private val deserializer: (InputStream) -> T) : BodyReader<T> {

    override fun <S : InputStream> read(response: HttpResponse<S>): T =
        deserializer(response.body)
}
