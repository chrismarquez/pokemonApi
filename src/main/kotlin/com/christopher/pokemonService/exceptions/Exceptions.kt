package com.christopher.pokemonService.exceptions

import com.christopher.pokemonService.extensions.failure
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.pipeline.PipelineInterceptor

suspend fun PipelineContext<Unit, ApplicationCall>.exceptionally(
    fn: PipelineInterceptor<Unit, ApplicationCall>
) {
    suspend fun catchException(call: ApplicationCall, e: Exception, code: HttpStatusCode) {
        val error = e.message?.failure ?: e.toString().failure
        e.printStackTrace()
        call.respond(code, error)
    }
    try {
        fn(this, Unit)
    } catch (e: ErrorStatusCodeException) {
        catchException(this.call, e, e.statusCode)
    } catch (e: Exception) {
        catchException(this.call, e, HttpStatusCode.InternalServerError)
    }
}

sealed class ErrorStatusCodeException(message: String): Exception(message) {
    abstract val statusCode: HttpStatusCode
}

class NotFoundException(message: String) :  ErrorStatusCodeException(message) {
    override val statusCode: HttpStatusCode = HttpStatusCode.NotFound
}
class BadRequestException(message: String) :  ErrorStatusCodeException(message) {
    override val statusCode: HttpStatusCode = HttpStatusCode.BadRequest
}
class ConflictException(message: String) :  ErrorStatusCodeException(message) {
    override val statusCode: HttpStatusCode = HttpStatusCode.Conflict
}
class InternalServerException(message: String) :  ErrorStatusCodeException(message) {
    override val statusCode: HttpStatusCode = HttpStatusCode.InternalServerError
}
class UnauthorizedException(message: String) :  ErrorStatusCodeException(message) {
    override val statusCode: HttpStatusCode = HttpStatusCode.Unauthorized
}
class ForbiddenException(message: String) :  ErrorStatusCodeException(message) {
    override val statusCode: HttpStatusCode = HttpStatusCode.Forbidden
}