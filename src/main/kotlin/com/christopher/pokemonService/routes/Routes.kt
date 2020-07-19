package com.christopher.pokemonService.routes

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.routes() = routing {
    route("/") {
        get {
            call.respond("I am too alive, and I am the Senate")
        }
    }
    route("/common", Route::common)
    route("/compare", Route::compare)
}