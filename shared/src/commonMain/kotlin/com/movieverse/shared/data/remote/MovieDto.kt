package com.movieverse.shared.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoviesResponseDto(
    val results: List<MovieDto>
)

@Serializable
data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("poster_url") val posterUrl: String
)
