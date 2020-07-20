package com.christopher.pokemonService.models

/*
* Objects returned from the API this service consumes
* */

data class ResourceInfo (
    val name: String,
    val url: String
)

data class PokemonRes (
    val name: String,
    val moves: List<PokemonMoveRes>,
    val types: List<PokemonTypeRes>
)

data class PokemonMoveRes (
    val move: ResourceInfo
)

data class MoveRes (
    val name: String,
    val names: List<MoveName>
)

data class PokemonTypeRes (
    val type: ResourceInfo
)

data class TypeRes (
	val name: String,
	val damage_relations: DamageRelation
)

data class DamageRelation (
	val double_damage_from: List<ResourceInfo> = listOf(),
	val double_damage_to: List<ResourceInfo> = listOf(),
	val half_damage_from: List<ResourceInfo> = listOf(),
	val half_damage_to: List<ResourceInfo> = listOf(),
	val no_damage_from: List<ResourceInfo> = listOf(),
	val no_damage_to: List<ResourceInfo> = listOf()
)

data class MoveName (
    val name: String,
    val language: ResourceInfo
)