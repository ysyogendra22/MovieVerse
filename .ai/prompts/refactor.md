# Prompt: Refactor

## Checklist

1. **Confirm the refactor is warranted, not speculative.** Three similar lines beat a
   premature abstraction — don't extract a use-case layer, a config flag, or a generic
   helper for something that only has one caller today.
2. **Moving or renaming a `@Composable` or top-level function in `androidApp`?** This
   is exactly the kind of change that left stale dex behind before (decisions.md #10).
   After the change, explicitly tell the user to clean-build + fully reinstall, not
   just re-run.
3. **Changing a shared interface (`MovieRepository`, DTOs)?** Update every
   implementation and every call site in the same change — Android ViewModel, iOS
   Swift call site, and any mock/test data. Grep for the old symbol name before calling
   it done.
4. **Moving Swift files?** Update `project.pbxproj` (path, group membership) and
   re-validate with `plutil -lint`.
5. **Renaming/moving a Gradle module or package?** Check `settings.gradle.kts`,
   `libs.versions.toml` references, and every module's `namespace`/package
   declarations stay consistent.
6. Keep the diff to the refactor itself — don't bundle in unrelated formatting changes
   or opportunistic cleanups; note them separately instead.
