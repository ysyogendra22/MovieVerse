package com.movieverse.shared.di

import com.movieverse.shared.domain.repository.MovieRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * Android reaches Koin via `koinViewModel()` directly. Swift can't use that
 * Compose API, so iOS pulls dependencies through this instead, e.g.
 * `Injector.shared.movieRepository()`.
 */
object Injector : KoinComponent {
    fun movieRepository(): MovieRepository = get()
}
