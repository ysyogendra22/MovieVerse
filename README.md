# MovieVerse — Kotlin Multiplatform Reference App

A production-track Kotlin Multiplatform (KMP) app: one Kotlin `shared` module driving
two fully native apps — Jetpack Compose on Android, SwiftUI on iOS. Built as a
reference implementation of a real-world KMP architecture (Clean-ish layering, MVVM,
Repository pattern, DI, real networking, typed error handling), not a toy demo.

Both apps show the same flow: browse a movie list → open a movie's detail — backed by
a live network call to a real (if not movie-specific — see [Data source](#data-source))
API, with full loading/success/error/empty state handling on both platforms.

## Contents

- [Features](#features)
- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [Project structure](#project-structure)
- [Getting started](#getting-started)
- [Testing](#testing)
- [CI/CD](#cicd)
- [Error handling](#error-handling)
- [Data source](#data-source)
- [Extending this starter](#extending-this-starter)
- [Documentation map](#documentation-map)
- [Known limitations](#known-limitations)

## Features

- Movie list: poster, title, rating, release date, overview snippet
- Movie detail: full poster, overview, genres, rating, human-readable release date —
  fetched fresh by id (`GET /shows/{id}`), not reused from the already-loaded list
- List → detail navigation with the title shown immediately (not after the detail
  fetch completes) — Navigation3 on Android, `NavigationStack` on iOS
- Full UI state handling on every screen: loading spinner, success, typed error with
  Retry, and an explicit empty state — not just an `isLoading` boolean
- Real network calls (TVMaze), no mocked data in production code

## Tech stack

| Concern | Choice |
|---|---|
| Multiplatform | Kotlin Multiplatform (Gradle 9.6.1, AGP 9.2.0, Kotlin 2.4.0) |
| Android UI | Jetpack Compose, Material 3 |
| iOS UI | SwiftUI |
| Dependency injection | Koin 4.1.1 |
| Networking | Ktor Client 3.4.0 + kotlinx.serialization 1.11.0 |
| Local storage | *(deliberately skipped — see [Extending this starter](#extending-this-starter))* |
| Navigation | Navigation3 (Android) / native `NavigationStack` (iOS) |
| Image loading | Coil 3 (Android) / native `AsyncImage` (iOS) |
| Concurrency | Kotlin Coroutines, `Flow`, `StateFlow` |
| Architecture | Clean-ish layering (domain/data/di/presentation) + MVVM + Repository pattern |
| Testing | `kotlin.test` + `kotlinx-coroutines-test` (`shared`), + JUnit4 (`androidApp`) |
| CI/CD | GitHub Actions — release notes on merge to `main` |
| Build | Gradle Kotlin DSL |

**Room and Voyager were deliberately left out** — see
[`.ai/decisions.md`](.ai/decisions.md) #4 and #5 for the full reasoning:
- **Room 3.0** (the KMP-first rewrite) shipped only days before this was written — too
  fresh to build a starter on. Add it behind `MovieRepositoryImpl` once it has mileage.
- **Voyager** assumes one shared Compose UI across platforms; this project keeps UI
  fully native per platform, so it doesn't fit — Navigation3 + native `NavigationStack`
  was the natural pairing instead.

## Architecture

```
UI (Compose / SwiftUI)
  → ViewModel (platform-specific: catches MovieError, maps to UiState)
    → MovieRepository (domain interface)
      → MovieRepositoryImpl (translates exceptions → MovieError)
        → MovieApiClient (Ktor)
          → HttpClient (OkHttp on Android / Darwin on iOS) → https://api.tvmaze.com
```

- **Domain layer** (`shared/domain`) — `Movie` model, `MovieRepository` interface,
  `MovieError` sealed error hierarchy. Zero platform or framework dependency.
- **Data layer** (`shared/data`) — `MovieApiClient` (Ktor), `ShowDto`
  (`kotlinx.serialization`, matches TVMaze's real shape), `MovieRepositoryImpl`. Its
  `safeApiCall` helper is the *single* place Ktor/serialization exceptions get
  translated into `MovieError` — nothing above it needs to know networking is Ktor.
- **Presentation layer** (`shared/presentation`) — `MovieListUiState` /
  `MovieDetailUiState` sealed types (`Loading` / `Success` / `Error` / `Empty`) that
  both platforms' ViewModels produce.
- **DI** (`shared/di`) — Koin modules + `initKoin()`. Android calls it from
  `MovieVerseApplication`; iOS calls the Swift-friendly `doInitKoin()` wrapper from
  `iOSApp.swift`'s `init()`.
- **Android** injects via `koinViewModel()` in Compose. **iOS** can't use Compose's
  Koin integration, so it goes through `Injector.shared.movieRepository()` instead,
  and mirrors the shared `UiState` shape as a native Swift `enum` per ViewModel rather
  than switching on the Kotlin sealed type directly (see
  [`.ai/decisions.md`](.ai/decisions.md) #14).
- **Error messages** — `MovieError.toUserMessage()`, defined once in `shared`, called
  directly by both platforms so wording can't drift between them.

Full detail, including every non-obvious interop decision (Swift `Hashable`, Kotlin
default parameters, sealed-class export) and the reasoning behind each, lives in
[`.ai/architecture_summary.md`](.ai/architecture_summary.md) and
[`.ai/decisions.md`](.ai/decisions.md) — this README is the overview, those are the
full record.

## Project structure

```
MovieVerse/
├── shared/src/
│   ├── commonMain/kotlin/com/movieverse/shared/
│   │   ├── domain/
│   │   │   ├── model/Movie.kt
│   │   │   ├── repository/MovieRepository.kt
│   │   │   └── error/MovieError.kt          sealed error hierarchy + toUserMessage()
│   │   ├── data/
│   │   │   ├── remote/                      MovieApiClient, ShowDto, MovieMapper
│   │   │   └── repository/MovieRepositoryImpl.kt
│   │   ├── presentation/                    MovieListUiState, MovieDetailUiState
│   │   ├── di/                              Koin modules, initKoin(), Injector
│   │   ├── Platform.kt / Greeting.kt         expect/actual demo
│   ├── androidMain/, iosMain/                platform `actual` implementations
│   └── commonTest/                           mapper + repository tests
├── androidApp/src/
│   ├── main/kotlin/com/movieverse/android/
│   │   ├── MainActivity.kt, MovieVerseApplication.kt
│   │   ├── di/AndroidModule.kt
│   │   └── ui/                              Screens, ViewModels, MovieRoute, DateFormat
│   └── test/                                 ViewModel tests + FakeMovieRepository
├── iosApp/
│   ├── iosApp.xcodeproj/                    hand-maintained project.pbxproj
│   └── iosApp/                              Views, ViewModels, DateFormatting
├── .github/workflows/                        release-on-merge.yml
├── .ai/                                      architecture docs, decisions, AI-assistant rules
├── docs/                                     product docs (vision, PRD, roadmap-adjacent)
└── settings.gradle.kts                       Gradle module wiring
```

## Getting started

### Prerequisites

- **Android Studio** (Koala or newer) with a JDK 17+ configured (Gradle 9 requires
  JDK 17+ to run)
- **Xcode** 15+ (for the iOS app), macOS only
- No local Gradle/Java install needed — Android Studio provides its own
- Internet access — movie data is a live call to TVMaze, no offline mode

### Run the Android app

1. Open the root `MovieVerse/` folder in Android Studio.
2. Let Gradle sync (first sync downloads dependencies — may take a few minutes).
   - If Android Studio complains about a missing Gradle wrapper jar, use
     **File ▸ Sync Project with Gradle Files** — Android Studio will regenerate it.
3. Select the `androidApp` run configuration and press Run.

### Run the iOS app

1. Open `iosApp/iosApp.xcodeproj` in Xcode.
2. Select a simulator and press Run.
3. Xcode's first build phase (**Compile Kotlin Framework**) invokes Gradle to build
   `shared` into an Apple framework and embeds it automatically — no CocoaPods needed.
   The first build will be slow (compiles the Kotlin/Native framework); subsequent
   builds are incremental.

If Xcode can't find the `shared` module, do one Gradle build first from the terminal:
`./gradlew :shared:embedAndSignAppleFrameworkForXcode` (requires a JDK on your PATH),
then rebuild in Xcode.

## Testing

| Layer | Location | Covers |
|---|---|---|
| Mapper | `shared/src/commonTest/.../MovieMapperTest.kt` | HTML-stripping, entity-unescaping, null fallbacks |
| Repository | `shared/src/commonTest/.../MovieRepositoryImplTest.kt` | `safeApiCall`'s exception→`MovieError` translation, against a `ktor-client-mock` `MockEngine` (test-only dependency — production has no mock engine) |
| ViewModels | `androidApp/src/test/.../MovieListViewModelTest.kt`, `MovieDetailViewModelTest.kt` | `Loading`/`Success`/`Error`/`Empty` transitions + retry, against a `FakeMovieRepository` test double |

Run with `./gradlew test` (Android/JVM + `shared`'s JVM test target) or via Android
Studio's test runner.

**Not covered yet**, deliberately (see [`.ai/decisions.md`](.ai/decisions.md) #16):
- iOS/Swift (XCTest) — no test target exists in `project.pbxproj` yet.
- `MovieError.Timeout` specifically — reliably triggering Ktor's real timeout plugin
  in a unit test is timing-dependent; the other four `MovieError` branches are covered.

## CI/CD

[`.github/workflows/release-on-merge.yml`](.github/workflows/release-on-merge.yml)
publishes a GitHub Release automatically whenever a PR is merged into `main`:

- Tag: `v{date}-{short-sha}` (e.g. `v2026.07.15-a1b2c3d`)
- Release notes: PR title, number, author, merge commit, and description
- No secrets to configure — uses the built-in `GITHUB_TOKEN`
- PR title/body/author are treated as untrusted input and passed through step `env:`
  vars rather than interpolated directly into the script, to avoid a script-injection
  hole (a PR titled with a shell command shouldn't be able to execute it)

Doesn't run on PR *open* — only once a PR actually merges, so releases only ever
reflect real, merged code.

## Error handling

Every network/serialization failure is translated (in `MovieRepositoryImpl.safeApiCall`)
into one of a fixed set of typed errors, each with its own user-facing message
(`MovieError.toUserMessage()`, shared by both platforms):

| `MovieError` | Triggered by | 
|---|---|
| `NetworkUnavailable` | No connection / DNS failure (`kotlinx.io.IOException`) |
| `ApiError(code)` | Non-2xx response (`ClientRequestException` / `ServerResponseException`) |
| `Timeout` | Request or connect timeout |
| `EmptyResponse` | Malformed/unparseable response body |
| `Unknown(cause)` | Anything else |

ViewModels catch `MovieError` and map it into a `UiState.Error`, rendered with a Retry
action on both platforms.

## Data source

Movie data comes from **[TVMaze](https://www.tvmaze.com/api)**
(`https://api.tvmaze.com`) — a free, keyless **TV show** API used as dummy data, not
an actual movie catalog (no free movie API without an application/key was available).
`GET /shows` powers the list, `GET /shows/{id}` powers the detail screen. TVMaze's
"show" vocabulary is mapped onto this app's "Movie" domain vocabulary in
`MovieMapper.kt` — see [`.ai/decisions.md`](.ai/decisions.md) #11.

**To swap in a real movie API** (e.g. TMDB): change `MovieApiClient`'s `baseUrl`,
`ShowDto`'s fields, and `MovieMapper.kt`'s mapping. No architecture change required —
this is a config/mapping change, contained entirely in `shared/data/remote`.

## Extending this starter

- **Go live with a real movie catalog** — see [Data source](#data-source) above.
- **Add local storage** — introduce Room (once it settles) or SQLDelight behind the
  existing `MovieRepository` interface; `MovieRepositoryImpl` is the natural place to
  merge remote + cached data (repository pattern).
- **Add more shared logic** — anything with no UI/platform dependency belongs in
  `shared/domain` or `shared/data` (validation, formatting, use cases).
- **Add a feature** — follow [`.ai/prompts/create_feature.md`](.ai/prompts/create_feature.md)'s
  checklist; it walks through every layer a new feature touches.
- **Add tests** — see [Testing](#testing) above and
  [`.ai/prompts/generate_tests.md`](.ai/prompts/generate_tests.md) for the established
  patterns to reuse.

## Documentation map

This README is the overview. Deeper documentation lives in:

- **[`.ai/`](.ai/)** — architecture reference for AI-assisted development: `rules.md`
  (hard constraints), `instructions.md` (process), `coding_style.md`,
  `architecture_summary.md`, `glossary.md`, `decisions.md` (the full reasoning log
  behind every non-obvious choice in this project), and `prompts/` (task checklists
  for common changes).
- **[`docs/`](docs/)** — product-facing docs (vision, discovery, PRD, feature list,
  MVP definition). Marked as drafts — grounded in what's actually built, with open
  product questions flagged rather than answered speculatively.

## Known limitations

- `shared`'s Android target still uses the classic `com.android.library` +
  `kotlin.multiplatform` combo, which AGP 9.0+ deprecates. Kept alive via
  `android.builtInKotlin=false` / `android.newDsl=false` in `gradle.properties` — a
  **temporary bypass**, not a long-term fix. Migrate to
  `com.android.kotlin.multiplatform.library` before AGP 10 (H2 2026) removes the
  classic combo entirely — see [`.ai/project_context.md`](.ai/project_context.md).
- Data source is TVMaze (TV shows), not an actual movie catalog — see
  [Data source](#data-source).
- No offline support, no search/filter, no favorites/watchlist — see
  [`docs/03-Feature-List.md`](docs/03-Feature-List.md) for the full backlog.
- Error messages are English-only, centralized in `shared` rather than localized per
  platform.
- Package/bundle IDs (`com.movieverse.android`, `com.movieverse.ios`,
  `com.movieverse.shared`) should be renamed before shipping this as a real app.
