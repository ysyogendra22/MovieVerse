# Architecture Summary

Clean-ish layering (domain/data/di/presentation — no dedicated use-case layer, since a
single repository call per screen is enough for what exists today) + MVVM + Repository
pattern, with native UI per platform sharing one Kotlin business-logic module.

```
shared/src/commonMain/kotlin/com/movieverse/shared/
├── domain/
│   ├── model/Movie.kt                    entity — no framework dependency
│   ├── repository/MovieRepository.kt     interface: getMovies(), getMovieDetail(id)
│   └── error/MovieError.kt               sealed error hierarchy + toUserMessage()
├── data/
│   ├── remote/
│   │   ├── ShowDto.kt                    @Serializable, matches TVMaze's real JSON shape
│   │   ├── MovieMapper.kt                ShowDto.toDomain() (incl. HTML-stripping summary)
│   │   └── MovieApiClient.kt             Ktor HttpClient wrapper, hits api.tvmaze.com
│   └── repository/MovieRepositoryImpl.kt implements MovieRepository, translates
│                                           Ktor/serialization exceptions into MovieError
├── presentation/
│   ├── MovieListUiState.kt               sealed: Loading / Success / Error / Empty
│   └── MovieDetailUiState.kt             sealed: Loading / Success / Error
├── di/
│   ├── NetworkModule.kt                  single { HttpClient }, single { MovieApiClient }
│   ├── RepositoryModule.kt               single<MovieRepository> { MovieRepositoryImpl }
│   ├── KoinInit.kt                       initKoin() / doInitKoin() (Swift entry point)
│   └── Injector.kt                       KoinComponent facade for Swift
├── Platform.kt / Greeting.kt             expect/actual demo, unrelated to the movie feature
```

## Data flow

```
UI (Compose / SwiftUI)
  → ViewModel (platform-specific: catches MovieError, maps to UiState)
    → MovieRepository (domain interface)
      → MovieRepositoryImpl (data layer: safeApiCall translates exceptions -> MovieError)
        → MovieApiClient (Ktor)
          → HttpClient(OkHttp on Android / Darwin on iOS) → https://api.tvmaze.com
```

## Dependency injection (Koin)

- `shared/di` defines `networkModule` + `repositoryModule`; `sharedModules` aggregates
  them; `initKoin(config)` starts Koin with a platform-supplied config hook.
- **Android**: `MovieVerseApplication.onCreate()` calls
  `initKoin { androidContext(this); androidLogger(); modules(androidModule) }`.
  `androidModule` (in `androidApp/di`) declares `viewModel { MovieListViewModel(get()) }`
  and a parameterized `viewModel { (id: Int) -> MovieDetailViewModel(id, get()) }`.
  Composables get them via `koinViewModel()` / `koinViewModel { parametersOf(movieId) }`.
