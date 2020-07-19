package com.christopher.pokemonService.extensions

import com.christopher.pokemonService.models.MoveName

fun List<MoveName>.getName(language: String): String? = find { it.language.name == language }?.name