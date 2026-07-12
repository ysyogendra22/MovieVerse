package com.movieverse.shared.di

import com.movieverse.shared.data.repository.MovieRepositoryImpl
import com.movieverse.shared.domain.repository.MovieRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<MovieRepository> { MovieRepositoryImpl(apiClient = get()) }
}
