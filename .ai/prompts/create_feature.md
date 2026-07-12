# Prompt: Create a Feature

Use when adding a new feature (a new domain entity + screen, e.g. "add a favorites
list" or "add movie search").

## Checklist

1. **Domain** (`shared/domain`): add the model (`model/X.kt`, plain data class, no
   framework deps), the repository interface (`repository/XRepository.kt`,
   `suspend fun` methods only), and — if it can fail in ways the UI needs to
   distinguish — extend or reuse `MovieError` (`domain/error/MovieError.kt`) rather
   than inventing a parallel error type.
2. **Data** (`shared/data`): add the DTO (`remote/XDto.kt`, `@Serializable`, matching
   the *real* API response — fetch the actual endpoint and check its shape before
   writing the DTO, don't assume an envelope/field names), a mapper
   (`remote/XMapper.kt`, `XDto.toDomain()`), the API client (`remote/XApiClient.kt`,
   wraps `HttpClient`), and the repository implementation (`repository/XRepositoryImpl.kt`).
   Route all calls through a `safeApiCall`-style helper (see
   `MovieRepositoryImpl.safeApiCall`) that translates Ktor/serialization exceptions
   into `MovieError` — don't let raw Ktor exceptions escape the data layer.
3. **Presentation** (`shared/presentation`): add a `XUiState` sealed interface
   (`Loading`/`Success`/`Error`/`Empty` as applicable — see `MovieListUiState`) if the
   screen has meaningfully different states to render, rather than a boolean+nullable
   data class.
4. **DI** (`shared/di`): register the new pieces in a module (new or existing), add it
   to `sharedModules` in `KoinInit.kt`. If iOS needs direct access, add an accessor to
   `Injector.kt`.
5. **Android UI**: stateful `XScreen` composable (injects `XViewModel` via
   `koinViewModel()`, `koinViewModel { parametersOf(...) }` if it needs a runtime
   argument like an id) + stateless child composables, rendering each `XUiState` case
   explicitly (with a Retry action on `Error`). Add the ViewModel to
   `androidApp/di/AndroidModule.kt`. If it needs its own route, add it to
   `MovieRoute.kt` and wire it into `MovieVerseApp.kt`'s `entryProvider`.
6. **iOS UI**: `ObservableObject` ViewModel pulling dependencies from `Injector`,
   mapping completion-handler results into a native Swift `enum` mirroring the shared
   `XUiState` shape (don't try to switch on the Kotlin sealed type directly from
   Swift). For error text, call the shared `error.toUserMessage()` extension rather
   than reimplementing messages in Swift. Plus the SwiftUI view. Register any new
   `.swift` file in `project.pbxproj` (see `instructions.md`) and validate with
   `plutil -lint`.
7. Cross-check versions of any new dependency before adding it (rules.md #1).
8. After implementing, tell the user to clean-build + fully reinstall the Android app
   before testing (rules.md #7), and to build in Xcode for iOS since neither can be
   verified from this environment.

Reference implementation: the movie list → detail flow (`Movie`, `MovieRepository`,
`MovieError`, `MovieListUiState`/`MovieDetailUiState`, `MovieApiClient`,
`MovieListScreen`/`MovieDetailScreen`, `MovieListViewModel.swift`/`MovieDetailView.swift`)
follows exactly this shape end to end, including a real (TVMaze) network call and full
error/empty/loading state handling.
