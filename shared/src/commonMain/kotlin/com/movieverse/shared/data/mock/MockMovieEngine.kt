package com.movieverse.shared.data.mock

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.serialization.json.Json

private val sampleMoviesJson = """
{
  "results": [
    {"id": 1, "title": "Inception", "overview": "A thief who steals corporate secrets through dream-sharing technology.", "vote_average": 8.8, "poster_url": "https://picsum.photos/seed/1/500/750"},
    {"id": 2, "title": "Interstellar", "overview": "A team of explorers travel through a wormhole in space.", "vote_average": 8.6, "poster_url": "https://picsum.photos/seed/2/500/750"},
    {"id": 3, "title": "The Matrix", "overview": "A hacker learns the truth about his reality.", "vote_average": 8.7, "poster_url": "https://picsum.photos/seed/3/500/750"},
    {"id": 4, "title": "Parasite", "overview": "Greed and class discrimination threaten a family's newfound relationship.", "vote_average": 8.5, "poster_url": "https://picsum.photos/seed/4/500/750"},
    {"id": 5, "title": "Spirited Away", "overview": "A girl wanders into a world ruled by gods and witches.", "vote_average": 8.6, "poster_url": "https://picsum.photos/seed/5/500/750"}
  ]
}
""".trimIndent()

/**
 * Ktor engine that answers every request with the same canned JSON payload,
 * exercising the real HTTP client + kotlinx.serialization pipeline without a
 * network call or API key. Swap this for a real engine (OkHttp/Darwin) once a
 * live backend is wired up — see `di/NetworkModule.kt`.
 */
fun mockMovieEngine(): MockEngine = MockEngine { _ ->
    respond(
        content = sampleMoviesJson,
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
}
