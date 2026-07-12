package com.movieverse.shared.data.repository

import com.movieverse.shared.data.remote.MovieApiClient
import com.movieverse.shared.data.remote.toDomain
import com.movieverse.shared.domain.model.Movie
import com.movieverse.shared.domain.repository.MovieRepository

class MovieRepositoryImpl(
    private val apiClient: MovieApiClient
) : MovieRepository {
    override suspend fun getMovies(): List<Movie> =
        apiClient.getPopularMovies().map { it.toDomain() }
}
