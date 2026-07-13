# Prompt: Fix a Bug

## Checklist

1. **Reproduce first, in the right layer.** Is it a data problem (wrong DTO mapping,
   an actual TVMaze response you didn't expect), a DI problem (missing Koin
   registration → runtime crash on `get()`), a UI problem (Compose/SwiftUI state), or a
   build/toolchain problem (version mismatch, stale build)? Don't guess — read the
   actual stack trace/error.
2. **Runtime crash with `NoSuchMethodError` / `NoSuchFieldError` on Android?** Suspect
   stale dex before suspecting the source. Check the source is actually consistent
   (read the relevant files), then have the user clean-build + fully reinstall
   (rules.md #7) before looking further.
3. **Gradle sync/build failure mentioning a plugin, DSL, or deprecation?** Check
   whether it's a genuine version-compatibility issue (see decisions.md #2 for the
   AGP9/`shared` precedent) before patching around it — a one-line suppress can mask a
   real incompatibility.
4. **Wrong data displayed?** Data is live from TVMaze now, not mocked — check what the
   API actually returned (`GET https://api.tvmaze.com/shows` /
   `GET https://api.tvmaze.com/shows/{id}` in a browser or curl) before assuming a
   mapping bug in `MovieMapper.kt`.
5. **Wrong or missing error state?** Check `MovieRepositoryImpl.safeApiCall`'s catch
   order first — a new exception type Ktor throws that isn't handled there falls
   through to `MovieError.Unknown`, which may be masking a more specific case.
6. **iOS-only bug involving a Kotlin type?** Check whether it's a Swift interop
   limitation (default params, `Hashable`, function types with receivers all behave
   differently than in Kotlin — see decisions.md #7 and coding_style.md) rather than a
   logic bug.
7. Fix the root cause, not the symptom — don't add a try/catch or a fallback value to
   paper over an error whose cause you haven't identified.
8. State plainly what you verified vs. what you couldn't (no local JDK/Xcode in this
   environment — see instructions.md).
