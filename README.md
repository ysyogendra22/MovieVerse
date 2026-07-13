# MovieVerse — Kotlin Multiplatform Starter

A production-track starting point for a Kotlin Multiplatform (KMP) app with:

- **`shared/`** — Kotlin business logic used by both apps: domain models, repository
  interfaces/implementations, networking, DI, `expect`/`actual` platform code
- **`androidApp/`** — native Android app (Jetpack Compose + Material 3)
- **`iosApp/`** — native iOS app (SwiftUI)

Both apps show a movie list → movie detail flow backed by the same shared repository,
to demonstrate the architecture end to end.

## Stack

| Concern | Choice |
|---|---|
| Multiplatform | Kotlin Multiplatform (Gradle 9.6.1, AGP 9.2.0, Kotlin 2.4.0) |
| Android UI | Jetpack Compose, Material 3 |
| iOS UI | SwiftUI |
| Dependency injection | Koin |
| Networking | Ktor Client + kotlinx.serialization |
| Local storage | *(skipped — see below)* |
| Navigation | Navigation3 (Android) / native `NavigationStack` (iOS) |
| Image loading | Coil 3 (Android) / native `AsyncImage` (iOS) |
| Concurrency | Kotlin Coroutines, `Flow`, `StateFlow` |
| Architecture | Clean-ish layering (domain/data/di) + MVVM + Repository pattern |
| Build | Gradle Kotlin DSL |

**Room and Voyager were deliberately left out:**
- **Room 3.0** (the new KMP-first rewrite) shipped only days before this was written —
  too fresh to build a starter on. The repository is remote-only for now; add Room as
  a local cache in `data/` behind the existing `MovieRepository` interface once it has
  more mileage, or once you actually need offline support.
- **Voyager** assumes a single shared Compose UI. This starter keeps UI fully native per
  platform (Compose on Android, SwiftUI on iOS), so **Navigation3** (Android-only) fits
  better than a cross-platform nav library; iOS just uses `NavigationStack` natively.

## Project layout

```
MovieVerse/
├── shared/src/commonMain/kotlin/com/movieverse/shared/
│   ├── domain/
│   │   ├── model/Movie.kt              domain entity
│   │   ├── repository/MovieRepository.kt   repository interface
│   │   └── error/MovieError.kt         sealed error hierarchy + toUserMessage()
│   ├── data/
│   │   ├── remote/    Ktor API client (hits TVMaze), DTOs, DTO→domain mapper
│   │   └── repository/MovieRepositoryImpl.kt   translates exceptions -> MovieError
│   ├── presentation/    MovieListUiState / MovieDetailUiState (Loading/Success/Error/Empty)
│   ├── di/             Koin modules, initKoin(), Injector (Swift entry point)
│   ├── Platform.kt      expect/actual platform-detection demo
│   └── Greeting.kt
├── shared/src/androidMain, iosMain/   platform `actual` implementations
├── androidApp/          Android app (Compose, Navigation3, Koin, Coil)
├── iosApp/              iOS app (SwiftUI) + Xcode project
└── settings.gradle.kts  Gradle module wiring
```

## Toolchain versions

- Gradle 9.6.1, Android Gradle Plugin 9.2.0, Kotlin 2.4.0
- `compileSdk`/`targetSdk` 36+ (Android 16)
- `shared` combines `com.android.library` with `org.jetbrains.kotlin.multiplatform`,
  which AGP 9.0+ refuses to apply unless you migrate to the new
  `com.android.kotlin.multiplatform.library` plugin. That plugin's DSL is still
  changing shape across AGP 9.x releases, so `gradle.properties` sets
  `android.builtInKotlin=false` and `android.newDsl=false` as a **temporary bypass**
  back to AGP's pre-9.0 Kotlin handling — this is project-wide, which is why
  `androidApp` also applies the classic `org.jetbrains.kotlin.android` plugin again
  instead of relying on AGP 9's built-in Kotlin support.
- Migrate `shared` to `com.android.kotlin.multiplatform.library` before AGP 10
  (H2 2026) removes the classic combo entirely, then these bypass flags and the
  explicit `kotlin-android` plugin on `androidApp` can come back out.

## Prerequisites

- **Android Studio** (Koala or newer) with a JDK 17+ configured (Gradle 9 requires JDK 17+ to run)
- **Xcode** 15+ (for the iOS app), macOS only
- No local Gradle/Java install needed — Android Studio provides its own
- Internet access — movie data comes from a live call to TVMaze
  (`https://api.tvmaze.com`), a free, keyless TV show API used as dummy data (see
  "How the sharing works" below); there's no offline mode

## Running the Android app

1. Open the root `MovieVerse/` folder in Android Studio.
2. Let Gradle sync (first sync downloads dependencies — may take a few minutes).
   - If Android Studio complains about a missing Gradle wrapper jar, use
     **File ▸ Sync Project with Gradle Files** — Android Studio will regenerate it.
