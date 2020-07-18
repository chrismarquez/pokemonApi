package com.christopher.pokemonService

import com.christopher.pokemonService.models.CommonMovesRes
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.response.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.netty.NettyApplicationEngine
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions.*
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CommonMovesEndpointTest {

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
    fun endpointAcceptsCorrectRequest() = runBlocking {
        val first = "Charizard"
        val second = "Moltres"
        val result = client.get<CommonMovesRes>("$baseUrl/common/") {
            parameter("first", first)
            parameter("second", second)
        }
        with (result) {
            assertEquals(firstPokemon.name, "Charizard")
            assertEquals(secondPokemon.name, "Moltres")
            assertEquals(firstPokemon.type, "[ Fire / Flying ]")
            assertEquals(secondPokemon.type, "[ Fire / Flying ]")
        }
    }

    @Test
    fun endpointAcceptsRequestWithLimit() = runBlocking {
        val first = "Charizard"
        val second = "Moltres"
        val result = client.get<CommonMovesRes>("$baseUrl/common/") {
            parameter("first", first)
            parameter("second", second)
            parameter("limit", 5)
        }
        assertEquals(result.moveList.size, 5)
    }

    @Test
    fun endpointRejectsInvalidRequest() = runBlocking {
        val noArgsResponse = client.get<HttpResponse>("$baseUrl/common/")
        assertEquals(noArgsResponse.status, HttpStatusCode.BadRequest)
        val oneArgResponse = client.get<HttpResponse>("$baseUrl/compare/") { parameter("first", "Charizard") }
        assertEquals(oneArgResponse, HttpStatusCode.BadRequest)
        val incorrectArgsResponse = client.get<HttpResponse>("$baseUrl/compare/") {
            parameter("firsz", "Charizard")
            parameter("seconx", "Charizard")
        }
        assertEquals(incorrectArgsResponse, HttpStatusCode.BadRequest)
    }

    @Test
    fun endpointAcceptsLanguageChange() = runBlocking {
        val first = "Charizard"
        val second = "Moltres"

        suspend fun validateLanguage(language: String, expectedName: String) {
            val foreignResult = client.get<CommonMovesRes>("$baseUrl/common") {
                parameter("first", first)
                parameter("second", second)
                header("Accept-Language", language)
            }
            with (foreignResult) {
                val containsName = expectedName in moveList // wing-attack
                assertTrue(containsName)
            }
        }

        validateLanguage("ja-Hrkt", "つばさでうつ")
        validateLanguage("ja-Hrkt", "翅膀攻击")
        validateLanguage("es-Mx", "Ataque Ala")
    }

}