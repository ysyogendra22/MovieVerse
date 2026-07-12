# Rules

Hard constraints for anyone (human or AI) making changes in this repo. Most of these
exist because we broke something this way already — see [decisions.md](decisions.md)
for the incident behind each one.

1. **Verify library versions before adding or bumping a dependency.** Don't rely on
   training-data knowledge of "latest stable" — this stack moves fast (Gradle 9,
   AGP 9, Kotlin 2.4 all shipped within the last few months) and guessing wrong causes
   real runtime breakage (we hit a `NoSuchMethodError` from a stale `activity-compose`
   paired with a much newer Compose BOM). Look it up, then update the version catalog.

2. **Never commit `local.properties`.** It holds a machine-specific Android SDK path.
   It's in `.gitignore` — keep it there.

3. **Keep UI fully native per platform.** Compose on Android, SwiftUI on iOS. Don't
   introduce Compose Multiplatform UI sharing without treating it as a deliberate
   architecture change (it invalidates the Navigation3/Voyager and Coil/AsyncImage
   choices — see [decisions.md](decisions.md)).

4. **`shared/domain` has zero platform or framework dependencies.** No Android, no
   Ktor, no Koin annotations in `domain/model` or `domain/repository` — those belong in
   `data/` and `di/`.

5. **All network calls go through `shared/data/remote`.** Don't call HTTP directly
   from a ViewModel or UI layer; go through the repository interface.

6. **iOS DI goes through `Injector`, not Koin's Compose APIs.** Swift can't use
   `koinViewModel()`. Use `Injector.shared.xxx()` (`shared/di/Injector.kt`) and add new
   accessors there as needed.

7. **After a structural refactor to `androidApp` (moved/renamed composables, changed
   function signatures), do a clean build + full reinstall before testing**, not an
   incremental "Run". We hit a `NoSuchMethodError` from stale dex referencing an old
   function signature after exactly this kind of refactor — Compose's incremental
   compiler doesn't reliably invalidate across it.

8. **Validate `iosApp.xcodeproj/project.pbxproj` after editing it**, with
   `plutil -lint iosApp/iosApp.xcodeproj/project.pbxproj`. It's hand-maintained (no
   Xcode/xcodegen in this environment), so a typo there silently corrupts the project.

9. **Don't add sample/demo features beyond what's asked for scaffolding requests.**
   If a demonstrative example seems valuable, propose it and ask before building it.
