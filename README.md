# Splitzy

**Split Bills. Not Friendships.**

Splitzy is a fast, offline bill-splitting calculator for Android. Enter the bill, pick a tip, set how many people are paying, and it shows what each person owes.

## Features

- Tip presets of 0, 10, 12, 15, 18 and 20%, plus a custom tip from 0 to 100% (decimals allowed)
- People stepper from 1 to 99 (defaults to 2)
- A large per-person result that updates as you type
- **Fair cent allocation.** When a total doesn't divide evenly, the leftover cents are assigned one each to a few people, so the shares add up to the exact total. For example, R100 split 3 ways is R33.33, R33.33 and R33.34.
- 18 currencies, with decimal places from ISO 4217 (JPY has none). The last currency used is remembered.
- Copy the per-person amount, or share a formatted breakdown through the Android share sheet
- Light and dark themes: follows the system by default, with a manual toggle that is remembered
- Adaptive launcher icon with a themed (monochrome) layer, and a branded splash screen
- No account, no ads, no permissions, no network

## Screen sizes and performance

- The layout follows the **app window**, not the device. Below 600dp it uses one column; at 600dp and wider (large phones in landscape, unfolded foldables, tablets) inputs and results sit side by side, capped at 1040dp.
- The tagline gives way on short windows and at large font sizes. Copy and Share stack when side by side would truncate them. Amounts shrink to fit instead of clipping.
- While you type a bill on a phone, a live "Each person pays …" line stays visible above the keyboard.
- Test results, device configurations and measured numbers are in [`docs/LIVE_CHECKLIST.md`](docs/LIVE_CHECKLIST.md).

## Tech stack

| Area | Choice |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM: `ViewModel` + `StateFlow`, inputs in `SavedStateHandle` |
| Persistence | DataStore Preferences (currency, theme) |
| Money | `BigDecimal` throughout, never floating point |
| Tests | JUnit 4, kotlinx-coroutines-test, Turbine |
| Build | AGP 9, Gradle version catalog, compileSdk/targetSdk 37, minSdk 24 |

## Project structure

```
app/src/main/java/app/splitzy/
├── domain/        Pure Kotlin calculation engine (no Android imports)
│   ├── BillCalculator.kt   tip, total, fair split
│   ├── AmountInput.kt      input sanitising and parsing
│   ├── SplitCurrency.kt    currencies and formatting
│   └── Tip.kt              tip presets and custom tip
├── data/          UserPreferences (DataStore)
├── ui/
│   ├── theme/     Brand tokens: colour, type, spacing, radii, motion
│   ├── brand/     Logo mark and wordmark
│   └── calculator/  Screen, ViewModel, UI state, components
└── MainActivity.kt  Splash screen, edge-to-edge, theme selection
```

## Design system

The brand board is the source of truth, and every value is defined once in `ui/theme`:

- **Colours:** Deep Navy `#101B2D`, Splitzy Teal `#20C9A6`, Mint `#A5F3D6`, Cloud White `#F5F7FA`, Indigo `#6366F1`, Success `#10B981`
- **Type:** Plus Jakarta Sans (bundled, SIL Open Font License), on a 32/24/18/16/14/12sp scale
- **Shape:** cards 24dp, buttons and inputs 16dp, fully rounded tip chips
- **Layout:** 8dp spacing grid, touch targets of at least 48dp, animations of 150 to 220ms

## Build and run

Requirements: Android Studio (recent stable) or JDK 17+ with the Android SDK.

```bash
# Debug APK
./gradlew assembleDebug

# Install on a connected device or running emulator
./gradlew installDebug

# Unit tests
./gradlew testDebugUnitTest

# Release-optimised (R8) build, debug-signed, for local performance checks
./gradlew assembleBenchmark
```

In Android Studio, open the project folder, wait for Gradle sync, then run the `app` configuration.

To build a release, configure your own signing key. Keystores and their passwords must never be committed; `.gitignore` already excludes them.

## Branching and commits

- `main` is always stable and releasable
- Work happens on short-lived `feature/*`, `fix/*` and `chore/*` branches, merged with `--no-ff` after the build and tests pass
- Commits follow [Conventional Commits](https://www.conventionalcommits.org/) (`feat`, `fix`, `design`, `test`, `docs`, `chore`, …)
- Releases are tagged using Semantic Versioning (`v1.0.0`)
- GitHub Actions builds and tests every push and pull request
