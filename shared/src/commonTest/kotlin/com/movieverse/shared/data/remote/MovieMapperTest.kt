package com.movieverse.shared.data.remote

import kotlin.test.Test
import kotlin.test.assertEquals

class MovieMapperTest {

    @Test
    fun `maps all fields to the domain model`() {
        val dto = ShowDto(
            id = 1,
            name = "Under the Dome",
            summary = "<p>A small town is sealed off.</p>",
            genres = listOf("Drama", "Science-Fiction"),
            premiered = "2013-06-24",
            rating = RatingDto(average = 8.8),
            image = ImageDto(medium = "medium.jpg", original = "original.jpg")
        )

        val movie = dto.toDomain()

        assertEquals(1, movie.id)
        assertEquals("Under the Dome", movie.title)
        assertEquals("A small town is sealed off.", movie.overview)
        assertEquals(listOf("Drama", "Science-Fiction"), movie.genres)
        assertEquals("2013-06-24", movie.releaseDate)
        assertEquals(8.8, movie.rating)
        assertEquals("original.jpg", movie.posterUrl)
    }

    @Test
    fun `strips HTML tags and unescapes entities from summary`() {
        val dto = ShowDto(
            id = 1,
            name = "Show",
            summary = "<p>Rock &amp; Roll&#39;s <b>best</b> &quot;story&quot;.</p>"
        )

        assertEquals("Rock & Roll's best \"story\".", dto.toDomain().overview)
    }

    @Test
    fun `falls back to a placeholder when summary is null`() {
        val dto = ShowDto(id = 1, name = "Show", summary = null)

        assertEquals("No description available.", dto.toDomain().overview)
    }

    @Test
    fun `falls back to zero rating when rating is null or has no average`() {
        assertEquals(0.0, ShowDto(id = 1, name = "Show", rating = null).toDomain().rating)
        assertEquals(0.0, ShowDto(id = 1, name = "Show", rating = RatingDto(average = null)).toDomain().rating)
    }

    @Test
    fun `prefers the original poster over medium, and falls back to empty string`() {
        assertEquals(
            "original.jpg",
            ShowDto(id = 1, name = "Show", image = ImageDto(medium = "medium.jpg", original = "original.jpg"))
                .toDomain().posterUrl
        )
        assertEquals(
            "medium.jpg",
            ShowDto(id = 1, name = "Show", image = ImageDto(medium = "medium.jpg", original = null)).toDomain().posterUrl
        )
        assertEquals("", ShowDto(id = 1, name = "Show", image = null).toDomain().posterUrl)
    }

    @Test
    fun `falls back to Unknown when premiered is null`() {
        assertEquals("Unknown", ShowDto(id = 1, name = "Show", premiered = null).toDomain().releaseDate)
    }
}
