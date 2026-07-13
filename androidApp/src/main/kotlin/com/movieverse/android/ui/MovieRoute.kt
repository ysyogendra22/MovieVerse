package com.movieverse.android.ui

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface MovieRoute : NavKey {
    @Serializable
    data object List : MovieRoute

    @Serializable
    data class Detail(val movieId: Int, val movieTitle: String) : MovieRoute
}
