package com.movieverse.shared.data.remote

import com.movieverse.shared.domain.model.Movie

fun MovieDto.toDomain(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    rating = voteAverage,
    posterUrl = posterUrl
)
