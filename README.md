# 🎬 Movie Machine - The Ultimate IMDb Streaming Experience

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/Architecture-MVC-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/API-TMDB-01b4e4?style=for-the-badge" />
</p>

---

## 🌟 The Core Innovation: "Streaming Without Boundaries"
Unlike standard movie browsers, **Movie Machine** bridges the gap between discovery and consumption. The standout feature of this project is the **In-App Direct Streaming Engine**. 

By intelligently mapping TMDB metadata to external IMDb identifiers, the app allows users to **watch any movie or TV show directly within the application—completely free and without any subscription or membership requirements.** No external browsers, no redirects, just pure entertainment.

---

## 🔥 Key Features

- 🚀 **One-Click Play**: Watch IMDb content directly via an integrated, full-screen, high-performance WebView player.
- 🔍 **Multi-Search Engine**: Search for both Movies and TV Shows simultaneously with intelligent metadata handling (Title vs. Name mapping).
- 🧬 **Smart Onboarding**: Personalized "Recommended for You" section based on user-selected genres (Action, Comedy, Sci-Fi, etc.) stored securely via `SharedPreferences`.
- 📱 **Modern UI/UX**: 
  - Dynamic **Horizontal & Vertical RecyclerViews**.
  - Professional **Navigation Drawer** (Slide-out menu).
  - High-quality image caching using the **Glide** library.
- 🛠 **Safe & Secure**: API keys are managed securely via `BuildConfig` to prevent exposure in public repositories (No hardcoding).

---

## 🏗 Engineering Excellence (Architecture & Principles)

This project was built with long-term maintainability and scalability in mind, strictly adhering to modern software engineering standards:

- **MVC Pattern**: Strict separation of concerns between **Models** (Data), **Views** (UI), and **Controllers** (API Logic).
- **SOLID Principles**: Focused on Single Responsibility and Interface Segregation to ensure clean, readable, and modular code.
- **Asynchronous Processing**: Heavy network operations are handled off the Main Thread using `ExecutorService` to ensure zero UI freezing.

---

## 🧪 Testing & Quality Assurance

Quality is not an afterthought. The project includes:
- **Unit Tests (JUnit)**: Validating core data processing logic and media type mapping rules.
- **UI Tests (Espresso)**: Automated verification of user interactions, search flows, and seamless navigation.

---

## 🛠 Tech Stack

| Category | Technology |
| :--- | :--- |
| **Language** | Java (Android SDK) |
| **Networking** | HttpURLConnection (Async) |
| **JSON Parsing** | org.json |
| **Image Loading** | Glide |
| **Layouts** | RelativeLayout, GridLayout, DrawerLayout |
| **Storage** | SharedPreferences |
| **Version Control** | Git / GitHub |

---

## 📸 Screenshots

<p align="center">
  <img src="https://via.placeholder.com/200x400?text=Onboarding" width="200" title="Onboarding Preferences" />
  <img src="https://via.placeholder.com/200x400?text=Home+Screen" width="200" title="Home Screen" />
  <img src="https://via.placeholder.com/200x400?text=Search+Results" width="200" title="Search Engine" />
  <img src="https://via.placeholder.com/200x400?text=Video+Player" width="200" title="In-App Player" />
</p>

---

 ## ⚙️ Installation & Setup

 1. **Clone the repository**:
   ```bash
   git clone https://github.com/adilefebegen/MovieMachine-Android-App.git
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
  - **Student ID:** 35480
- **Group:** AI 01
- **Semester:** IV
- **Faculty:** Computer Engineering

[LinkedIn Profile](https://www.linkedin.com/in/adilefebegen) | [GitHub Profile](https://github.com/adilefebegen)

---

## 📝 Important Notes for Project Defense
As per the course requirements:
- **Project Defense**: I am prepared to explain the MVC structure, SOLID principles, and the logic behind the API communication during the defense session.
- **Code Integrity**: All code has been written following clean coding practices and modularization to ensure readability and maintainability.

---

## 📦 Submission Package
The final submission includes the following components:
 1. **Full Android Studio Project**: Entire zipped folder containing all source code and resources.
 2. **Screenshots**: Visual documentation of the application in action.
 3. **APK File**: Optional executable file for direct testing on physical devices.

---

<p align="center">
  <i>This project fulfills the requirements for the Android Development Course (Java).</i>
</p>