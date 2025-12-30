## Android Music Player – Intern Assignment

A modern Android Music Player app built using Jetpack Compose and MVVM architecture as part of an Android Developer Intern screening assignment.

---

## Features

- **Song List**
    - Displays tracks with **Title**, **Artist**, and **Duration**
    - Data fetched from a public music API

- **Playback Control**
    - Play / Pause audio streaming
    - Seek bar to scrub through audio
    - Handles playback completion safely
    - Uses `MediaPlayer.prepareAsync()` to avoid UI blocking

- **Sorting**
    - Sort by **Name (A–Z)**
    - Sort by **Duration (Shortest → Longest)**

- **UI**
    - Built entirely with **Jetpack Compose**
    - Material 3 components
    - Clean and responsive layout

---

## Tech Stack

- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose (Material 3)
- **Architecture**: MVVM
- **State Management**: StateFlow
- **Concurrency**: Kotlin Coroutines
- **Networking**: Jamendo Public API
- **Media Playback**: Android MediaPlayer

---

## API Used

**Jamendo Public API**

Jamendo was chosen because it provides free access to real music metadata and audio streams, making it suitable for demo and educational applications.

---

## Setup & Run

1. Open the project in Android Studio
2. Sync Gradle dependencies
3. Run on an emulator or physical device
4. Ensure an active internet connection for streaming audio

---

## Assumptions & Simplifications

- **Android-only Implementation**  
  Although Kotlin Multiplatform is mentioned in the assignment, an Android-only approach was chosen to focus on core Android fundamentals.

- **MediaPlayer Scope**  
  MediaPlayer is managed inside the ViewModel for simplicity.  
  In a production application, playback would be handled via a Foreground Service or Media3 (ExoPlayer) for background playback support.

- **Error Handling**  
  Basic error handling is implemented to prevent crashes and show user-friendly messages.

---

## Project Structure

- `MusicViewModel` – Handles playback logic, sorting, and UI state
- `MusicScreen` – Main UI built with Jetpack Compose
- `ui/theme` – Compose theme configuration
