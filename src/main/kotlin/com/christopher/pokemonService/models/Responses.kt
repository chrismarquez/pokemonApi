package com.christopher.pokemonService.models

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