3. Select the `androidApp` run configuration and press Run.

## Running the iOS app

1. Open `iosApp/iosApp.xcodeproj` in Xcode.
2. Select a simulator and press Run.
3. Xcode's first build phase (**Compile Kotlin Framework**) invokes Gradle to build
   `shared` into an Apple framework and embeds it automatically — no CocoaPods needed.
   The first build will be slow (compiles the Kotlin/Native framework); subsequent
   builds are incremental.

If Xcode can't find the `shared` module, do one Gradle build first from the terminal:
`./gradlew :shared:embedAndSignAppleFrameworkForXcode` (requires a JDK on your PATH),
then rebuild in Xcode.

## How the sharing works

- **Domain layer** (`shared/domain`): `Movie` model, `MovieRepository` interface
  (`getMovies()`, `getMovieDetail(id)`), `MovieError` sealed error hierarchy — no
  platform or framework dependency.
- **Data layer** (`shared/data`): `MovieApiClient` (Ktor) hits TVMaze
  (`GET /shows`, `GET /shows/{id}`); `ShowDto` (`kotlinx.serialization`) matches its
  real response shape; `MovieRepositoryImpl` maps DTOs to domain models and translates
  Ktor/serialization exceptions into `MovieError` (its `safeApiCall` helper is the one
  place that happens — nothing above it needs to know networking is Ktor).
  `HttpClient` is declared with no explicit engine; `androidMain`/`iosMain` each add
  their own engine (`ktor-client-okhttp` / `ktor-client-darwin`) and Ktor auto-detects
  whichever is on the classpath.
- **Presentation layer** (`shared/presentation`): `MovieListUiState`/`MovieDetailUiState`
  sealed types (`Loading`/`Success`/`Error`/`Empty`) that both platforms' ViewModels
  produce from repository calls + caught `MovieError`s.
- **DI** (`shared/di`): Koin modules (`networkModule`, `repositoryModule`) plus
  `initKoin()`. Android calls it from `MovieVerseApplication` (with `androidContext`);
  iOS calls the Swift-friendly `doInitKoin()` from `iOSApp.swift`'s `init()`.
- **Platform code**: `Platform.kt` declares an `expect class Platform` — each platform
  supplies its own `actual` (`Platform.android.kt`, `Platform.ios.kt`).
- **Android** injects dependencies with `koinViewModel()` in Compose
  (`ui/MovieListViewModel.kt`/`MovieDetailViewModel.kt` take `MovieRepository` as a
  constructor param; the detail one also takes a `movieId` via Koin's parameter DSL).
- **iOS** can't use Compose's Koin integration, so it goes through
  `Injector.shared.movieRepository()` (`di/Injector.kt`) instead. Kotlin `suspend`
  functions appear in Swift as completion-handler callbacks. iOS mirrors
  `MovieListUiState`/`MovieDetailUiState` as native Swift `enum`s in each ViewModel
  file rather than switching on the Kotlin sealed type directly.
- **Error messages**: `MovieError.toUserMessage()` (in `shared`) is called by both
  platforms directly, so wording can't drift between them — see
  `.ai/decisions.md` #13.
- **Navigation**: Android uses Navigation3 (`ui/MovieRoute.kt`, `ui/MovieVerseApp.kt`)
  with a list → detail back stack, keyed by `movieId`; iOS uses SwiftUI's native
  `NavigationStack` + `navigationDestination(for:)`, keyed by the movie's `id` rather
  than the Kotlin `Movie` object (Kotlin-exported classes aren't guaranteed `Hashable`
  in Swift). Both platforms fetch the detail screen's data fresh by id rather than
  reusing the already-loaded list item.

## Extending this starter

- **Swap to a real movie catalog**: TVMaze is a TV show API used as dummy data (see
  `.ai/decisions.md` #11) — change `MovieApiClient`'s `baseUrl`, `ShowDto`'s fields,
  and `MovieMapper.kt`'s mapping to point at an actual movie API (e.g. TMDB).
- **Add local storage**: introduce Room (once it settles) or SQLDelight behind the
  existing `MovieRepository` interface — `MovieRepositoryImpl` is the natural place to
  merge remote + cached data (repository pattern).
- **Add more shared logic**: put anything with no UI/platform dependency in
  `shared/domain` or `shared/data` (validation, formatting, use cases).
- **Tests**: add shared logic tests under `shared/src/commonTest` — they run on both
  platforms' test targets.

## Notes

- This starter deliberately keeps UI native (Compose on Android, SwiftUI on iOS) rather
  than using Compose Multiplatform, so each app can fully use platform-native UI/UX
  while sharing only business logic.
- Package/bundle IDs: `com.movieverse.android` (Android), `com.movieverse.ios` (iOS),
  `com.movieverse.shared` (shared module) — rename these before shipping.
