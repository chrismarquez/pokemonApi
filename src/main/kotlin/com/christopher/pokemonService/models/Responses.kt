package com.christopher.pokemonService.models

/*
* Objects that may be returned as payloads from this Application
* */

data class Battle (
    val multiplier: Double,
    val description: String
)

data class BattleCompareRes (
    val attackingPokemon: Pokemon,
    val defendingPokemon: Pokemon,
    val attack: Battle,
    val defense: Battle
)

data class CommonMovesRes (
    val firstPokemon: Pokemon,
    val secondPokemon: Pokemon,
    val moveList: List<String>
)

