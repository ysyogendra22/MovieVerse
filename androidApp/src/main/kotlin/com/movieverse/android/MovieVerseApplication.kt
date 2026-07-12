package com.movieverse.android

import android.app.Application
import com.movieverse.android.di.androidModule
import com.movieverse.shared.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MovieVerseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MovieVerseApplication)
            androidLogger()
            modules(androidModule)
        }
    }
}
