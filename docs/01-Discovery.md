# 01 — Discovery

> **Draft.** No user research or market analysis has actually been done — this
> captures what's known/assumed from the current build and flags what needs real
> discovery work.

## Problem space (assumed, not validated)

People want a fast way to browse popular/trending movies and see enough detail
(overview, rating, poster) to decide what to watch — without a slow, ad-heavy
experience. This is the generic "movie discovery app" problem; it hasn't been
validated against a specific underserved need or audience yet.

## Competitive landscape (not researched)

Not evaluated. Existing players in this space (TMDB's own apps, JustWatch, Letterboxd,
IMDb, streaming services' own discovery UIs) would be the obvious starting point for a
real competitive pass — none of this has been done.

## What's actually known

- **Technical constraint**: no real movie API/key was available, so the app currently
  runs on TVMaze (`https://api.tvmaze.com`) — a free, keyless *TV show* API used as
  dummy data, not an actual movie catalog. See
  [`../.ai/decisions.md`](../.ai/decisions.md) #11. Swapping to a real movie API (e.g.
  TMDB, which needs an API key) is a config/mapping change, not an architecture change
  — see [`../.ai/architecture_summary.md`](../.ai/architecture_summary.md)'s
  "Networking" section.
- **Platform**: Android + iOS, native UI on both.

## Next steps for real discovery

1. Define target user and core use case concretely (casual browsing? decision-making
   before watching? tracking watched movies?).
2. Pick and integrate a real movie catalog (TVMaze is TV shows, not movies) — this
   unblocks any meaningful user testing.
3. Look at 2-3 direct competitors for feature/UX baseline before writing the PRD's
   feature list in [03-Feature-List.md](03-Feature-List.md) as anything more than a
   placeholder.
