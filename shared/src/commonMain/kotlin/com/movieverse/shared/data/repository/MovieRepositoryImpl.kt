package com.movieverse.shared.data.repository

import com.movieverse.shared.data.remote.MovieApiClient
import com.movieverse.shared.data.remote.toDomain
import com.movieverse.shared.domain.error.MovieError
import com.movieverse.shared.domain.model.Movie
import com.movieverse.shared.domain.repository.MovieRepository
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

class MovieRepositoryImpl(
    private val apiClient: MovieApiClient
) : MovieRepository {

    override suspend fun getMovies(): List<Movie> = safeApiCall {
        apiClient.getShows().map { it.toDomain() }
    }

    override suspend fun getMovieDetail(id: Int): Movie = safeApiCall {
        apiClient.getShow(id).toDomain()
    }

    /**
     * Translates Ktor/serialization exceptions into [MovieError] so nothing above
     * this class needs to know networking is implemented with Ktor. Catch order
     * matters: timeout types are IOException subtypes, so they must be listed before
     * the general IOException catch.
     */
    private suspend fun <T> safeApiCall(block: suspend () -> T): T =
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpRequestTimeoutException) {
            throw MovieError.Timeout
        } catch (e: ConnectTimeoutException) {
            throw MovieError.Timeout
        } catch (e: SocketTimeoutException) {
            throw MovieError.Timeout
        } catch (e: ClientRequestException) {
            throw MovieError.ApiError(e.response.status.value)
        } catch (e: ServerResponseException) {
            throw MovieError.ApiError(e.response.status.value)
        } catch (e: SerializationException) {
            throw MovieError.EmptyResponse
        } catch (e: IOException) {
            throw MovieError.NetworkUnavailable
        } catch (e: Exception) {
            throw MovieError.Unknown(e)
        }
}
