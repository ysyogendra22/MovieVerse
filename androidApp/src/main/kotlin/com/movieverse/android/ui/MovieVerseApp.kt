package com.movieverse.android.ui

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay

@Composable
fun MovieVerseApp() {
    MovieVerseTheme {
        // rememberNavBackStack() always returns NavBackStack<NavKey>, not something
        // generic inferred from the initial key's specific type.
        val backStack: NavBackStack<NavKey> = rememberNavBackStack(MovieRoute.List)

        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = { route ->
                when (route) {
                    is MovieRoute.List -> NavEntry(route) {
                        MovieListScreen(
                            onMovieClick = { movieId, movieTitle ->
                                backStack.add(MovieRoute.Detail(movieId, movieTitle))
                            }
                        )
                    }

                    is MovieRoute.Detail -> NavEntry(route) {
                        MovieDetailScreen(
                            movieId = route.movieId,
                            movieTitle = route.movieTitle,
                            onBack = { backStack.removeLastOrNull() }
                        )
                    }

                    else -> error("Unknown route: $route")
                }
            }
        )
    }
}
