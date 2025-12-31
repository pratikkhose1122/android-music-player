# Kotlin Multiplatform Music Player

This project has been migrated from a single-module Android application to a **Kotlin Multiplatform (KMP)** project with **Compose Multiplatform** UI.

## Structure

The project is divided into two modules:

1.  **`:shared`** (KMP Module)
    *   **`commonMain`**: Contains all business logic, UI, and data layers.
        *   **UI**: `MusicScreen.kt` built with Compose Multiplatform.
        *   **ViewModel**: `MusicViewModel` managing state and logic.
        *   **Data**: `JamendoResponse`, `TrackDto` models.
        *   **Networking**: Ktor logic.
        *   **Audio Interface**: `AudioPlayer` interface.
    *   **`androidMain`**:
        *   **Audio Implementation**: `AndroidAudioPlayer` implementing `AudioPlayer` using Android's native `MediaPlayer`.
    *   **`iosMain`**:
        *   Contains the `ios*` target configuration and stubs for `AudioPlayer`.

2.  **`:app`** (Android App)
    *   A thin wrapper primarily consisting of `MainActivity`.
    *   It simply calls the shared `App()` composable entry point.

## Key KMP Features

*   **Compose Multiplatform**: The UI is written once in `commonMain` and runs on Android. It is ready for iOS adoption.
*   **Expect/Actual Mechanism**: Used for the `AudioPlayer` to handle platform-specific audio playback (using `MediaPlayer` on Android).
*   **Shared ViewModel**: Logic is decoupled from the Android framework, residing in common code.

## Tech Stack

*   **Language**: Kotlin 2.0
*   **UI**: Compose Multiplatform
*   **Networking**: Ktor
*   **Architecture**: MVVM (Shared)

## API Used

**Jamendo Public API**
Used to fetch real music metadata and audio streams.

## Running the App

1.  Open the project in Android Studio.
2.  Sync Gradle with the new KMP configuration.
3.  Select the **`app`** configuration and click Run.
4.  The Android app behaves exactly as before but runs entirely from the shared codebase.

## APK

A fresh debug APK is available for testing:
*   `apk/music-player-debug.apk` 
