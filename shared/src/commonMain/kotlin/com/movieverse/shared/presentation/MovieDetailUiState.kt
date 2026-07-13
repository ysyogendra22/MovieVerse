package com.movieverse.shared.presentation

import com.movieverse.shared.domain.error.MovieError
import com.movieverse.shared.domain.model.Movie

sealed interface MovieDetailUiState {
    data object Loading : MovieDetailUiState
    data class Success(val movie: Movie) : MovieDetailUiState
    data class Error(val error: MovieError) : MovieDetailUiState
}
