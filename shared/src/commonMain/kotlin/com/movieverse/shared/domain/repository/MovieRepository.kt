package com.movieverse.shared.domain.repository

import com.movieverse.shared.domain.model.Movie

interface MovieRepository {
    suspend fun getMovies(): List<Movie>
}
