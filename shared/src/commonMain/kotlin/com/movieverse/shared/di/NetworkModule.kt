package com.movieverse.shared.di

import com.movieverse.shared.data.remote.MovieApiClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        // No explicit engine: each platform's own engine artifact
        // (androidMain -> okhttp, iosMain -> darwin) is auto-detected.
        HttpClient {
            expectSuccess = true // so 4xx/5xx throw ClientRequestException/ServerResponseException

            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 10_000
            }
        }
    }
    single { MovieApiClient(httpClient = get()) }
}
