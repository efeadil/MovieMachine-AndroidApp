# 🎬 Movie Machine - Professional Movie Discovery Platform

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/Architecture-MVC-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/API-TMDB-01b4e4?style=for-the-badge" />
</p>

---

## 🌟 Project Overview
**Movie Machine** is a high-performance Android application designed to provide a seamless movie and TV show discovery experience. Refactored for a professional academic defense, the project emphasizes clean architecture (MVC), robust error handling, and cross-media metadata mapping.

---

## 🔥 Key Features

- 🔍 **Unified Search Engine**: Search for both Movies and TV Shows simultaneously with intelligent metadata handling (Title vs. Name mapping).
- 🎞️ **Direct Trailer Access**: Instantly view movie trailers and detailed IMDb/TMDB information via optimized external Intent links.
- 🧬 **Smart Onboarding**: Personalized discovery experience based on user-selected genres, passed efficiently between activities via Android Intents.
- 📱 **Modern UI/UX**: 
  - Dynamic **Horizontal & Vertical RecyclerViews** for cast and movie listings.
  - High-quality image processing and caching using the **Glide** library.
  - Clean, distraction-free interface optimized for content discovery.
- 🌍 **Bilingual Documentation**: All source code and test files include comprehensive **English/Turkish (EN/TR)** documentation to facilitate technical defense.
- 🛠 **Safe & Secure**: API keys are managed securely via `BuildConfig` and `local.properties`.

---

## 🏗 Engineering Excellence (Architecture & Principles)

This project strictly adheres to modern software engineering standards:

- **Strict MVC Pattern**: 
  - **Models**: Clean data structures (`Movie`, `CastMember`).
  - **Views**: Specialized activities for list, search, and details.
  - **Controller**: Centralized `MovieController` handling all API logic and JSON parsing.
- **English-First Standards**: All functional strings, Toast messages, and error logs are in English for global standards.
- **Memory Efficiency**: Removed persistent storage (SharedPreferences) in favor of Intent-based data transfer to minimize disk I/O.
- **Asynchronous Processing**: All network operations are handled off the Main Thread to ensure a smooth, 60 FPS user experience.

---

## 🧪 Testing & Quality Assurance

The project contains three different test levels:
- **Unit Tests (JUnit 4 & Mockito)**: Executes the real model, parser, and network-availability logic locally without contacting TMDB.
- **Integration Test (AndroidJUnit4)**: `MovieApiIntegrationTest` executes the real `MovieController.fetchMovies()` request against TMDB on a connected device or emulator. It does not use a hard-coded sample response.
- **UI Tests (Espresso)**: Automates input validation and navigation flows on a connected device or emulator. Network/API behavior is tested separately by the integration test, so UI tests do not rely on fixed sleeps or always-visible views.

Run the local unit tests from the project directory:

```bash
./gradlew test
```

Build the instrumentation tests:

```bash
./gradlew assembleDebugAndroidTest
```

To run the real TMDB integration test, configure `tmdb_api_key` in `local.properties`, start an emulator or connect a device with USB debugging enabled, and run:

```bash
./gradlew connectedDebugAndroidTest
```

The integration test intentionally fails when the API key is missing, the network request times out, TMDB rejects the key, or the response contains no valid movie data. This prevents a fake test from passing silently.

---

## 🛠 Tech Stack

| Category | Technology |
| :--- | :--- |
| **Language** | Java (Android SDK) |
| **Architecture** | MVC (Model-View-Controller) |
| **Networking** | HttpURLConnection / ExecutorService |
| **JSON Parsing** | org.json (Standard Library) |
| **Image Loading** | Glide |
| **Testing** | JUnit 4, Mockito, Espresso |

---

## ⚙️ Installation & Setup

 1. **Clone the repository**:
   ```bash
   git clone https://github.com/efeadil/MovieMachine-AndroidApp.git
   ```

  2. **Add your API Key**:
   Open the `local.properties` file in the root directory and add your TMDB API Key:
   ```properties
   TMDB_API_KEY=your_api_key_here
   ```

  3. **Build & Run**: Open the project in **Android Studio**, sync Gradle, and run it on an emulator or physical device.

 ---

### 👨‍💻 Author & Project Details
 - **Name:** Adil Efe Begen
 - **Project:** Movie Machine (Android Development Course)
 - **Architecture:** MVC / Clean Code

[LinkedIn Profile](https://www.linkedin.com/in/adilefebegen) | [GitHub Profile](https://github.com/efeadil)

---

<p align="center">
  <i>This project fulfills the requirements for the professional Android Development curriculum.</i>
</p>
