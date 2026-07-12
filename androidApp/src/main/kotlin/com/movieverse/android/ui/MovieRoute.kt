package com.movieverse.android.ui

import androidx.navigation3.runtime.NavKey
import com.movieverse.shared.domain.model.Movie
import kotlinx.serialization.Serializable

sealed interface MovieRoute : NavKey {
    @Serializable
    data object List : MovieRoute

    @Serializable
    data class Detail(val movie: Movie) : MovieRoute
}
