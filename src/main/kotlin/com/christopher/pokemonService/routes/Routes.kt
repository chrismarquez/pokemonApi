package com.christopher.pokemonService.routes

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing

private const val openApiDocs = "https://app.swaggerhub.com/apis/chrismarquez7/PokemonAPI/1.0.0"

fun Application.routes() = routing {
    route("/") {
        get {
            call.respondRedirect(openApiDocs, true)
        }
    }
    route("/v1") {
        route("/common", Route::common)
        route("/compare", Route::compare)
    }
    route("/common", Route::common)
    route("/compare", Route::compare)
}