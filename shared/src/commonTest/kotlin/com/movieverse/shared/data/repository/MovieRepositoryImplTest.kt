package com.movieverse.shared.data.repository

import com.movieverse.shared.data.remote.MovieApiClient
import com.movieverse.shared.domain.error.MovieError
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Exercises MovieRepositoryImpl.safeApiCall's exception translation, which is the
 * one place Ktor/serialization exceptions get turned into MovieError — see
 * `.ai/decisions.md` #12. Deliberately doesn't test the Timeout branch: reliably
 * triggering Ktor's real HttpTimeout plugin in a unit test is timing-dependent
 * (flaky) rather than something worth forcing into a fast test suite.
 */
class MovieRepositoryImplTest {

    private fun repositoryFor(engine: MockEngine): MovieRepositoryImpl {
        val httpClient = HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        return MovieRepositoryImpl(MovieApiClient(httpClient, baseUrl = "https://test"))
    }

    private fun jsonEngine(body: String, status: HttpStatusCode = HttpStatusCode.OK) = MockEngine { _ ->
        respond(content = body, status = status, headers = headersOf(HttpHeaders.ContentType, "application/json"))
    }

    @Test
    fun `getMovies maps a non-empty response to domain movies`() = runTest {
        val engine = jsonEngine(
            """[{"id":1,"name":"Under the Dome","summary":"<p>Sealed off.</p>","genres":["Drama"],"premiered":"2013-06-24","rating":{"average":8.8},"image":{"original":"poster.jpg"}}]"""
        )

        val movies = repositoryFor(engine).getMovies()

        assertEquals(1, movies.size)
        assertEquals("Under the Dome", movies.first().title)
        assertEquals("Sealed off.", movies.first().overview)
    }

    @Test
    fun `getMovies returns an empty list for an empty response, not an error`() = runTest {
        val movies = repositoryFor(jsonEngine("[]")).getMovies()

        assertTrue(movies.isEmpty())
    }

    @Test
    fun `getMovies maps a 404 to MovieError-ApiError`() = runTest {
        val engine = jsonEngine("""{"message":"not found"}""", HttpStatusCode.NotFound)

        val error = assertFailsWith<MovieError.ApiError> { repositoryFor(engine).getMovies() }

        assertEquals(404, error.code)
    }

    @Test
    fun `getMovies maps a 500 to MovieError-ApiError`() = runTest {
        val engine = jsonEngine("""{"message":"boom"}""", HttpStatusCode.InternalServerError)

        val error = assertFailsWith<MovieError.ApiError> { repositoryFor(engine).getMovies() }

        assertEquals(500, error.code)
    }

    @Test
    fun `getMovies maps a malformed body to MovieError-EmptyResponse`() = runTest {
        // Non-empty but invalid JSON, so this deterministically hits the parser's
        // SerializationException rather than any Ktor empty-body special-casing.
        val engine = jsonEngine("not valid json")

        assertFailsWith<MovieError.EmptyResponse> { repositoryFor(engine).getMovies() }
    }

    @Test
    fun `getMovieDetail maps a single show to a domain movie`() = runTest {
        val engine = jsonEngine(
            """{"id":1,"name":"Under the Dome","summary":null,"genres":[],"premiered":null,"rating":null,"image":null}"""
        )

        val movie = repositoryFor(engine).getMovieDetail(1)

        assertEquals("Under the Dome", movie.title)
        assertEquals("Unknown", movie.releaseDate)
    }

    @Test
    fun `getMovieDetail maps a 404 to MovieError-ApiError`() = runTest {
        val engine = jsonEngine("""{"message":"not found"}""", HttpStatusCode.NotFound)

        val error = assertFailsWith<MovieError.ApiError> { repositoryFor(engine).getMovieDetail(999) }

        assertEquals(404, error.code)
    }
}
