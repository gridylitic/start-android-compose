# start-android-compose

The starting point for the ingrid.fyi roadmap track that begins from a
running native Android app. This is the *before* state your track assumes: a
Jetpack Compose app — three screens, navigation, live data from one public
API on a background dispatcher — and nothing more. The track teaches
everything else.

**Everything here runs locally. No accounts, no sign-ups, no cloud.** It
reads from [PokeAPI](https://pokeapi.co), a free, public, key-less REST API.
The only thing you install is Android Studio and its SDK.

**What's inside:** three composable screens (`Home` → `List` → `Detail`)
wired with Navigation Compose, a scrollable `LazyColumn` of all 151 original
Pokémon fetched on `Dispatchers.IO`, and loading and error modelled
explicitly as UI state (a `sealed interface UiState` the view models emit).

## Run it

1. **Android Studio** (latest stable) — free from
   [developer.android.com/studio](https://developer.android.com/studio). It
   brings the Android SDK and an emulator with it.

Open this folder in Android Studio, let it sync Gradle once, pick an
emulator (or a device with USB debugging), and press **Run**. From the
command line the same build is:

```bash
./gradlew assembleDebug          # Windows: gradlew.bat assembleDebug
```

which writes the debug APK to `app/build/outputs/apk/debug/`. No
configuration and no key.

**Kotlin is assumed throughout** — the track's checkpoints are written in
it.

## You're ready when

- The app runs on an emulator or device from Android Studio.
- The **List** screen scrolls with real data fetched from the API.
- Tapping a row **navigates** to the Detail screen and Back returns — that
  navigation is what your track builds on.
- With the emulator offline, the List shows a readable error instead of a
  blank screen. That error state is doing its job.
- `./gradlew assembleDebug` produces `app-debug.apk`.

That's the whole starting point. Open your track and take the first
checkpoint.

## Deliberately not included

So that your checkpoints have their work to do, this starter ships **no
authentication, no automated tests, no data-quality or request validation,
and no release signing or deployment setup of any kind** — one API, three
screens, loading and error. Your first checkpoint changes that.

---

Built for [ingrid.fyi](https://ingrid.fyi). MIT licensed — fork it, break
it, rebuild it; that is what it is for.
