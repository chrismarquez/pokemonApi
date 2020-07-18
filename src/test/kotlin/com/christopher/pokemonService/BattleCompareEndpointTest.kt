package com.christopher.pokemonService

import com.christopher.pokemonService.models.BattleCompareRes
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.response.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.netty.NettyApplicationEngine
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BattleCompareEndpointTest {

    private lateinit var client: HttpClient
    private var server: NettyApplicationEngine? = null
    private val baseUrl = "http://localhost:8080"

    @BeforeAll
    fun startApp() {
        server = runApp(wait = false)
        client = HttpClient {}
    }

    @AfterAll
    fun terminateApp() {
        server?.stop(1L, 1L, TimeUnit.SECONDS)
    }

    @Test
    fun isAppRunning() = runBlocking {
        val helloMessage = client.get<String>("$baseUrl/")
        assertFalse(helloMessage.isEmpty())
    }

    @Test
    fun endpointAcceptsCorrectRequest() = runBlocking {
        val attacking = "Charizard"
        val defending = "Bulbasaur"
        val response = client.get<BattleCompareRes>("$baseUrl/compare/") {
            parameter("atk", attacking)
            parameter("def", defending)
        }
        with (response) {
            assertEquals(attackingPokemon.name, attacking)
            assertEquals(defendingPokemon.name, defending)
            assertEquals(attackingPokemon.type, "[ Fire / Flying ]")
            assertEquals(defendingPokemon.type, "[ Grass / Poison ]")
            assertEquals(attack.multiplier, 2.0)
            assertEquals(defense.multiplier, 0.5)
        }
    }

    @Test
    fun endpointRejectsInvalidRequest() = runBlocking {
        val noArgsResponse = client.get<HttpResponse>("$baseUrl/compare/")
        assertEquals(noArgsResponse.status, HttpStatusCode.BadRequest)
        val oneArgResponse = client.get<HttpResponse>("$baseUrl/compare/") { parameter("atk", "Charizard") }
        assertEquals(oneArgResponse, HttpStatusCode.BadRequest)
        val incorrectArgsResponse = client.get<HttpResponse>("$baseUrl/compare/") {
            parameter("atkl", "Charizard")
            parameter("df", "Bulbasaur")
        }
        assertEquals(incorrectArgsResponse, HttpStatusCode.BadRequest)
    }

}