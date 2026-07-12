# Project Context — MovieVerse

## What this is

MovieVerse is a Kotlin Multiplatform (KMP) reference/starter app: a movie browsing app
(list → detail) with native UI on both platforms and shared business logic. It exists
to demonstrate a production-track KMP architecture (Clean-ish layering, MVVM,
Repository pattern, DI, real networking, typed error handling) rather than to ship a
finished product — the data source (TVMaze) is real but is a TV show API being used as
free dummy data, not an actual movie catalog. See [decisions.md](decisions.md) #11.

- **Repo**: `github.com/ysyogendra22/MovieVerse`
- **Current branch**: `beta/v-1.0.1`
- **Status**: scaffolded and architected; Android verified running via Compose; iOS
  has not been build-verified in this environment (no local Xcode/JDK toolchain access
  during development — see [decisions.md](decisions.md)).

## Modules

- **`shared/`** — Kotlin business logic: domain models, repository interfaces/impls,
  Ktor networking, Koin DI. Compiles to a JVM/Android target and an iOS framework.
- **`androidApp/`** — native Android app (Jetpack Compose, Material 3, Navigation3).
- **`iosApp/`** — native iOS app (SwiftUI), consumes `shared` as an Apple framework via
  Gradle's direct integration (no CocoaPods).

## Stack at a glance

Gradle 9.6.1 · AGP 9.2.0 · Kotlin 2.4.0 · Koin 4.1.1 · Ktor 3.4.0 ·
kotlinx.serialization 1.11.0 · Coil 3.5.0 · Navigation3 1.1.4 · compileSdk/targetSdk 36+.

See [architecture_summary.md](architecture_summary.md) for how these fit together, and
[decisions.md](decisions.md) for why each was chosen over its alternatives.

## Known rough edges (read before touching build files)

- `shared`'s Android target still uses the classic `com.android.library` +
  `kotlin.multiplatform` combo, which AGP 9.0+ deprecates. It's kept alive via
  `android.builtInKotlin=false` / `android.newDsl=false` in `gradle.properties` — a
  **temporary bypass**, not a long-term fix. See [decisions.md](decisions.md) and the
  README's "Toolchain versions" section.
- Movie data comes from a real network call to TVMaze (`https://api.tvmaze.com`) — no
  mock engine anymore. The app needs internet access to load anything.
- No automated tests exist yet.
