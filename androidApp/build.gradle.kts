// AGP 9.2's `android { }` extension function is marked deprecated at ERROR
// severity in the jar itself — unrelated to gradle.properties' newDsl=false
// bypass, which only changes runtime behavior, not the compiled annotation.
@file:Suppress("DEPRECATION", "DEPRECATION_ERROR")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // `shared` forces android.builtInKotlin=false project-wide (see gradle.properties),
    // so this module needs the classic kotlin-android plugin applied explicitly again.
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.composeCompiler)
    // Needed for @Serializable Navigation3 route keys (see ui/MovieRoute.kt).
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    // Must match android.compileOptions below, or compileDebugKotlin fails with
    // "Inconsistent JVM Target Compatibility" (kotlin-android otherwise targets
    // whatever JDK is running Gradle, not the project's Java target).
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

android {
    namespace = "com.movieverse.android"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.movieverse.android"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.core)
    debugImplementation(libs.compose.ui.tooling)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
}
