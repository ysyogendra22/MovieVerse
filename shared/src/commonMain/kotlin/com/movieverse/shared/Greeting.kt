package com.movieverse.shared

class Greeting {
    private val platform = getPlatform()

    fun greet(): String = "Welcome to MovieVerse on ${platform.name}!"
}
