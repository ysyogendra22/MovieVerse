package com.movieverse.android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movieverse.shared.domain.error.MovieError
import com.movieverse.shared.domain.repository.MovieRepository
import com.movieverse.shared.presentation.MovieListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieListViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = MovieListUiState.Loading
            _uiState.value = try {
                val movies = repository.getMovies()
                if (movies.isEmpty()) MovieListUiState.Empty else MovieListUiState.Success(movies)
            } catch (e: MovieError) {
                MovieListUiState.Error(e)
            }
        }
    }
}
