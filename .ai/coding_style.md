# Coding Style

Conventions actually in use in this codebase — match these rather than introducing new
patterns.

## Kotlin (shared, androidApp)

- **Package-per-layer**: `domain.model`, `domain.repository`, `domain.error`,
  `data.remote`, `data.repository`, `presentation`, `di`. One primary type per file;
  file name matches the type (`Movie.kt` → `Movie`, `MovieRepositoryImpl.kt` →
  `MovieRepositoryImpl`).
- No wildcard imports.
- Data classes for models (`Movie`, `ShowDto`) — no builders, no mutable state in
  domain models.
- DTOs live in `data/remote` and are mapped to domain models via an extension function
  (`ShowDto.toDomain()` in `MovieMapper.kt`), never returned directly past the data layer.
- **Repository implementations own all exception translation.** Nothing above
  `MovieRepositoryImpl` should ever catch a Ktor or `kotlinx.serialization` exception
  type directly — `safeApiCall` is the one place that happens, translating into
  `MovieError`. New repository methods go through the same helper.
- **UI state is a sealed type**, not a handful of booleans on a data class (`isLoading`
  + nullable error, etc.). `MovieListUiState`/`MovieDetailUiState` in
  `shared/presentation` are the pattern: `Loading` / `Success(data)` / `Error(MovieError)`
  (+ `Empty` where "successfully got zero results" is meaningfully different from an
  error).
- **ViewModels take their dependencies as constructor parameters with no default
  value** — DI (Koin) supplies them. Don't give a ViewModel a default-constructed
  fallback like `MovieRepository = SomeDefaultImpl()`; that hides the DI wiring and
  we've already removed one such fallback (`FakeMovieRepository`) once.
- **Koin modules**: one `val xModule = module { ... }` per concern (`networkModule`,
  `repositoryModule`), aggregated in `di/KoinInit.kt`. Platform-specific modules
  (`androidModule`) live in that platform's source set, not in `shared`.
- Comments only where the *why* isn't obvious from the code (a workaround, a
  non-obvious constraint) — not restating what the code does.

## Compose (androidApp)

- Screens split into a stateful `XScreen` composable (injects its ViewModel via
  `koinViewModel()`, collects `StateFlow` via `collectAsState()`) and private stateless
  composables underneath it (`MovieList`, `MovieCard`) that take plain data + lambdas.
- Navigation lives only in the top-level nav host (`MovieVerseApp.kt`) — screens accept
  an `onXClick: (X) -> Unit` callback rather than performing navigation themselves.
- Route keys (`MovieRoute.kt`) are a `sealed interface` implementing `NavKey`, with
  `@Serializable` on each concrete route (required by `rememberNavBackStack`).

## Swift (iosApp)

- ViewModels are `final class X: ObservableObject`, `@MainActor`-annotated, with
  `@Published` state properties — mirrors the Android ViewModel's `StateFlow` shape
  without trying to share the ViewModel class itself across platforms.
- Shared dependencies are pulled via `Injector.shared.xxx()`, not by constructing
  implementations directly in Swift.
- Kotlin `suspend fun` calls appear as completion-handler closures in Swift
  (`repository.getMovies { movies, error in ... }`) — always guard `[weak self]` in the
  closure.
- Navigate by a plain-Swift-Hashable key (e.g. `movie.id: Int32`), not by the Kotlin
  type itself, in `navigationDestination(for:)` — Kotlin-exported classes aren't
  guaranteed to satisfy Swift's `Hashable`.
- **UI state is a native Swift `enum`** (`MovieListUiState`, `MovieDetailUiState`
  declared per-ViewModel-file in Swift), mirroring `shared`'s sealed interface shape
  rather than switching on the Kotlin type directly from Swift. Map completion-handler
  `(data?, error?)` results into this enum inside the ViewModel.
- For error display text, call the shared `error.toUserMessage()` extension (cast the
  bridged `Error` to `MovieError` first: `(error as? MovieError)?.toUserMessage()`) —
  don't reimplement the message strings in Swift.

## General

- Prefer three similar lines over a premature abstraction; don't add a use-case layer,
  a fallback path, or a config flag for something that doesn't exist yet.
- Match the existing file's import ordering and formatting rather than reformatting
  unrelated code in the same change.
