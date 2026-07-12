package com.movieverse.shared.domain.error

/**
 * What the UI actually needs to distinguish — not a raw exception. Thrown by
 * [com.movieverse.shared.data.repository.MovieRepositoryImpl], which is where Ktor's
 * own exceptions get translated into this (see MovieRepositoryImpl.safeApiCall) so
 * nothing above the data layer needs to know networking is Ktor.
 */
sealed class MovieError : Exception() {
    data object NetworkUnavailable : MovieError()
    data class ApiError(val code: Int) : MovieError()
    data object Timeout : MovieError()
    data object EmptyResponse : MovieError()
    data class Unknown(override val cause: Throwable) : MovieError()
}

/**
 * Centralized here (rather than per-platform) so Android and iOS never drift on
 * wording, and so Swift doesn't need to pattern-match MovieError's Kotlin sealed
 * subtypes at all — Kotlin/Native exports this extension as a plain callable method
 * on the exported MovieError class. A localized production app would move this to
 * each platform's own string resources instead.
 */
fun MovieError.toUserMessage(): String = when (this) {
    is MovieError.NetworkUnavailable -> "No internet connection. Please check your network and try again."
    is MovieError.ApiError -> "Something went wrong (error $code). Please try again."
    is MovieError.Timeout -> "The request took too long. Please try again."
    is MovieError.EmptyResponse -> "We couldn't load data right now. Please try again."
    is MovieError.Unknown -> "Something unexpected happened. Please try again."
}
