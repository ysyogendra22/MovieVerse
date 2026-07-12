# Prompt: Generate Tests

Unit tests exist for the mapper, repository, and Android ViewModels — use these as the
concrete pattern to follow rather than inventing a new one:

- `shared/src/commonTest/kotlin/.../data/remote/MovieMapperTest.kt`
- `shared/src/commonTest/kotlin/.../data/repository/MovieRepositoryImplTest.kt`
- `androidApp/src/test/kotlin/.../ui/MovieListViewModelTest.kt`
- `androidApp/src/test/kotlin/.../ui/MovieDetailViewModelTest.kt`
- `androidApp/src/test/kotlin/.../ui/FakeMovieRepository.kt` (the test double both
  ViewModel tests share)
- `androidApp/src/test/kotlin/.../MainDispatcherRule.kt` (makes `viewModelScope`
  runnable in a plain JVM test)

**Not covered yet** — see [`../decisions.md`](../decisions.md) #16 for why:
- iOS/Swift (XCTest) — no test target exists in `project.pbxproj`.
- `MovieError.Timeout` specifically — timing-dependent to trigger reliably against
  Ktor's real `HttpTimeout` plugin; the other four `MovieError` branches are covered.

## Where tests go

- **`shared/src/commonTest`** — domain logic, mappers, repository implementations.
  Runs against both the Android (JVM) and iOS targets.
- **`androidApp/src/test`** — Android ViewModels (they live in `androidApp`, not
  `shared`, so their tests do too). Plain JVM tests, no Robolectric/instrumentation
  needed — `androidx.lifecycle.ViewModel` is testable directly once
  `MainDispatcherRule` swaps in a test dispatcher for `Dispatchers.Main`.
- Platform-specific test source sets (`androidUnitTest`, `iosTest`) only for code
  that's actually platform-specific (the `Platform.android.kt` / `Platform.ios.kt`
  `actual` implementations) — nothing there yet.

## Patterns to reuse

- **Repository tests**: build a real `HttpClient` against `MockEngine`
  (`ktor-client-mock`, `commonTest`-only — production has no mock engine, see
  decisions.md #11) mirroring `NetworkModule`'s config (`expectSuccess = true`,
  `ContentNegotiation` + `Json`), then assert on `MovieRepositoryImpl`'s output or the
  specific `MovieError` thrown (`assertFailsWith<MovieError.ApiError> { ... }`).
- **ViewModel tests**: construct the ViewModel directly with `FakeMovieRepository`
  (no Koin involved) and assert on `uiState.value`. Add `@get:Rule val
  mainDispatcherRule = MainDispatcherRule()` to any test touching a ViewModel.
  `FakeMovieRepository`'s fields are `var`s so a test can flip from failing to
  succeeding mid-test and call `viewModel.load()` again — exactly what a Retry button
  does.
- **Mapper/pure-function tests**: no mocking, no coroutines — just call the function
  and assert.

## Next priorities

1. DTO serialization round-trip (`ShowDto` through `kotlinx.serialization.json.Json`)
   to catch `@SerialName` mismatches early if TVMaze's shape ever changes.
2. Compose/SwiftUI UI tests — still lower priority until there's more than one
   screen's worth of interaction logic to justify them.
3. iOS XCTest target, if iOS-side coverage becomes a priority (see decisions.md #16
   for why it wasn't set up alongside the Kotlin tests).

## Conventions

- Use `kotlin.test` (`commonTest`) / `kotlin.test` + JUnit4 (`androidApp` via
  `kotlin("test-junit")`) rather than introducing a new assertion library.
- Name test files `XTest.kt` mirroring the file under test.
- Don't mock what you don't own if you can avoid it — prefer the real
  `kotlinx.serialization` `Json` instance over mocking serialization behavior.
- Test doubles (`FakeMovieRepository`) are fine in test code; they are not the same
  thing as the production-code default-fallback anti-pattern rules.md #9 warns against
  — a fake explicitly passed by test code is normal, a hidden default in production
  code is not.
