package com.github.lion7.httpklient

abstract class HttpKlientException(val request: HttpRequest, val response: HttpResponse<*>) :
    RuntimeException("HTTP request '${request.method} ${request.uri}' failed with status code '${response.statusCode}' and body:\n${response.bodyAsString()}") {

    companion object {
        fun HttpResponse<*>.bodyAsString(): String = when (body) {
            is String -> body
            is ByteArray -> "ByteArray of ${body.size} bytes"
            else -> body.toString()
        }
    }
}

open class InformationalStatusException(request: HttpRequest, response: HttpResponse<*>) : HttpKlientException(request, response)

open class RedirectStatusException(request: HttpRequest, response: HttpResponse<*>) : HttpKlientException(request, response)

open class ClientStatusException(request: HttpRequest, response: HttpResponse<*>) : HttpKlientException(request, response)
class BadRequestException(request: HttpRequest, response: HttpResponse<*>) : ClientStatusException(request, response)
class UnauthorizedException(request: HttpRequest, response: HttpResponse<*>) : ClientStatusException(request, response)
class ForbiddenException(request: HttpRequest, response: HttpResponse<*>) : ClientStatusException(request, response)
class NotFoundException(request: HttpRequest, response: HttpResponse<*>) : ClientStatusException(request, response)
class MethodNotAllowedException(request: HttpRequest, response: HttpResponse<*>) : ClientStatusException(request, response)
class NotAcceptableException(request: HttpRequest, response: HttpResponse<*>) : ClientStatusException(request, response)
class ConflictException(request: HttpRequest, response: HttpResponse<*>) : ClientStatusException(request, response)

open class ServerStatusException(request: HttpRequest, response: HttpResponse<*>) : HttpKlientException(request, response)
class InternalServerErrorException(request: HttpRequest, response: HttpResponse<*>) : ServerStatusException(request, response)
class NotImplementedException(request: HttpRequest, response: HttpResponse<*>) : ServerStatusException(request, response)
class BadGatewayException(request: HttpRequest, response: HttpResponse<*>) : ServerStatusException(request, response)
class ServiceUnavailableException(request: HttpRequest, response: HttpResponse<*>) : ServerStatusException(request, response)
class GatewayTimeoutException(request: HttpRequest, response: HttpResponse<*>) : ServerStatusException(request, response)
class HttpVersionNotSupportedException(request: HttpRequest, response: HttpResponse<*>) : ServerStatusException(request, response)

open class UnknownStatusException(request: HttpRequest, response: HttpResponse<*>) : HttpKlientException(request, response)
