package com.movieverse.shared.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * Talks to TVMaze (https://api.tvmaze.com) — a free, keyless TV show API used as
 * dummy data. Exceptions are left to propagate as-is; translating them into
 * [com.movieverse.shared.domain.error.MovieError] is MovieRepositoryImpl's job, not
 * this class's — this stays a thin HTTP wrapper.
 */
class MovieApiClient(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://api.tvmaze.com"
) {
    suspend fun getShows(): List<ShowDto> = httpClient.get("$baseUrl/shows").body()

    suspend fun getShow(id: Int): ShowDto = httpClient.get("$baseUrl/shows/$id").body()
}
