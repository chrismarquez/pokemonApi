package com.christopher.pokemonService

import com.christopher.pokemonService.extensions.Success
import com.christopher.pokemonService.models.CommonMovesRes
import io.ktor.client.HttpClient
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
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
        client = HttpClient {
            install(JsonFeature) {
                serializer = GsonSerializer {
                    setPrettyPrinting()
                    serializeNulls()
                }
            }
        }
    }

    @AfterAll
    fun terminateApp() {
        server?.stop(1L, 1L, TimeUnit.SECONDS)
    }

    @Test
    fun endpointAcceptsCorrectRequest() = runBlocking {
        val first = "Charizard"
        val second = "Moltres"
        val result = client.get<Success<CommonMovesRes>>("$baseUrl/common/") {
            parameter("first", first)
            parameter("second", second)
        }
        with (result.data) {
            assertEquals("Charizard", firstPokemon.name)
            assertEquals("Moltres", secondPokemon.name)
            assertEquals("[ Fire / Flying ]", firstPokemon.type)
            assertEquals("[ Fire / Flying ]", secondPokemon.type)
        }
    }

    @Test
    fun endpointAcceptsRequestWithLimit() = runBlocking {
        val first = "Charizard"
        val second = "Moltres"
        val result = client.get<Success<CommonMovesRes>>("$baseUrl/common/") {
            parameter("first", first)
            parameter("second", second)
            parameter("limit", 5)
        }
        assertEquals(5, result.data.moveList.size)
    }

    @Test
    fun endpointRejectsInvalidRequest() = runBlocking {
        val noArgsResponse = client.get<HttpResponse>("$baseUrl/common/")
        assertEquals(HttpStatusCode.BadRequest, noArgsResponse.status)
        val oneArgResponse = client.get<HttpResponse>("$baseUrl/compare/") { parameter("first", "Charizard") }
        assertEquals(HttpStatusCode.BadRequest, oneArgResponse.status)
        val incorrectArgsResponse = client.get<HttpResponse>("$baseUrl/compare/") {
            parameter("firsz", "Charizard")
            parameter("seconx", "Charizard")
        }
        assertEquals(HttpStatusCode.BadRequest, incorrectArgsResponse.status)
    }

    @Test
    fun endpointAcceptsLanguageChange() = runBlocking {
        val first = "Charizard"
        val second = "Moltres"

        suspend fun validateLanguage(language: String, expectedName: String) {
            val foreignResult = client.get<Success<CommonMovesRes>>("$baseUrl/common") {
                parameter("first", first)
                parameter("second", second)
                header("Accept-Language", language)
            }
            with (foreignResult.data) {
                val containsName = expectedName in moveList // wing-attack
                assertTrue(containsName)
            }
        }

        validateLanguage("ja-Hrkt", "つばさでうつ")
        validateLanguage("zh-Hans", "翅膀攻击")
        validateLanguage("es", "Ataque Ala")
    }

}