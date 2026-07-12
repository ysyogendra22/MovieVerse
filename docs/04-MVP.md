# 04 — MVP

> **Draft.** Defines the smallest version worth putting in front of real users. Revisit
> once [00-Vision.md](00-Vision.md)'s open questions (audience, data source) are answered.

## MVP scope

The technical shape of the MVP is now in place: real data (list → detail, TVMaze),
loading/success/error/empty states with retry, native UI on both platforms. What's
left is product-level, not architectural:

1. **A real movie catalog** — TVMaze is a free TV show API used as dummy data (see
   [`../.ai/decisions.md`](../.ai/decisions.md) #11); it's real and reliable, but it's
   not actually movies. Swapping the data source doesn't require an architecture
   change (`MovieApiClient`'s `baseUrl` + `ShowDto`/`MovieMapper.kt` are the only
   things that need to change), but it does require a product decision — see
   [01-Discovery.md](01-Discovery.md).
2. **Verify iOS end to end** — Android has been build- and run-verified; iOS has not
   been confirmed in this environment (no local Xcode toolchain — see
   [`../.ai/project_context.md`](../.ai/project_context.md)).

## Explicitly NOT required for MVP

- Search, favorites, offline cache, pagination — all real features, all deferred (see
  [03-Feature-List.md](03-Feature-List.md)'s backlog).
- Full test coverage isn't a blocker for a first user-facing MVP — Kotlin-side unit
  tests exist (mapper, repository, ViewModels; see
  [`../.ai/architecture_summary.md`](../.ai/architecture_summary.md)'s "Testing"
  section), but iOS/XCTest coverage is still open and shouldn't gate shipping.
- Localized error messages — currently English-only, centralized in `shared` (see
  [`../.ai/decisions.md`](../.ai/decisions.md) #13). Fine for an initial release.

## Definition of done for MVP

- Real movie data loads on both Android and iOS. **(Done for TVMaze; pending a real
  movie-catalog decision.)**
- A failed request shows a real error state with retry, not a permanent spinner. **Done.**
- Both apps have been build- and run-verified end to end. **Android done; iOS pending.**
