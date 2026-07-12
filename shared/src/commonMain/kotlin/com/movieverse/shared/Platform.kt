package com.movieverse.shared

/**
 * Each platform (Android, iOS) supplies its own implementation of this
 * interface in its respective source set (androidMain / iosMain).
 * This is the standard KMP "expect/actual" pattern for platform-specific code.
 */
expect class Platform {
    val name: String
}

expect fun getPlatform(): Platform