- **iOS**: `iOSApp.init()` calls `KoinInitKt.doInitKoin()` (a zero-arg wrapper around
  `initKoin()`, since Kotlin default parameters and receiver-typed function params
  don't cross into Swift cleanly). `MovieListViewModel.swift` / `MovieDetailViewModel.swift`
  call `Injector.shared.movieRepository()` directly (Swift can't use `koinViewModel()`).

## Networking

`MovieApiClient` hits **TVMaze** (`https://api.tvmaze.com`) — a free, keyless TV show
API used as real (if slightly mismatched-domain) dummy data; `GET /shows` for the list,
`GET /shows/{id}` for detail. `HttpClient` is declared in `NetworkModule.kt` with no
explicit engine — `androidMain` adds `ktor-client-okhttp`, `iosMain` adds
`ktor-client-darwin`, and Ktor auto-detects whichever is on each platform's classpath.
`expectSuccess = true` + `HttpTimeout` are configured so non-2xx responses and timeouts
throw exceptions `MovieRepositoryImpl` can translate (see below), instead of requiring
manual status-code checks everywhere.

## Error handling

`MovieRepositoryImpl.safeApiCall` is the single place that translates Ktor/serialization
exceptions into `MovieError` — nothing above the data layer knows networking is Ktor,
or that failures originate as `ClientRequestException` / `HttpRequestTimeoutException` /
`kotlinx.io.IOException` / `SerializationException`. Catch order matters: timeout
exception types are `IOException` subtypes, so they're caught before the general
`IOException` catch; `CancellationException` is rethrow-first so cancellation isn't
swallowed.

`MovieError` is a sealed class (`NetworkUnavailable`, `ApiError(code)`, `Timeout`,
`EmptyResponse`, `Unknown(cause)`) with a `toUserMessage()` extension defined once in
`shared` — both platforms call this directly rather than each maintaining their own
copy of the display strings (see decisions.md for why this lives in `shared` rather
than per-platform string resources for now).

ViewModels catch `MovieError` and map it into a `UiState` (`Loading` / `Success` /
`Error` / `Empty` for the list; no `Empty` case for detail, since a single item either
loads or doesn't). Android exposes `shared`'s `MovieListUiState`/`MovieDetailUiState`
sealed interfaces directly via `StateFlow`. **iOS re-declares the same shape as a
native Swift `enum`** in each ViewModel file rather than switching on the Kotlin sealed
type from Swift — see decisions.md for why.

## Navigation & UI

- **Android**: Navigation3 (`androidx.navigation3`). `MovieVerseApp.kt` owns a
  `NavBackStack<NavKey>` and an `entryProvider` mapping `MovieRoute.List` /
  `MovieRoute.Detail(movieId)` to `NavEntry`s. Route keys live in `MovieRoute.kt`.
  The detail route carries only the `movieId`, not a full `Movie` — `MovieDetailScreen`
  fetches it fresh via `MovieDetailViewModel`.
- **iOS**: SwiftUI's native `NavigationStack` + `navigationDestination(for:)`, keyed by
  `movie.id` (`Int32`) rather than the `Movie` object itself (see coding_style.md).
  `MovieDetailView(movieId:)` fetches independently via its own `MovieDetailViewModel`.
- **Images**: Coil 3 (`AsyncImage` composable) on Android; SwiftUI's built-in
  `AsyncImage` on iOS. No shared/multiplatform image-loading code.

## Testing

- **`shared/src/commonTest`**: `MovieMapperTest` (HTML-stripping, null fallbacks —
  pure function, no mocking needed) and `MovieRepositoryImplTest` (exercises
  `safeApiCall`'s exception→`MovieError` translation against a `ktor-client-mock`
  `MockEngine` — success/empty/404/500/malformed-body; the `Timeout` branch is
  deliberately not covered here, see decisions.md #16). `ktor-client-mock` is a
  **test-only** dependency (`commonTest`) — production code has no mock engine.
- **`androidApp/src/test`**: `MovieListViewModelTest`/`MovieDetailViewModelTest`
  against `FakeMovieRepository` (a mutable in-memory test double, not a production
  fallback — see coding_style.md), using `MainDispatcherRule` to make `viewModelScope`
  runnable in a plain JVM test. Because these tests use `UnconfinedTestDispatcher`,
  the fake repository's calls aren't truly async, so `uiState.value` is already the
  final state (`Success`/`Error`/`Empty`) by the time `load()` returns — the tests
  assert outcomes, not the transient `Loading` state.
- **iOS**: no XCTest target yet — see decisions.md #16.

## What's deliberately not here

- **Local storage / caching** — no Room, no SQLDelight. `MovieRepositoryImpl` is the
  natural place to add a cache later (repository merges remote + local, still behind
  the same `MovieRepository` interface).
- **A use-case layer** — one repository method per screen is the whole "business
  logic" so far; add use cases when there's actual orchestration across multiple
  repositories/rules to justify one, not preemptively.
- **Localized error strings** — `toUserMessage()` is English-only, centralized in
  `shared`. Fine for now; a production app should move this to each platform's own
  string resources.

See [decisions.md](decisions.md) for the reasoning behind each of these choices.
