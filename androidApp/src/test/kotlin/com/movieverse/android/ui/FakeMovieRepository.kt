package com.movieverse.android.ui

import com.movieverse.shared.domain.error.MovieError
import com.movieverse.shared.domain.model.Movie
import com.movieverse.shared.domain.repository.MovieRepository

/**
 * Test double, not a production fallback (see coding_style.md — ViewModels never get
 * a default-constructed repository in production code; this is only ever passed
 * explicitly from test code).
 */
class FakeMovieRepository(
    var movies: List<Movie> = emptyList(),
    var movieDetail: Movie? = null,
    var errorToThrow: MovieError? = null
) : MovieRepository {

    override suspend fun getMovies(): List<Movie> {
        errorToThrow?.let { throw it }
        return movies
    }

    override suspend fun getMovieDetail(id: Int): Movie {
        errorToThrow?.let { throw it }
        return movieDetail ?: error("FakeMovieRepository: no movieDetail configured for id=$id")
    }
}

fun sampleMovie(id: Int = 1, title: String = "Under the Dome") = Movie(
    id = id,
    title = title,
    overview = "A small town is sealed off.",
    rating = 8.8,
    posterUrl = "poster.jpg",
    releaseDate = "2013-06-24",
    genres = listOf("Drama")
)
