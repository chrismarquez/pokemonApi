package com.christopher.pokemonService

import com.christopher.pokemonService.config.config
import com.christopher.pokemonService.routes.routes
import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

const val PORT = 8080

fun main() = runApp()

fun runApp(wait: Boolean = true) {
    println("Hello there! Ready and listening on port $PORT")
    val server = setupServer()
    server.start(wait)
}

fun setupServer() = embeddedServer(Netty, port = PORT) {
    init()
}

fun Application.init() {
    config()
    routes()
}