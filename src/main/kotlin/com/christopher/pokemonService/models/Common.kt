package com.christopher.pokemonService.models

/*
* Common objects used throughout the Application
* */

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