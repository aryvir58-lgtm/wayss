# WAYSs

Original Android social + messaging platform foundation using Kotlin, Jetpack Compose and Firebase.

## Included

- Kotlin + Jetpack Compose
- Material 3
- Navigation Compose
- Firebase Auth / Firestore / Storage / Messaging / App Check dependencies
- CameraX and Media3 dependencies
- WorkManager
- Original Wayss visual design system
- Persistent Light/Dark theme
- Persistent 3D/Normal visual-style switch
- Original icon wrapper/animation system
- Auth repository interfaces and Firebase-backed implementation
- Firestore data models
- Admin/verification role model
- Firestore security rules
- Storage rules
- Cloud Functions TypeScript foundation for secure admin verification
- FCM notification service foundation
- Profile, feed, search, create, messages and settings navigation foundation

## Required configuration

1. Install Android Studio with a recent Android SDK and JDK 17.
2. Create a Firebase project.
3. Add the Android app with package `com.wayss.app`.
4. Download `google-services.json` into `app/`.
5. Enable Authentication providers you want to use.
6. Enable Firestore, Storage, Cloud Messaging and App Check.
7. Deploy `firebase/firestore.rules`, `firebase/storage.rules`, and the functions.
8. Set the protected creator/admin UID through a server-side custom claim. Never put it in the Android client.
9. For WebRTC, configure a real TURN/STUN/RTC provider before enabling calling in production.

## Build

`./gradlew assembleDebug`

or on Windows:

`gradlew.bat assembleDebug`

The APK will be under `app/build/outputs/apk/debug/`.

## Important

This archive is a production-oriented foundation, not a claim that every external service is provisioned automatically. Firebase credentials, App Check, TURN/RTC credentials, OAuth configuration, privacy/legal text, store signing, and production monitoring must be configured by the developer.

There are no fake social actions in this project. Features that require external credentials are represented by explicit service interfaces/configuration points rather than pretending to work.
