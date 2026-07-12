package com.movieverse.android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movieverse.shared.Greeting
import com.movieverse.shared.domain.model.Movie
import com.movieverse.shared.domain.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MovieListUiState(
    val greeting: String = "",
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = true
)

class MovieListViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieListUiState())
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(greeting = Greeting().greet())
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            val movies = repository.getMovies()
            _uiState.value = _uiState.value.copy(movies = movies, isLoading = false)
        }
    }
}
