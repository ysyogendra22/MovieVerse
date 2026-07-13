package com.movieverse.android.di

import com.movieverse.android.ui.MovieDetailViewModel
import com.movieverse.android.ui.MovieListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val androidModule = module {
    viewModel { MovieListViewModel(repository = get()) }
    viewModel { (movieId: Int) -> MovieDetailViewModel(movieId = movieId, repository = get()) }
}
