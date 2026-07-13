# Glossary

**KMP (Kotlin Multiplatform)** — Kotlin's cross-platform toolkit; lets `shared/` compile
to both a JVM/Android artifact and a native iOS framework from one codebase.

**`expect`/`actual`** — KMP's mechanism for platform-specific implementations of a
common declaration. `Platform.kt` declares `expect class Platform`; `Platform.android.kt`
and `Platform.ios.kt` each provide the `actual` implementation.

**MVVM** — UI (Compose/SwiftUI) observes a ViewModel's state (`StateFlow` on Android,
`@Published` on iOS); the ViewModel holds no UI framework references itself.

**Repository pattern** — UI/ViewModels depend on a `MovieRepository` interface
(`domain/repository`), never on the concrete data source directly. Lets the
implementation swap (mock → real API → cached) without touching callers.

**DTO (Data Transfer Object)** — `ShowDto`, the `@Serializable` shape matching TVMaze's
wire format (JSON), distinct from the domain `Movie` model. Mapped via `toDomain()`.

**TVMaze** — `https://api.tvmaze.com`, a free, keyless TV show API used as this app's
real data source. It's a *TV show* database, not a movie database — used here as
dummy data per the user's spec, with TVMaze's "show" vocabulary mapped onto this app's
"Movie" domain vocabulary in `MovieMapper.kt`. See [decisions.md](decisions.md) #11.

**`MovieError`** — Sealed class in `domain/error` (`NetworkUnavailable`, `ApiError`,
`Timeout`, `EmptyResponse`, `Unknown`) that `MovieRepositoryImpl` translates all
networking/serialization exceptions into. Carries a `toUserMessage()` extension both
platforms call for display text — see [decisions.md](decisions.md) #12–13.

**UiState** — A sealed type (`MovieListUiState`, `MovieDetailUiState` in
`shared/presentation`) representing everything a screen can be in: `Loading`,
`Success(data)`, `Error(MovieError)`, and (list only) `Empty`. Android consumes the
Kotlin type directly; iOS mirrors the same shape as a native Swift `enum` — see
[decisions.md](decisions.md) #14.

**Koin `module` / `single` / `viewModel`** — Koin's DSL: `module { }` declares a group
of dependencies, `single { }` a singleton, `viewModel { }` a Koin-managed
`androidx.lifecycle.ViewModel` factory.

**BOM (Bill of Materials)** — A Gradle platform dependency (`koin-bom`, `compose-bom`)
that pins consistent versions for a family of artifacts, so you only declare one version
number instead of one per artifact.

**AGP (Android Gradle Plugin)** — The Gradle plugin that builds Android
modules/apps. Version 9.x introduced "built-in Kotlin" support and deprecated some
classic APIs still in use here under a temporary bypass (see project_context.md).

**NavKey / NavBackStack / NavEntry** — Navigation3's core types: `NavKey` marks a type
usable as a back-stack entry, `NavBackStack` is the observable back stack list,
`NavEntry` wraps the composable content for one entry.

**Clean-ish Architecture** — This project's layering (domain/data/di) borrows Clean
Architecture's separation of concerns without a full use-case layer — see
[architecture_summary.md](architecture_summary.md) for what was intentionally left out
and why.
