package com.movieverse.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val rating: Double,
    val posterUrl: String,
    val releaseDate: String,
    val genres: List<String>
)
