
# Calculator (Android, Kotlin) – advanced calculator app with expression evaluation, Firebase-based history, user authentication (email + biometrics), QR code creation and scanning, dark/light themes, a simple built-in mini-game, and additional utilities.

---

## Features

* Mathematical expression evaluation (powered by `exp4j`).
* Calculation history synchronized with Firebase Firestore (requires configuration).
* User authentication (including biometric login).
* QR code creation and scanning.
* Light/Dark theme switching.
* Screen brightness control and various UI settings.
* **Mini-game "CircleView"**:
  When launched, a ball appears on the screen and moves according to the device's tilt. Pressing the device's volume buttons simulates a "click" at the ball's current position, allowing interaction with on-screen elements in the game.

---

## Project Structure (Key Files)

* `app/src/main/java/com/example/calculator/`

  * `MainActivity.kt` — main UI logic and navigation.
  * `StringCalculating.kt` — expression parsing and evaluation.
  * `FirebaseHelper.kt` — Firebase interactions (Auth, Firestore, FCM).
  * `LogInActivity.kt` — login/registration screen and biometrics.
  * `CreateQRCode.kt`, `ScanQRActivity.kt` — QR code features.
  * `CalculatorActionListener.kt` — calculator button handling.
  * `CircleView.kt` — mini-game rendering and interaction.
* `app/src/main/res/` — resources, layouts, and strings.

---

## Quick Setup & Run

1. Unpack the project and open it in Android Studio (`File > Open` → select `Calculator` folder).
2. Connect a device or emulator with API level >= `minSdk` (specified in `app/build.gradle.kts`).
3. Configure Firebase:

   * Create a project in Firebase Console.
   * In the Android section, add an app (use the package name `com.example.calculator` or adjust it in the project).
   * Download `google-services.json` and place it in the `app/` folder (`app/google-services.json` path).
   * Enable Firebase Authentication (Email/Password) and Firestore.
   * *(Optional)* Enable Firebase Cloud Messaging if push notifications are required.
4. Build and run the app.

