# Notely 📝

<p align="center">
  <strong>Ultra-clean, minimalist note-taking and to-do productivity application inspired by Google Keep and Transsion Notepad.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" alt="Platform" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white" alt="Language" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose%20%2B%20Material%203-4285F4?logo=jetpackcompose&logoColor=white" alt="Compose" />
  <img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License" />
  <img src="https://img.shields.io/badge/Privacy-100%25%20Local--First-success" alt="Privacy" />
</p>

---

## 🌟 Overview

**Notely** is crafted by **OperatSI** to deliver an elegant, distraction-free note-taking and task-tracking experience. Combining the fluid grid simplicity of Google Keep with the refined utility of Transsion Notepad, Notely empowers you to capture thoughts, manage actionable checklists, and schedule reminders with zero friction and absolute privacy.

> **Privacy First**: Everything you write remains 100% on your device. Zero external cloud sync, zero telemetry, zero tracking, and zero advertising SDKs. Read our full [Privacy Policy](PRIVACY_POLICY.md).

---

## ✨ Features

### 📋 Rich Note Management
- **Instant Note Creation**: Quickly capture thoughts, ideas, and meeting notes.
- **Visual Color Palette**: Personalize individual notes with curated pastel and corporate hues.
- **Pin Key Notes**: Keep critical notes pinned right at the top for immediate access.
- **WYSIWYG Rich Text Formatting**: Format notes with Bold, Italic, Underline, Strikethrough, and Headings (H1–H5) with **zero raw markdown symbols** cluttering your view.
- **Live Search**: Rapid real-time search across titles and content.
- **Grid & Staggered Views**: Seamlessly toggle between multi-column grid and single-column layout.

### ✅ To-Do & Task Productivity
- **Interactive Checklists**: One-tap completion toggles with smooth animated transitions.
- **Priority Classifications**: Organize tasks with High, Medium, and Low priority tags.
- **Custom Due Date & Alarms**: Schedule reminders with exact dates and 12-hour AM/PM times.
- **Completed Archive**: Keep focus on active items while preserving completed history.

### ⏰ Exact Reminders & Alarms
- **Clean 12-Hour AM/PM Selector**: Corporate time picker with precise 12-hour format, 5-minute intervals, and AM/PM segmented controls.
- **System AlarmManager Integration**: Reliable notifications triggered at the exact requested minute.
- **Boot Persistence**: Automatically re-registers scheduled alarms upon device reboot.

### 🗂 Multi-Selection & Batch Actions
- **Long-Press Selection**: Enter multi-select mode with a single hold or tap on any note or task.
- **Batch Deletion**: Delete multiple notes at once with a floating bottom action bar and immediate undo protection.

### 📱 Adaptive & Responsive Design
- Optimized across all Android form factors:
  - **Phones** (portrait & landscape with adaptive bottom navigation)
  - **7-inch & 10-inch Tablets** (responsive navigation rail, multi-column grid)
  - **Foldables & Chromebooks** (dynamic window resizing, keyboard navigation support)

### 🔒 100% On-Device Privacy
- Sandboxed local storage for notes and tasks.
- Hardware-backed file encryption on modern Android devices.
- No sign-in or account creation required.

---

## 🛡️ Privacy Policy

Notely is committed to complete user privacy:
- **No Data Collection**: We do not collect or transmit personal information.
- **No Third-Party SDKs**: No ads, trackers, or telemetry.
- **Full Policy**: [Read the Official Privacy Policy](PRIVACY_POLICY.md).

---

## 💬 Community & Discussion

Have questions, ideas, or feedback? Join our community discussions on GitHub:
- **GitHub Discussions**: [https://github.com/Operatsi/Notely/discussions](https://github.com/Operatsi/Notely/discussions)

---

## 🛠️ Architecture & Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/) (100% Kotlin codebase)
- **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://m3.material.io/)
- **Architecture**: MVVM with unidirectional data flow (UDF) using Kotlin `StateFlow` and Coroutines
- **Storage**: Clean local persistence via Android sandboxed preferences & JSON serialization
- **System Services**: `AlarmManager`, `NotificationManager`, and `BroadcastReceiver`

---

## 🚀 Building & Running from Source

### Prerequisites
- Android Studio Ladybug (2024.2.1+) or newer
- Android SDK 35 (compileSdk 35, minSdk 24)
- JDK 17 or higher

### Build Instructions

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Operatsi/Notely.git
   cd Notely
   ```

2. **Build the debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```

3. **Install on a connected device:**
   ```bash
   ./gradlew installDebug
   ```

---

## 📄 License

```
Copyright 2026 OperatSI

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
Full details available in [LICENSE](LICENSE).

---

<p align="center">
  Developed by <strong>OperatSI</strong>
</p>
