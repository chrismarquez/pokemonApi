package com.christopher.pokemonService.models

data class Pokemon (
    val name: String,
    val type: String
) {
    companion object {
        fun fromTypeList(name: String, types: List<String>): Pokemon {
            val type = "[ ${types.joinToString(" / ") { it.capitalize() }} ]"
            return Pokemon(name.capitalize(), type)
        }
    }
}