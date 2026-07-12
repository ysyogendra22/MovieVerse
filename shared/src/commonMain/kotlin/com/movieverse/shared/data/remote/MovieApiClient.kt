package com.movieverse.shared.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * Talks to the movies backend. The [HttpClient] injected here defaults to a mock
 * engine (see [com.movieverse.shared.data.mock.mockMovieEngine]) so this starter
 * runs with no real API key. Point [baseUrl] at a real service (e.g. TMDB) and
 * swap the engine in `di/NetworkModule.kt` to go live.
 */
class MovieApiClient(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://api.example.com/3"
) {
    suspend fun getPopularMovies(): List<MovieDto> =
        httpClient.get("$baseUrl/movie/popular").body<MoviesResponseDto>().results
}
