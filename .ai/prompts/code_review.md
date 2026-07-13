# Prompt: Code Review

Review against this project's actual conventions, not generic best practices.

## Checklist

- **Layering**: does `domain/` stay free of platform/framework imports (no `android.*`,
  no Ktor, no Koin annotations)? Does everything network-related route through
  `data/remote` rather than being called ad hoc from a ViewModel or UI layer?
- **DI**: are new dependencies registered in a Koin module rather than manually
  constructed inline? Do ViewModels take dependencies as required constructor params
  with no default fallback (see coding_style.md — we deliberately removed a
  `FakeMovieRepository` default once)?
- **Versions**: does any new/bumped dependency in `libs.versions.toml` have a version
  that was actually checked against current stable releases, not assumed? Flag if not.
- **Compose**: is navigation only happening in the top-level nav host, not inside
  individual screens? Are stateless composables actually stateless (data + lambdas in,
  no ViewModel reference)?
- **Swift**: are Kotlin types being used in ways that assume Swift protocol conformance
  (`Hashable`, `Equatable`) they aren't guaranteed to have? Are `suspend fun` completion
  handlers guarding `[weak self]`?
- **Xcode project file**: if a new `.swift` file was added, was `project.pbxproj`
  updated and validated with `plutil -lint`?
- **Scope**: does the change do only what was asked? Flag unrequested sample code,
  extra abstractions, or speculative config (rules.md #9).
- **Comments**: flag comments that restate *what* the code does; keep only ones
  explaining *why* something non-obvious is the way it is.

Report findings the way the repo's own `/code-review` skill does — most severe first,
with file:line references — rather than a prose summary.
