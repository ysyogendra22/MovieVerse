package com.movieverse.android.ui

import com.movieverse.android.MainDispatcherRule
import com.movieverse.shared.domain.error.MovieError
import com.movieverse.shared.presentation.MovieDetailUiState
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MovieDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `emits Success with the repository's movie on load`() {
        val movie = sampleMovie(id = 42)
        val viewModel = MovieDetailViewModel(
            movieId = 42,
            repository = FakeMovieRepository(movieDetail = movie)
        )

        val state = assertIs<MovieDetailUiState.Success>(viewModel.uiState.value)
        assertEquals(movie, state.movie)
    }

    @Test
    fun `emits Error with the thrown MovieError when the repository fails`() {
        val viewModel = MovieDetailViewModel(
            movieId = 42,
            repository = FakeMovieRepository(errorToThrow = MovieError.ApiError(code = 404))
        )

        val state = assertIs<MovieDetailUiState.Error>(viewModel.uiState.value)
        val error = assertIs<MovieError.ApiError>(state.error)
        assertEquals(404, error.code)
    }

    @Test
    fun `retry re-fetches after an initial failure`() {
        val repository = FakeMovieRepository(errorToThrow = MovieError.NetworkUnavailable)
        val viewModel = MovieDetailViewModel(movieId = 42, repository = repository)
        assertIs<MovieDetailUiState.Error>(viewModel.uiState.value)

        val movie = sampleMovie(id = 42)
        repository.errorToThrow = null
        repository.movieDetail = movie
        viewModel.load()

        val state = assertIs<MovieDetailUiState.Success>(viewModel.uiState.value)
        assertEquals(movie, state.movie)
    }
}
