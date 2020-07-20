package com.christopher.pokemonService.routes

import com.christopher.pokemonService.services.PokemonService
import com.christopher.pokemonService.exceptions.BadRequestException
import com.christopher.pokemonService.exceptions.exceptionally
import com.christopher.pokemonService.extensions.success
import io.ktor.application.call
import io.ktor.request.acceptLanguage
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import org.koin.ktor.ext.inject
import java.lang.NumberFormatException

fun Route.common() {
    val service: PokemonService by inject()

    get { exceptionally {
        val attacking = call.parameters["first"] ?: throw BadRequestException("Expecting first parameter, one of the two pokemon")
        val defending = call.parameters["second"] ?: throw BadRequestException("Expecting second parameter, one of the two pokemon")
        val limit = try {
            call.parameters["limit"]?.toInt() ?: -1
        } catch (e: NumberFormatException) {
            throw BadRequestException("The limit parameter should be a positive integer, received ${call.parameters["limit"]}")
        }
        val language = call.request.acceptLanguage() ?: "en"
        val response = service.findCommonMoves(attacking.toLowerCase(), defending.toLowerCase(), limit, language)
        call.respond(response.success)
    }}
}
