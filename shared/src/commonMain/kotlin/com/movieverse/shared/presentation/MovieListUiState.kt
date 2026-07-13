package com.movieverse.shared.presentation

import com.movieverse.shared.domain.error.MovieError
import com.movieverse.shared.domain.model.Movie

/**
 * Shared between Android and iOS ViewModels so both platforms model the list screen's
 * state identically. [Empty] is a successful fetch that returned zero movies — distinct
 * from [Error], which is a failed fetch (see MovieError for failure categories).
 */
sealed interface MovieListUiState {
    data object Loading : MovieListUiState
    data class Success(val movies: List<Movie>) : MovieListUiState
    data class Error(val error: MovieError) : MovieListUiState
    data object Empty : MovieListUiState
}
