// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android
    id("com.android.application") version "8.5.2" apply false
    id("com.android.library") version "8.5.2" apply false
    
    // Kotlin
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("org.jetbrains.kotlin.multiplatform") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0" apply false
    
    // Compose Multiplatform
    id("org.jetbrains.compose") version "1.6.11" apply false
}
