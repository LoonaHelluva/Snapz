# Snapz

Snapz is an Android application that demonstrates Firebase integration for authentication, database, and storage. The project is built with Kotlin and Gradle.

## Prerequisites

- **Android Studio** with the Android SDK installed
- **Compile SDK version 34** (see `app/build.gradle.kts`)

## Build

To build a debug APK from the command line run:

```bash
./gradlew assembleDebug
```

This will compile the project and create the debug variant in `app/build/outputs/apk/`.

## Firebase Configuration

To enable Firebase services, place your `google-services.json` file in the `app/` directory:

```
app/google-services.json
```

A sample file is already present in the repository. Replace it with your project credentials before building.
