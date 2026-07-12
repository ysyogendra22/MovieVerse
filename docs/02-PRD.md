# 02 — Product Requirements Document

> **Draft.** Written against the current implementation, not a validated product spec.
> Update this once [01-Discovery.md](01-Discovery.md)'s open questions are answered.

## Summary

MovieVerse lets a user browse a list of movies and open one to see its detail —
poster, title, overview, genres, rating, release date. Data currently comes from
TVMaze (a free TV show API used as dummy data — see
[`../.ai/decisions.md`](../.ai/decisions.md) #11), not a real movie catalog.

## Requirements — implemented

| # | Requirement | Status |
|---|---|---|
| 1 | Show a list of movies (title, overview snippet, rating, release date, poster thumbnail) | Done |
| 2 | Tapping a movie opens a detail view (full poster, overview, genres, rating, release date) | Done — fetched fresh by id, not reused from the list |
| 3 | Native UI on Android (Compose/Material 3) and iOS (SwiftUI) | Done |
| 4 | Loading state while movies are fetched | Done |
| 5 | Real (if not movie-specific) data source, no manual setup required | Done — TVMaze, keyless |
| 6 | Error state (network unavailable, API error, timeout, empty/malformed response, unexpected exception), with retry | Done — see `MovieError` in `../.ai/glossary.md` |
| 7 | Empty state (successful fetch, zero results) | Done |
| 8 | Unit tests: mapper, repository, ViewModels | Done on Kotlin side (`shared`/`androidApp`) — see `../.ai/architecture_summary.md`'s "Testing" section |

## Requirements — not yet implemented

| # | Requirement | Notes |
|---|---|---|
| 9 | Search / filter movies | Not started |
| 10 | Offline caching of previously loaded movies | Deferred — see [`../.ai/decisions.md`](../.ai/decisions.md) #4 (Room skipped for now) |
| 11 | Favoriting / watchlist | Not started |
| 12 | iOS unit tests (XCTest) | Not started — see [`../.ai/decisions.md`](../.ai/decisions.md) #16 |
| 13 | A real movie catalog (vs. TVMaze's TV shows) | Needs a data source decision — see 01-Discovery.md |

## Non-functional

- App requires internet access (TVMaze is a live network call now — no offline mode).
- No accessibility audit done yet.
- Error messages are English-only, centralized in `shared` — not localized per platform
  (see [`../.ai/decisions.md`](../.ai/decisions.md) #13).

## Explicitly out of scope for now

- Desktop/web targets.
- User accounts / auth.
- Monetization.

These may change once [00-Vision.md](00-Vision.md)'s open questions are resolved.
