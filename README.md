# WeekendX 🗓️

**WeekendX** is a feature-rich, modern Android application built with **Kotlin**, **Jetpack Compose**, and **Material 3**. It is designed to effortlessly calculate, analyze, and visualize weekends, holiday and date ranges, monthly/yearly summaries, and custom weekend groupings with precision and elegance.

---

## ✨ Key Features

- **📅 Advanced Date & Weekend Calculation**: Calculate total days, weekend days (Saturdays & Sundays), and weekday breakdowns between any custom date range.
- **📊 Monthly & Yearly Summaries**: Group and visualize breakdown statistics aggregated by month and year.
- **🔍 Calendar Visualizer**: Interactive calendar preview highlighting weekend distributions and selected date spans.
- **📜 Calculation History**: Save, review, and manage past calculations for quick access and reference.
- **⚙️ Customizable Settings & Preferences**: Tailor weekend preferences, display formats, and app behaviors.
- **🎨 Modern Material 3 UI**: Immersive, responsive design with polished components, dynamic theme support, and haptic feedback.

---

## 🛠️ Tech Stack & Architecture

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://m3.material.io/).
- **Architecture**: Unidirectional Data Flow (UDF) using `ViewModel`, `StateFlow`, and Jetpack Navigation Compose.
- **Language**: [Kotlin](https://kotlinlang.org/)
- **Asynchronous Processing**: Kotlin Coroutines & Flow.
- **Testing**: JUnit for robust domain logic and calculation unit testing.

### Project Structure

```text
com.example.weekendx/
├── data/
│   ├── model/         # Data classes (AppPreferences, Summary, Result, etc.)
│   └── repository/    # Preferences & History repository implementation
├── domain/
│   ├── calculator/    # Core weekend & date calculation logic
│   ├── calendar/      # Calendar generation & visualizer utilities
│   ├── formatter/     # Date and number formatting helpers
│   └── generator/     # Monthly/Yearly summary generators
├── navigation/        # Navigation routes and NavHost setup
├── ui/
│   ├── about/         # About screen
│   ├── components/    # Reusable Compose components (Cards, Dialogs, Pickers, etc.)
│   ├── history/       # Saved calculations history screen
│   ├── home/          # Main calculator screen & ViewModel
│   ├── settings/      # App settings screen
│   ├── splash/        # Splash screen
│   └── theme/         # Material 3 colors, typography, and theme
└── util/              # Haptic feedback and sharing utilities
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Arctic Fox or newer (Ladybug / Koala / Jellyfish recommended).
- **JDK**: Java 11 or higher.
- **Android SDK**: Compile SDK 37 / Min SDK 26 (Android 8.0+).

### Installation & Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/abdullah-ateeq/WeekendX.git
   ```
2. **Open the project** in Android Studio.
3. Let Gradle sync and download dependencies.
4. Run the app on an Android emulator or physical device.

---

## 🧪 Testing

The project includes unit tests for core domain logic, calculation algorithms, and formatting.

Run unit tests via terminal:
```bash
./gradlew test
```

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).

---

## 👤 Author

Developed by **Abdullah Ateeq** ([abdullah-ateeq](https://github.com/abdullah-ateeq)).
