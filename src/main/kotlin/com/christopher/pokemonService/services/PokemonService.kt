package com.christopher.pokemonService.services

import com.christopher.pokemonService.exceptions.NotFoundException
import com.christopher.pokemonService.models.*
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.response.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/*
* The main business service. In charge of contacting an external API to retrieve requested data,
* and transform it to match queries
* @constructor Creates an instance of this service
* */
class PokemonService {

	private val pokeApiUrl = "https://pokeapi.co/api/v2"

	private val client = HttpClient {
		expectSuccess = false
		install(JsonFeature) {
			serializer = GsonSerializer {
				setPrettyPrinting()
				serializeNulls()
			}
		}
	}

	/*
	* Given two pokemons, return whether the first might have an advantage over the second (based on damage multipliers)
	* @param attacking The attacking pokemon name (comparing its effectiveness against defending pokemon)
	* @param defending The defending pokemon name (comparing how effective the attacking pokemon is against it)
	* @return A BattleCompareRes object containing the resulting multipliers and descriptions
	* @throws BadRequestException, NotFoundException
	* */
	suspend fun compareBattling(attacking: String, defending: String): BattleCompareRes = withContext(Dispatchers.IO) {
		val (atkPokemonRes, defPokemonRes) = retrievePokemon(attacking, defending)
		val atkPokemon = Pokemon.fromTypeList(atkPokemonRes.name, atkPokemonRes.types.map { it.type.name })
		val defPokemon = Pokemon.fromTypeList(defPokemonRes.name, defPokemonRes.types.map { it.type.name })
		val (battleTo, battleFrom) = calculatePokemonAdvantage(atkPokemonRes, defPokemonRes)
		BattleCompareRes(atkPokemon, defPokemon, battleTo, battleFrom)
	}

	/*
	* Given two pokemons, return whether the first might have an advantage over the second (based on damage multipliers)
	* @param first One of the pokemon to use (name)
	* @param second The other pokemon to use (name)
	* @param limit The maximum amount of results to return. Must be a positive integer or zero.
	* @param language The language in which to return the moves' names.
	* @return A CommonMovesRes object containing the resulting shared move names in the requested language
	* @throws BadRequestException, NotFoundException
	* */
	suspend fun findCommonMoves(first: String, second: String, limit: Int, language: String): CommonMovesRes = withContext(Dispatchers.IO) {
		val pokemonList = retrievePokemon(first, second).toList()
		val (movesA, movesB) = pokemonList.map { pokemon -> pokemon.moves.map { it.move.url } }
		val moves = movesA intersect movesB
		val limitedMoves = if (limit >= 0) moves.take(limit) else moves
		val promiseList = limitedMoves.map {
			async { client.get<MoveRes>(it) }
		}
		val movesList = promiseList.awaitAll().handleMoveLanguage(language)
		val (pokemonA, pokemonB) = pokemonList.map { pokemon -> Pokemon.fromTypeList(pokemon.name, pokemon.types.map { it.type.name }) }
		CommonMovesRes(pokemonA, pokemonB, movesList)
	}

	private fun List<MoveRes>.handleMoveLanguage(language: String): List<String> = map {
		val availableLanguages = it.names.map { moveName -> moveName.language.name }
		if (language in availableLanguages) {
			it.names.find { moveName -> moveName.language.name == language }?.name ?: it.name
		} else it.name
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

	private suspend fun retrievePokemon(atkPokemon: String, defPokemon: String): Pair<PokemonRes, PokemonRes> = withContext(Dispatchers.IO) {
		val atkPokemonResDeferred = async { client.get<HttpResponse>("$pokeApiUrl/pokemon/$atkPokemon/") }
		val defPokemonResDeferred = async { client.get<HttpResponse>("$pokeApiUrl/pokemon/$defPokemon/") }
		val atkPokemonResponse = atkPokemonResDeferred.await()
		val defPokemonResponse = defPokemonResDeferred.await()
		if (atkPokemonResponse.status == HttpStatusCode.NotFound) throw NotFoundException("Pokemon $atkPokemon does not exist")
		if (defPokemonResponse.status == HttpStatusCode.NotFound) throw NotFoundException("Pokemon $defPokemon does not exist")
		val atkPokemonRes = atkPokemonResponse.receive<PokemonRes>()
		val defPokemonRes = defPokemonResponse.receive<PokemonRes>()
		Pair(atkPokemonRes, defPokemonRes)
	}

	private suspend fun calculateTypeAdvantage(atkTypeUrl: String, defTypeUrl: String): Pair<Battle, Battle> = withContext(Dispatchers.IO) {
		val atkTypeDeferred = async { client.get<TypeRes>(atkTypeUrl) }
		val defTypeDeferred = async { client.get<TypeRes>(defTypeUrl) }
		val atkType = atkTypeDeferred.await()
		val defType = defTypeDeferred.await()

		val damageTo = typeMatching(atkType, defType)
		val damageFrom = typeMatching(defType, atkType)

		val damageToMessage = getAttackMessage(damageTo, atkType.name, defType.name)
		val damageFromMessage = getAttackMessage(damageFrom, defType.name, atkType.name)
		Pair(
			Battle(damageTo, damageToMessage),
			Battle(damageFrom, damageFromMessage)
		)
	}

	private fun typeMatching(atkType: TypeRes, defType: TypeRes): Double = when (defType.name) {
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

	private fun getAttackMessage(multiplier: Double, atkType: String, defType: String): String = when (multiplier) {
		2.0 -> "It's super effective! A $atkType type attack deals double damage to $defType pokemon."
		0.5 -> "Not very effective! A $atkType type attack deals half damage to $defType pokemon."
		0.0 -> "Not effective!! A $atkType type attack deals no damage to $defType pokemon."
		else -> "It's effective. A $atkType type attack deals normal damage to $defType pokemon."
	}


}