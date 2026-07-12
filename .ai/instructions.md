# Instructions for AI Assistants

Process to follow when working in this repo, on top of the constraints in
[rules.md](rules.md).

## Before changing dependencies or build files

1. Web-search the current stable version of anything you're adding or bumping — do
   not trust a remembered version number. Check it's actually compatible with the
   Gradle/AGP/Kotlin versions already in `gradle/libs.versions.toml`, not just
   individually "latest."
2. If a choice has a real tradeoff (bleeding-edge vs. battle-tested, two libraries that
   both fit), surface the tradeoff and ask rather than picking silently — see
   [decisions.md](decisions.md) for the Room/Navigation3 precedent.
3. After editing `gradle/libs.versions.toml` or any `build.gradle.kts`, re-check every
   file that references the changed plugin/dependency (root `build.gradle.kts`,
   `shared/build.gradle.kts`, `androidApp/build.gradle.kts` commonly need to change
   together).

## Before changing shared/androidApp source

1. Confirm which layer a change belongs in: `domain` (pure), `data` (Ktor/DTOs/repo
   impl), `di` (Koin wiring), or platform UI. See
   [architecture_summary.md](architecture_summary.md).
2. If the change is a structural refactor (moving a `@Composable` to a new file,
   changing a public function's signature), tell the user to clean-build and fully
   reinstall the app before testing — see rules.md #7.

## Before changing iosApp

1. Any new Swift file needs registering in `iosApp.xcodeproj/project.pbxproj`
   (`PBXFileReference` + `PBXBuildFile` + group entry + Sources build phase entry) —
   there's no Xcode/xcodegen here to do it automatically.
2. Run `plutil -lint iosApp/iosApp.xcodeproj/project.pbxproj` after editing it, before
   calling the change done.
3. Don't assume a Kotlin type crosses cleanly into Swift generics/protocols (e.g.
   `Hashable`, default parameters, function types with a receiver). Check or work
   around it explicitly — see the Navigation3-by-`id`-not-`Movie` decision in
   [decisions.md](decisions.md).

## Scope discipline

- For scaffolding/setup requests, default to the minimal skeleton. If a sample feature
  would help demonstrate the pattern, ask before building it — don't add it silently.
- Don't invent product requirements. The `docs/` files describe MovieVerse's product
  vision as currently drafted — treat them as a starting point to refine with the user,
  not settled fact.

## Reporting back

- State plainly what hasn't been verified (e.g. "I can't build locally, please check
  X"). Don't claim something works when it's only been read for correctness, not run.
