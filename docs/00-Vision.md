# 00 — Vision

> **Draft.** This describes the vision as inferred from the current implementation
> (a movie browsing app) and the KMP architecture built so far. Treat it as a starting
> point — refine target audience, monetization, and scope with the user before treating
> any of it as settled.

## What MovieVerse is

A movie discovery app: browse popular movies, view details (poster, overview, rating).
Available natively on Android and iOS from one shared Kotlin codebase, so product
decisions and business logic don't have to be built twice.

## Why KMP

Two native apps, one source of truth for models, networking, and business rules. UI
stays fully native (Jetpack Compose / SwiftUI) on each platform — see
[`../.ai/decisions.md`](../.ai/decisions.md) #1 — so this isn't a shortcut on UI quality,
it's a shortcut on duplicated non-UI logic.

## Where it stands today

The architecture is in place (Clean-ish layering, MVVM, Repository pattern, DI, real
networking, typed error handling) and demonstrated end to end with one feature: a
movie list → detail flow, live data from TVMaze — see
[`../.ai/project_context.md`](../.ai/project_context.md).

## Open questions (need product input, not engineering ones)

- Target audience and primary use case beyond "browse movies"?
- Real *movie* data source — TVMaze (currently wired in) is TV shows, not movies;
  TMDB, a proprietary catalog, or something else needs deciding.
- Monetization, if any?
- Platforms beyond Android/iOS (this codebase's `shared` module could target
  desktop/web too, but nothing beyond mobile is scoped)?
