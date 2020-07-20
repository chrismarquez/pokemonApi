package com.christopher.pokemonService.extensions

/*
* Extensions to the Any type.
* */

val Any.success
    get() = Success(this)

val Any.failure
    get() = Failure(this)

sealed class Response (
    val status: String
)

class Success<T>(val data: T) : Response("success")
class Failure<T>(val data: T) : Response("failure")