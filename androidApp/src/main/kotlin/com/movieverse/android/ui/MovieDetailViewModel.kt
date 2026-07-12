package com.movieverse.android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movieverse.shared.domain.error.MovieError
import com.movieverse.shared.domain.repository.MovieRepository
import com.movieverse.shared.presentation.MovieDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val movieId: Int,
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieDetailUiState>(MovieDetailUiState.Loading)
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = MovieDetailUiState.Loading
            _uiState.value = try {
                MovieDetailUiState.Success(repository.getMovieDetail(movieId))
            } catch (e: MovieError) {
                MovieDetailUiState.Error(e)
            }
        }
    }
}
