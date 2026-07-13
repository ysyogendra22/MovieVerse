package com.movieverse.shared.data.remote

import kotlinx.serialization.Serializable

/**
 * Matches https://api.tvmaze.com/shows and /shows/{id} exactly — a bare array for
 * the list endpoint, no envelope. TVMaze is a free, keyless TV show API used here as
 * dummy data; field names ("name", "summary") are TVMaze's, mapped to this app's
 * "Movie" vocabulary in MovieMapper.kt.
 */
@Serializable
data class ShowDto(
    val id: Int,
    val name: String,
    val summary: String? = null,
    val genres: List<String> = emptyList(),
    val premiered: String? = null,
    val rating: RatingDto? = null,
    val image: ImageDto? = null
)

@Serializable
data class RatingDto(
    val average: Double? = null
)

@Serializable
data class ImageDto(
    val medium: String? = null,
    val original: String? = null
)
