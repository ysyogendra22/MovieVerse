package com.movieverse.shared.data.remote

import com.movieverse.shared.domain.model.Movie

fun ShowDto.toDomain(): Movie = Movie(
    id = id,
    title = name,
    overview = summary?.let(::stripHtml) ?: "No description available.",
    rating = rating?.average ?: 0.0,
    posterUrl = image?.original ?: image?.medium ?: "",
    releaseDate = premiered ?: "Unknown",
    genres = genres
)

// TVMaze's `summary` field is HTML ("<p>...</p>"); this is a plain-text UI, so strip
// tags and unescape the handful of entities TVMaze actually uses rather than pulling
// in a full HTML parser for one field.
private fun stripHtml(html: String): String =
    html
        .replace(Regex("<[^>]*>"), "")
        .replace("&amp;", "&")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace("&apos;", "'")
        .trim()
