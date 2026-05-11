# Finance Manager Android Application

**Assignment #04 — Software for Mobile and Devices**
**Institution:** National University of Computing and Emerging Sciences, CFD Campus
**Group Members:** 23F-3017, 23F-3060, 23F-3092
**Submission Date:** May 10, 2026

---

## Overview

This repository contains the complete source code for the Finance Manager Android Application developed as part of Assignment #04 for the Software for Mobile and Devices course. Building upon the foundation established in Assignments #01, #02, and #03, this iteration introduces industry-standard cloud technologies, modern UI paradigms, and proactive user engagement mechanisms.

The application, named **Penni**, is a personal finance management tool that allows users to track income and expenses, visualize spending patterns, and manage budgets effectively. In this assignment, the application has been significantly enhanced with Firebase Cloud Services, Firebase Cloud Messaging push notifications, Jetpack Compose UI components, and two independently researched advanced features.

---

## Architecture

The application follows the **MVVM (Model-View-ViewModel)** architectural pattern, ensuring clean separation of concerns and testability.

- **UI Layer:** Existing Fragment-based screens are retained for all core content views. Jetpack Compose has been introduced for new screens including the Login screen and the User Profile screen.
- **Data Layer:** Room Database (integrated in Assignment #03) continues to handle local persistence. Firebase Firestore has been added as the cloud data layer, enabling real-time synchronization across devices.
- **Authentication Layer:** Firebase Authentication handles both Email/Password and Google Sign-In flows. Sessions persist across app restarts via Firebase's built-in session management.
- **Notification Layer:** Firebase Cloud Messaging (FCM) handles push notifications. A custom `MyFirebaseMessagingService` receives and displays notifications using `NotificationCompat`.
- **Observability Layer:** Firebase Crashlytics monitors crash reports in real time. Firebase Analytics tracks user interactions and session behavior.
- **Remote Control:** Firebase Remote Config allows server-side control of app parameters without requiring an APK update.
- **Async Operations:** All Firebase operations and background tasks are performed asynchronously using Kotlin Coroutines and `ViewModelScope` to prevent UI thread blocking.

---

## Features

### Assignment #04 Core Features

**F1 — Firebase Authentication**

Firebase Authentication is integrated with two sign-in methods:
- Email and Password Authentication
- Google Sign-In via OAuth 2.0

The `AuthRepository.kt` class encapsulates all authentication logic. The `signInWithGoogle()` method handles the Google OAuth token exchange with Firebase Auth. The `signInWithEmailPassword()` method manages email/password authentication. User sessions persist across app restarts, and appropriate UI flows are provided for login, registration, and logout.

**F2 — Firebase Firestore Real-Time Database**

The data layer has been extended to use Firebase Firestore alongside the existing Room Database. The `FirestoreHelper.kt` class manages all Firestore interactions. The `syncUserData()` method attaches a real-time snapshot listener to the `users` collection, ensuring the UI is refreshed immediately when data changes on any connected device. A nested `transactions` sub-collection maintains a logical parent-child relationship with the `users` collection. The `addTransaction()` method writes transaction records with server-side timestamps.

**F3 — Firebase Cloud Messaging (Push Notifications)**

Push notifications are implemented via `MyFirebaseMessagingService.kt`, which extends `FirebaseMessagingService`. The `onMessageReceived()` method processes incoming FCM messages and displays them as system notifications using `NotificationCompat.Builder`. The `onNewToken()` callback captures refreshed FCM tokens and saves them to Firestore, ensuring the device is always reachable for targeted notifications. The `NotificationHelper.kt` utility class handles notification channel creation for Android 8.0 and above.

**F4 — Jetpack Compose UI**

Jetpack Compose has been integrated for new screens within the application. The Login screen (`LoginActivity.kt`) is fully built using Compose composables, providing a modern declarative UI. The User Profile screen (`UserProfileActivity.kt`) is also a Compose-based screen that displays user data fetched live from Firestore. View binding continues to be used for all existing Fragment-based screens.

---

### Self-Researched Features

**NF1 — Firebase Remote Config**

Firebase Remote Config was independently researched and integrated to allow server-side control of application behavior without distributing a new APK. The `RemoteConfigHelper.kt` class calls `fetchAndActivate()` at application startup to pull the latest parameter values from the Firebase console. This feature enables dynamic content updates, feature flags, and UI configuration changes that propagate to all users instantly.

Reference: https://firebase.google.com/docs/remote-config/get-started?platform=android

**NF2 — Firebase Crashlytics and Analytics**

Firebase Crashlytics and Firebase Analytics were integrated to bring industry-standard observability to the application. Crashlytics automatically captures and reports non-fatal and fatal crash reports with full stack traces. Analytics tracks custom user events and session data, providing insight into how users interact with the finance manager. Both services are initialized inside `MyApp.kt` in the `onCreate()` method of the custom Application class.

Reference: https://firebase.google.com/docs/crashlytics/get-started?platform=android

---

## Project Structure

```
app/
└── src/
    └── main/
        ├── AndroidManifest.xml
        ├── java/com/smd/penni/
        │   ├── MyApp.kt                         Application class (Crashlytics + Analytics init)
        │   ├── NavExtras.kt                     Navigation constants
        │   ├── activities/
        │   │   ├── LoginActivity.kt             Compose-based Login UI + Firebase Auth
        │   │   ├── RegisterActivity.kt          User registration with Firebase Auth
        │   │   ├── MainActivity.kt              Host activity for fragments
        │   │   ├── SplashActivity.kt            Session check + app entry point
        │   │   └── UserProfileActivity.kt       Compose-based User Profile screen
        │   ├── data/
        │   │   ├── AuthRepository.kt            Firebase Auth logic (email + Google)
        │   │   ├── AuthViewModel.kt             ViewModel for auth state management
        │   │   ├── DatabaseHelper.kt            Room Database helper (local persistence)
        │   │   ├── FirestoreHelper.kt           Firestore CRUD + real-time sync
        │   │   └── RemoteConfigHelper.kt        Firebase Remote Config integration
        │   ├── fragments/
        │   │   ├── HomeFragment.kt              Dashboard with transaction summary
        │   │   ├── DataFragment.kt              Transaction data and filtering
        │   │   ├── StatsFragment.kt             Spending statistics and charts
        │   │   ├── ProfileFragment.kt           User profile display
        │   │   ├── MainNavFragment.kt           Bottom navigation host
        │   │   └── TransactionDetailFragment.kt Transaction detail view
        │   ├── adapters/
        │   │   ├── TransactionAdapter.kt        RecyclerView adapter for transactions
        │   │   ├── BudgetAdapter.kt             RecyclerView adapter for budgets
        │   │   ├── CategoryAdapter.kt           RecyclerView adapter for categories
        │   │   └── MarketAdapter.kt             RecyclerView adapter for market data
        │   ├── models/
        │   │   ├── Transaction.kt               Transaction data model
        │   │   ├── BudgetItem.kt                Budget data model
        │   │   └── CategoryItem.kt              Category data model
        │   └── network/
        │       ├── ApiService.kt                Retrofit API service interface
        │       ├── ExchangeRateResponse.kt      Exchange rate API response model
        │       ├── MarketCoin.kt                Market coin data model
        │       ├── MyFirebaseMessagingService.kt FCM service for push notifications
        │       └── NotificationHelper.kt        Notification channel and builder utility
        └── res/
            ├── layout/                          XML layouts for activities and fragments
            ├── drawable/                        Vector drawables and background shapes
            └── values/                          Colors, strings, and themes
```

---

## Firebase Services Used

| Service | Purpose |
|---|---|
| Firebase Authentication | Email/Password and Google Sign-In |
| Firebase Firestore | Cloud database with real-time synchronization |
| Firebase Cloud Messaging | Push notification delivery |
| Firebase Remote Config | Server-side parameter and feature control |
| Firebase Crashlytics | Real-time crash monitoring and reporting |
| Firebase Analytics | User behavior and session tracking |

---

## Technology Stack

| Component | Technology |
|---|---|
| Language | Kotlin |
| Minimum SDK | API 24 (Android 7.0) |
| Target SDK | API 34 (Android 14) |
| Architecture | MVVM |
| UI Frameworks | Fragments (existing) + Jetpack Compose (new) |
| Local Database | Room |
| Cloud Database | Firebase Firestore |
| Networking | Retrofit 2 |
| Async | Kotlin Coroutines + ViewModelScope |
| Build System | Gradle (Kotlin DSL) |

---

## GitHub Collaboration

This project follows the branching strategy mandated by the assignment:

- **main** — Stable production branch
- **feature/firebase-auth** — Firebase Authentication implementation
- **feature/firestore-db** — Firestore real-time database integration
- **feature/fcm-notifications** — Push notification implementation
- **feature/jetpack-compose** — Jetpack Compose UI screens
- **feature/remote-config** — Firebase Remote Config (self-researched feature)
- **feature/crashlytics-analytics** — Firebase Crashlytics and Analytics (self-researched feature)

Each feature branch is merged into `main` via a Pull Request with at least one peer review comment, satisfying the GitHub Collaboration requirements of the assignment.

---

## Grading Reference

| Requirement | Implementation |
|---|---|
| Firebase Authentication | `AuthRepository.kt`, `LoginActivity.kt`, `RegisterActivity.kt` |
| Firebase Firestore | `FirestoreHelper.kt`, `DataFragment.kt`, `HomeFragment.kt` |
| Push Notifications (FCM) | `MyFirebaseMessagingService.kt`, `NotificationHelper.kt` |
| Jetpack Compose UI | `LoginActivity.kt`, `UserProfileActivity.kt` |
| Self-Researched Feature 1 | `RemoteConfigHelper.kt` (Firebase Remote Config) |
| Self-Researched Feature 2 | `MyApp.kt` (Firebase Crashlytics + Analytics) |
| Logic Map | `logic_map.pdf` |

---

## Setup and Installation

1. Clone the repository.
2. Open the project in Android Studio (Hedgehog or later recommended).
3. Ensure the `google-services.json` file is present in the `app/` directory. This file is project-specific and links the app to the Firebase project.
4. Sync Gradle dependencies.
5. Build and run the application on a physical device or emulator running Android 7.0 (API 24) or higher.

---

## Submission Documents

- `logic_map.pdf` — Requirement-to-code mapping with research references
- `BRANCHING_STRATEGY.md` — Git branching and collaboration strategy

---

## License

This project was developed for academic purposes at the National University of Computing and Emerging Sciences, CFD Campus. All rights reserved by the respective group members.
