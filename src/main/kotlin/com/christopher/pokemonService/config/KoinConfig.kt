package com.christopher.pokemonService.config

import com.christopher.pokemonService.PokemonService
import org.koin.dsl.module

val pokemonServiceModule = module {
    single { PokemonService() }
}