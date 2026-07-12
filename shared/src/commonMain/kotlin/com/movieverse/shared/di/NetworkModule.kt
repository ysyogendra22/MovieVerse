package com.movieverse.shared.di

import com.movieverse.shared.data.mock.mockMovieEngine
import com.movieverse.shared.data.remote.MovieApiClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient(mockMovieEngine()) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }
    single { MovieApiClient(httpClient = get()) }
}
