package com.movieverse.shared.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

val sharedModules = listOf(networkModule, repositoryModule)

/**
 * Called once at app startup. [config] lets each platform add its own setup
 * (e.g. Android's `androidContext(this)`) without shared code knowing about it.
 */
fun initKoin(config: KoinAppDeclaration = {}) {
    startKoin {
        config()
        modules(sharedModules)
    }
}

/**
 * Swift-callable entry point. Kotlin default parameters and function types
 * with a receiver (like [KoinAppDeclaration]) don't map cleanly to Swift, so
 * iOS calls this plain zero-arg function instead of [initKoin] directly.
 */
fun doInitKoin() {
    initKoin()
}
