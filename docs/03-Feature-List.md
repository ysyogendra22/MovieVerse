# 03 — Feature List

> Tracks features against [02-PRD.md](02-PRD.md)'s requirements. Update status as work
> lands — this should stay close to reality, not aspirational.

## Shipped

- **Movie list** — `androidApp/ui/MovieListScreen.kt`, `iosApp/ContentView.swift`.
  Poster thumbnail, title, overview snippet, release date, rating.
- **Movie detail** — `androidApp/ui/MovieDetailScreen.kt`, `iosApp/MovieDetailView.swift`.
  Full poster, overview, genres, rating, release date. Fetched fresh by id
  (`GET /shows/{id}`), not reused from the already-loaded list.
- **List → detail navigation** — Navigation3 on Android, `NavigationStack` on iOS,
  keyed by `movieId` (not the full `Movie` object).
- **Real data source** — TVMaze (`https://api.tvmaze.com`), free and keyless. See
  [`../.ai/decisions.md`](../.ai/decisions.md) #11.
- **Loading / Success / Error / Empty state** — every screen models its full state
  explicitly (`MovieListUiState`/`MovieDetailUiState` in `shared/presentation`) instead
  of an ad hoc `isLoading` boolean. See [`../.ai/glossary.md`](../.ai/glossary.md).
- **Typed error handling with retry** — network unavailable, API error, timeout,
  malformed/empty response, and unexpected exceptions are all distinguished
  (`MovieError` in `shared/domain/error`) and shown with a Retry action on both
  platforms.

## Backlog (not started, no priority order implied)

- **Search / filter** — no UI or repository method exists for this yet.
- **Offline cache** — deferred until Room (or SQLDelight) is added behind
  `MovieRepositoryImpl`; see [`../.ai/decisions.md`](../.ai/decisions.md) #4.
- **Favorites / watchlist** — needs a persistence decision first (same blocker as
  offline cache).
- **Pagination** — TVMaze's `/shows` returns its full catalog in one response; a real
  movie API will likely need paging support added to `MovieApiClient`.
- **Automated tests** — see [`../.ai/prompts/generate_tests.md`](../.ai/prompts/generate_tests.md).
- **A real movie catalog** — TVMaze is a TV show API used as dummy data; swapping to an
  actual movie API is still open, see [01-Discovery.md](01-Discovery.md).

## How to add a feature here

Follow [`../.ai/prompts/create_feature.md`](../.ai/prompts/create_feature.md)'s
checklist, then move the item from Backlog to Shipped in this file once it lands.
