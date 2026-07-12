package com.movieverse.android.ui

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Movie.releaseDate is TVMaze's raw "yyyy-MM-dd" (or "Unknown" when TVMaze has no
 * premiere date). Falls back to the original string for anything unparseable rather
 * than showing garbage.
 */
fun String.toReadableDate(): String = try {
    SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(this)?.let {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(it)
    } ?: this
} catch (e: ParseException) {
    this
}
