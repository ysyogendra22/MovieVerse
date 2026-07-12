package com.movieverse.shared

import android.os.Build

actual class Platform {
    actual val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = Platform()
