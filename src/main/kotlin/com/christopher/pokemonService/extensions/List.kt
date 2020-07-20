package com.christopher.pokemonService.extensions

import com.christopher.pokemonService.models.MoveName

/*
* Extensions to the List type.
* */

fun List<MoveName>.getName(language: String): String? = find { it.language.name == language }?.name