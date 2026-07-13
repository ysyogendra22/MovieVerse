package com.movieverse.android.ui

import com.movieverse.android.MainDispatcherRule
import com.movieverse.shared.domain.error.MovieError
import com.movieverse.shared.presentation.MovieListUiState
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MovieListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `emits Success with the repository's movies on load`() {
        val movies = listOf(sampleMovie(id = 1), sampleMovie(id = 2))
        val viewModel = MovieListViewModel(FakeMovieRepository(movies = movies))

        val state = assertIs<MovieListUiState.Success>(viewModel.uiState.value)
        assertEquals(movies, state.movies)
    }

    @Test
    fun `emits Empty when the repository returns no movies`() {
        val viewModel = MovieListViewModel(FakeMovieRepository(movies = emptyList()))

        assertIs<MovieListUiState.Empty>(viewModel.uiState.value)
    }

    @Test
    fun `emits Error with the thrown MovieError when the repository fails`() {
        val viewModel = MovieListViewModel(FakeMovieRepository(errorToThrow = MovieError.NetworkUnavailable))

        val state = assertIs<MovieListUiState.Error>(viewModel.uiState.value)
        assertIs<MovieError.NetworkUnavailable>(state.error)
    }

    @Test
    fun `load can recover from an error once the repository succeeds — what Retry relies on`() {
        val repository = FakeMovieRepository(errorToThrow = MovieError.Timeout)
        val viewModel = MovieListViewModel(repository)
        assertIs<MovieListUiState.Error>(viewModel.uiState.value)

        repository.errorToThrow = null
        repository.movies = listOf(sampleMovie())
        viewModel.load()

        val state = assertIs<MovieListUiState.Success>(viewModel.uiState.value)
        assertEquals(listOf(sampleMovie()), state.movies)
    }
}
