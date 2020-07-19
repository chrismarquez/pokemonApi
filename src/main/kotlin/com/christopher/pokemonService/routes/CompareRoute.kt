package com.christopher.pokemonService.routes

import com.christopher.pokemonService.PokemonService
import com.christopher.pokemonService.exceptions.BadRequestException
import com.christopher.pokemonService.exceptions.exceptionally
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import org.koin.ktor.ext.inject

fun Route.compare() {
    val service: PokemonService by inject()

    get { exceptionally {
        val attacking = call.parameters["atk"] ?: throw BadRequestException("Expecting atk parameter, the attacking pokemon name")
        val defending = call.parameters["def"] ?: throw BadRequestException("Expecting def parameter, the defending pokemon name")
        val response = service.compareBattling(attacking.toLowerCase(), defending.toLowerCase())
        call.respond(response)
    }}

}