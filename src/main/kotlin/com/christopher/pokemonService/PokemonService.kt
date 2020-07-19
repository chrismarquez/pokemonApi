package com.christopher.pokemonService

import com.christopher.pokemonService.exceptions.InternalServerException
import com.christopher.pokemonService.exceptions.NotFoundException
import com.christopher.pokemonService.models.*
import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class PokemonService {

	private val pokeApiUrl = "https://pokeapi.co/api/v2"

	private val client = HttpClient {
		install(JsonFeature) {
			serializer = GsonSerializer {
				setPrettyPrinting()
				serializeNulls()
			}
		}
	}

	suspend fun compareBattling(attacking: String, defending: String): BattleCompareRes = withContext(Dispatchers.IO) {
		try {
			val atkPokemonResDeferred = async { client.get<PokemonRes>("$pokeApiUrl/pokemon/$attacking/") }
			val defPokemonResDeferred = async { client.get<PokemonRes>("$pokeApiUrl/pokemon/$defending/") }
            val atkPokemonRes = atkPokemonResDeferred.await()
            val defPokemonRes = defPokemonResDeferred.await()
            val atkPokemon = Pokemon.fromTypeList(atkPokemonRes.name, atkPokemonRes.types.map { it.type.name })
            val defPokemon = Pokemon.fromTypeList(defPokemonRes.name, defPokemonRes.types.map { it.type.name })
            val (battleTo, battleFrom) = calculatePokemonAdvantage(atkPokemonRes, defPokemonRes)
			BattleCompareRes(atkPokemon, defPokemon, battleTo, battleFrom)
		} catch (e: ClientRequestException) {
			e.printStackTrace()
			if (e.response.status == HttpStatusCode.NotFound) throw NotFoundException("Pokemon does not exist")
			else throw InternalServerException("Unknown error")
		}
	}

	suspend fun findCommonMoves(first: String, second: String, limit: Int, language: String): CommonMovesRes {
		throw NotImplementedError()
	}

    private suspend fun calculatePokemonAdvantage(atkPokemon: PokemonRes, defPokemon: PokemonRes): Pair<Battle, Battle> {
        val types = atkPokemon.types.map { atk ->
            var damageToMultiplier = Battle(-1.0, "")
            var damageFromMultiplier = Battle(10.0, "")
            defPokemon.types.map { def ->
                val (damageTo, damageFrom) = calculateTypeAdvantage(atk.type.url, def.type.url)
                damageToMultiplier = if (damageTo.multiplier > damageToMultiplier.multiplier) damageTo else damageToMultiplier
                damageFromMultiplier = if (damageFrom.multiplier < damageFromMultiplier.multiplier) damageFrom else damageFromMultiplier
            }
            Pair(damageToMultiplier, damageFromMultiplier)
        }
        if (types.size == 1) return types.first()
        val (typeADamage, typeBDamage) = types
        return if (typeADamage.first.multiplier > typeBDamage.first.multiplier) typeADamage else typeBDamage
    }

	private suspend fun calculateTypeAdvantage(atkTypeUrl: String, defTypeUrl: String): Pair<Battle, Battle> = withContext(Dispatchers.IO) {
		val atkTypeDeferred = async { client.get<TypeRes>(atkTypeUrl) }
		val defTypeDeferred = async { client.get<TypeRes>(defTypeUrl) }
		val atkType = atkTypeDeferred.await()
		val defType = defTypeDeferred.await()

		val damageTo = when (defType.name) {
			in atkType.damage_relations.double_damage_to.map { it.name } -> 2.0
			in atkType.damage_relations.half_damage_to.map { it.name } -> 0.5
			in atkType.damage_relations.no_damage_to.map { it.name } -> 0.0
			else -> when (atkType.name) {
				in defType.damage_relations.double_damage_from.map { it.name } -> 2.0
				in defType.damage_relations.half_damage_from.map { it.name } -> 0.5
				in defType.damage_relations.no_damage_from.map { it.name } -> 0.0
				else -> 1.0
			}
		}

		val damageFrom = when (defType.name) {
			in atkType.damage_relations.double_damage_from.map { it.name } -> 2.0
			in atkType.damage_relations.half_damage_from.map { it.name } -> 0.5
			in atkType.damage_relations.no_damage_from.map { it.name } -> 0.0
			else -> when (atkType.name) {
				in defType.damage_relations.double_damage_to.map { it.name } -> 2.0
				in defType.damage_relations.half_damage_to.map { it.name } -> 0.5
				in defType.damage_relations.no_damage_to.map { it.name } -> 0.0
				else -> 1.0
			}
		}

        val damageToMessage = getAttackMessage(damageTo, atkType.name, defType.name)
        val damageFromMessage = getAttackMessage(damageFrom, defType.name, atkType.name)
        Pair(
            Battle(damageTo, damageToMessage),
            Battle(damageFrom, damageFromMessage)
        )
	}

    fun getAttackMessage(multiplier: Double, atkType: String, defType: String): String = when(multiplier) {
        2.0 -> "It's super effective! A $atkType type attack deals double damage to $defType pokemon."
        0.5 -> "Not very effective! A $atkType type attack deals half damage to $defType pokemon."
        0.0 -> "Not effective!! A $atkType type attack deals no damage to $defType pokemon."
        else -> "It's effective. A $atkType type attack deals normal damage to $defType pokemon."
    }


}