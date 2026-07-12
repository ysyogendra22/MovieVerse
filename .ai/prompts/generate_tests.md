# Prompt: Generate Tests

There are no automated tests in this repo yet — this is the starting point.

## Where tests go

- **`shared/src/commonTest`** — tests here run against both the Android (JVM) and iOS
  targets. This is where almost everything should live: domain logic, mappers,
  repository implementations.
- Platform-specific test source sets (`androidUnitTest`, `iosTest`) only for code that's
  actually platform-specific (the `Platform.android.kt` / `Platform.ios.kt`
  `actual` implementations).

## What to prioritize

1. **`MovieMapper.toDomain()`** — pure function, no dependencies, cheapest possible
   test, currently uncovered.
2. **`MovieRepositoryImpl.safeApiCall`** — the exception→`MovieError` translation is
   exactly the kind of logic worth locking down with a test per catch branch (timeout,
   4xx/5xx, malformed response, generic failure). Use `ktor-client-mock`'s `MockEngine`
   as a *test-only* dependency (it's not in production code anymore — see
   `.ai/decisions.md` #11) to simulate each failure mode without a real network call.
3. **DTO serialization** — round-trip a `ShowDto` through `kotlinx.serialization.json.Json`
   to catch `@SerialName` mismatches early (this is the kind of bug that's easy to
   introduce silently when the API shape changes).
4. Compose/SwiftUI UI tests are lower priority until there's more than one screen's
   worth of interaction logic to justify them.

## Conventions

- Use `kotlin.test` (already a `commonTest` dependency) rather than introducing a new
  assertion library without checking it's needed.
- Name test files `XTest.kt` mirroring the file under test (`MovieMapperTest.kt` for
  `MovieMapper.kt`).
- Don't mock what you don't own if you can avoid it — prefer the real
  `kotlinx.serialization` `Json` instance over mocking serialization behavior.
