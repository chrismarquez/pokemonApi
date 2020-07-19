package com.christopher.pokemonService.extensions

val Any.success
    get() = mapOf(
        "status" to "success",
        "data" to this
    )

val Any.failure
    get() = mapOf(
        "status" to "failure",
        "data" to this
    )