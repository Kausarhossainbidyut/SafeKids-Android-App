# 🛡️ SafeKids - শিশু-নিরাপদ অ্যাপ

<div align="center">

[![Android CI](https://github.com/YOUR_USERNAME/SafeKids-Android/workflows/Android%20CI/badge.svg)](https://github.com/YOUR_USERNAME/SafeKids-Android/actions)
[![Release](https://github.com/YOUR_USERNAME/SafeKids-Android/workflows/Release%20Build/badge.svg)](https://github.com/YOUR_USERNAME/SafeKids-Android/releases)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Version](https://img.shields.io/badge/version-1.0.0-green.svg)](https://github.com/YOUR_USERNAME/SafeKids-Android/releases)

**শিশুদের Reels/Shorts আসক্তি, অ্যাডাল্ট কনটেন্ট ও বিজ্ঞাপন থেকে রক্ষা করুন**

[📥 Download APK](#download) • [📖 Documentation](#documentation) • [🤝 Contributing](#contributing)

</div>

---

## 📱 SafeKids কি?

SafeKids একটি **Parental Control + Digital Wellbeing** অ্যান্ড্রয়েড অ্যাপ্লিকেশন যা শিশুদের ডিজিটাল নিরাপত্তা নিশ্চিত করে।

### 🎯 মূল উদ্দেশ্য

- ✅ **YouTube Shorts, Instagram Reels, TikTok** ব্লক করুন
- ✅ **অ্যাডাল্ট কনটেন্ট** থেকে সুরক্ষা
- ✅ **বিজ্ঞাপন ও ট্র্যাকার** ব্লক
- ✅ **স্ক্রিন টাইম** নিয়ন্ত্রণ
- ✅ **অ্যাপ ম্যানেজমেন্ট** ও লকিং
- ✅ **লোকাল-অনলি**: সব ডেটা encrypted local storage এ

---

## ⚡ Key Features

### 🔒 কনটেন্ট ব্লকিং
- YouTube Shorts, Instagram Reels, Facebook Reels, TikTok ব্লক
- সব ব্রাউজারে অ্যাডাল্ট ওয়েবসাইট ব্লক (DNS/VPN)
- অ্যাডাল্ট কিওয়ার্ড ডিটেকশন
- SafeSearch বাধ্যতামূলক (Google, Bing, YouTube)
- ক্যাটেগরি-ভিত্তিক সাইট ব্লকিং

### 🚫 অ্যাড ব্লকিং
- নেটওয়ার্ক-লেভেল বিজ্ঞাপন ব্লক
- ট্র্যাকার ও পপআপ ব্লক
- কুকি ব্যানার ব্লক
- রিয়েল-টাইম ব্লক কাউন্টার

### ⏰ সময় নিয়ন্ত্রণ
- অ্যাপ-ভিত্তিক দৈনিক সময় সীমা
- মোট স্ক্রিন টাইম লিমিট
- পড়ার সময়/ঘুমের সময় শিডিউল
- "আরও ১৫ মিনিট" অনুরোধ ফিচার

### 📊 অভিভাবক ড্যাশবোর্ড
- রিয়েল-টাইম অ্যাক্টিভিটি রিপোর্ট
- দৈনিক/সাপ্তাহিক/মাসিক রিপোর্ট
- ব্লক হওয়া চেষ্টার লগ
- একাধিক শিশুর প্রোফাইল

### 🔐 সুরক্ষা
- আনইনস্টল প্রটেকশন
- Factory Reset ট্যাম্পার অ্যালার্ট
- এনক্রিপ্টেড লোকাল ব্যাকআপ
- Device Admin সুরক্ষা

---

## 🏗️ Tech Stack

### Core
- **Language:** Kotlin 1.9.22
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM + Clean Architecture
- **DI:** Hilt (Dagger)
- **Async:** Kotlin Coroutines + Flow

### Data & Storage
- **Database:** Room (SQLite) + SQLCipher encryption
- **Preferences:** DataStore
- **Security:** Android Keystore, EncryptedSharedPreferences

### Blocking Engine
- **Content Detection:** AccessibilityService
- **Time Tracking:** UsageStatsManager
- **Network Filter:** VpnService (Local DNS)
- **Browser:** GeckoView + uBlock Origin
- **Uninstall Protection:** DevicePolicyManager

### Build & CI/CD
- **Build Tool:** Gradle 8.4
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 34 (Android 14)
- **CI/CD:** GitHub Actions
- **Signing:** Release keystore with ProGuard

---

## 📥 Download & Installation {#download}

### ⬇️ Download Latest Version

[![Download APK](https://img.shields.io/badge/Download-APK-green?style=for-the-badge&logo=android)](https://github.com/YOUR_USERNAME/SafeKids-Android/releases/latest)

1. **[Releases Page](https://github.com/YOUR_USERNAME/SafeKids-Android/releases)** থেকে latest APK download করুন
2. File name: `SafeKids-v{VERSION}.apk`

### 📲 Installation Steps

1. **APK Download করুন** (উপরের link থেকে)
2. **Settings → Security → Unknown Sources** enable করুন
3. **Downloaded APK install করুন**
4. প্রথম খোলায় সব **permissions** দিন:
   - Accessibility Service
   - Usage Stats Access
   - VPN Service
   - Device Admin
   - Overlay Permission
5. **Registration সম্পন্ন করুন:**
   - নাম দিন
   - ফোন নম্বর দিন
   - পাসওয়ার্ড সেট করুন
   - Recovery codes সংরক্ষণ করুন
6. **Mode নির্বাচন করুন:**
   - **Parent Mode** (অভিভাবকের ফোনে)
   - **Child Mode** (শিশুর ফোনে)

### ⚠️ Important Notes

- ✅ **Open Source & Free** - কোনো hidden cost নেই
- ✅ **No Play Store** - GitHub থেকে সরাসরি download
- ✅ **Privacy First** - সব ডেটা আপনার ফোনে encrypted
- ✅ **No Cloud/Backend** - কোনো server এ data যায় না
- ⚠️ **Permissions জরুরি** - সব permissions না দিলে কাজ করবে না

---

## 🚀 Getting Started (For Developers)

### Prerequisites

- **Android Studio:** Ladybug 2024.2.1+ (বা latest)
- **JDK:** 17 (LTS)
- **Gradle:** 8.4
- **Min SDK:** 26
- **Kotlin:** 1.9.22

### Setup Instructions

1. **Clone Repository:**
   ```bash
   git clone https://github.com/YOUR_USERNAME/SafeKids-Android.git
   cd SafeKids-Android
   ```

2. **Open in Android Studio:**
   - File → Open → Select project folder
   - Wait for Gradle sync

3. **Configure Signing (Optional for debug):**
   ```bash
   # Create keystore.properties from template
   cp keystore.properties.template keystore.properties
   
   # Edit keystore.properties with your credentials
   # (For development, you can skip this - debug signing will be used)
   ```

4. **Build & Run:**
   ```bash
   # Build debug APK
   ./gradlew assembleDebug
   
   # Install on connected device
   ./gradlew installDebug
   
   # Or click "Run" in Android Studio
   ```

### Build Variants

- **Debug:** `com.safekids.app.debug` - Development build with debug logs
- **Release:** `com.safekids.app` - Signed production build

---

## 📖 Documentation {#documentation}

### For Users
- [📱 Installation Guide (Bengali)](docs/INSTALLATION_BN.md)
- [🎯 Features Overview](docs/FEATURES.md)
- [❓ FAQ](docs/FAQ.md)
- [🔧 Troubleshooting](docs/TROUBLESHOOTING.md)

### For Developers
- [🏗️ Architecture Overview](docs/ARCHITECTURE.md)
- [📝 Implementation Guide](IMPLEMENTATION_GUIDE.md)
- [🔑 Keystore Setup](KEYSTORE_SETUP.md)
- [⚙️ GitHub Actions Workflows](.github/WORKFLOWS_GUIDE.md)
- [🔒 Security Guide](docs/SECURITY.md)
- [📊 Week 1-2 Progress](WEEK_1_2_REMAINING_FILES.md)

### Project Blueprint
- [🗺️ Complete Blueprint (Bengali)](../SafeKids_App_Blueprint_BN.md) - Detailed feature specifications

---

## 🤝 Contributing {#contributing}

আমরা contributions স্বাগত জানাই! 🎉

### How to Contribute

1. **Fork** this repository
2. **Create a branch:**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Make changes** and commit:
   ```bash
   git commit -m "Add: amazing feature"
   ```
4. **Push to branch:**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Create Pull Request**

### Contribution Guidelines

- Read [CONTRIBUTING.md](CONTRIBUTING.md) before starting
- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Write meaningful commit messages
- Add tests for new features
- Update documentation as needed

### Areas for Contribution

- 🐛 **Bug Fixes** - Report or fix issues
- ✨ **New Features** - Add requested features
- 📝 **Documentation** - Improve docs, add translations
- 🎨 **UI/UX** - Design improvements
- 🧪 **Testing** - Write unit/integration tests
- 🌍 **Localization** - Add support for more languages

---

## 📂 Project Structure

```
SafeKids-Android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/safekids/
│   │   │   │   ├── data/          # Data layer (Room, DAOs)
│   │   │   │   ├── domain/        # Domain models
│   │   │   │   ├── security/      # Encryption, keystore
│   │   │   │   ├── di/            # Hilt modules
│   │   │   │   ├── ui/            # Compose screens
│   │   │   │   └── viewmodel/     # ViewModels
│   │   │   ├── AndroidManifest.xml
│   │   │   └── res/               # Resources
│   │   └── test/                  # Unit tests
│   ├── build.gradle.kts           # App-level Gradle
│   └── proguard-rules.pro         # ProGuard rules
├── .github/
│   └── workflows/                 # CI/CD workflows
├── gradle/                        # Gradle wrapper
├── build.gradle.kts               # Project-level Gradle
├── settings.gradle.kts
├── .gitignore
├── keystore.properties.template   # Keystore config template
├── KEYSTORE_SETUP.md
├── README.md                      # This file
└── CONTRIBUTING.md
```

---

## 🔐 Security

### Reporting Security Issues

If you discover a security vulnerability, please **DO NOT** open a public issue. Instead:

1. Email: [YOUR_EMAIL@example.com]
2. Include detailed description
3. Steps to reproduce (if applicable)
4. Potential impact

We will respond within 48 hours.

### Security Features

- ✅ Local-only encrypted storage (SQLCipher)
- ✅ Android Keystore for password hashing
- ✅ No data transmission to servers
- ✅ Open source - code is auditable
- ✅ ProGuard code obfuscation in release builds

---

## 📊 Current Status

### Completed ✅

- [x] **Week 0:** GitHub repo setup, keystore, CI/CD workflows
- [x] **Project Setup:** Gradle, Hilt, Compose, Room configured
- [x] **Security Layer:** PasswordHasher, KeystoreManager
- [x] **Database:** Encrypted SQLite with Room
- [x] **Domain Models:** User, Registration, Login, Recovery

### In Progress 🚧

- [ ] **Week 1-2:** Login system, onboarding, mode selection UI
- [ ] **Authentication:** Repository, ViewModels, Compose screens
- [ ] **Navigation:** Screen flow setup

### Upcoming 📅

- [ ] **Week 3-4:** Content blocking engine
- [ ] **Week 5-6:** Ad blocker & safe browser
- [ ] **Week 7-8:** Time control & app management
- [ ] **Week 9+:** Testing, optimization, release

See [Development Roadmap](docs/ROADMAP.md) for detailed timeline.

---

## 🧪 Testing

### Run Tests

```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumented tests
./gradlew connectedDebugAndroidTest

# Coverage report
./gradlew jacocoTestReport
```

### CI/CD

- **Automatic builds** on every push/PR
- **Release builds** on version tags (`v*.*.*`)
- **Test reports** attached to PR comments
- See [Workflows Guide](.github/WORKFLOWS_GUIDE.md)

---

## 📜 License

```
Copyright 2024 SafeKids Project

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

See [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

### Libraries & Tools Used

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI toolkit
- [Hilt](https://dagger.dev/hilt/) - Dependency injection
- [Room](https://developer.android.com/training/data-storage/room) - Local database
- [SQLCipher](https://www.zetetic.net/sqlcipher/) - Database encryption
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) - Async programming
- [Material 3](https://m3.material.io/) - Design system

### Inspired By

- Google Family Link
- Qustodio
- Norton Family
- Brave Browser's ad blocking technology

---

## 📞 Contact & Support

### Get Help

- 📧 **Email:** [YOUR_EMAIL@example.com]
- 💬 **Discussions:** [GitHub Discussions](https://github.com/YOUR_USERNAME/SafeKids-Android/discussions)
- 🐛 **Issues:** [GitHub Issues](https://github.com/YOUR_USERNAME/SafeKids-Android/issues)

### Follow Updates

- ⭐ **Star this repo** to get updates
- 👀 **Watch releases** for new versions
- 📢 **Join discussions** for announcements

---

## 🗺️ Roadmap

### v1.0.0 (Current - Initial Release)
- ✅ Basic authentication & registration
- ✅ Parent/Child mode selection
- ✅ Encrypted local storage
- 🚧 Login system UI

### v1.1.0 (Q2 2024)
- Content blocking (Shorts/Reels)
- Basic time controls
- Activity logging

### v1.2.0 (Q3 2024)
- Ad blocker integration
- Safe browser
- Advanced app management

### v2.0.0 (Q4 2024)
- Dashboard & reports
- Advanced analytics
- Multi-child profiles

See [ROADMAP.md](docs/ROADMAP.md) for detailed plans.

---

## ⚖️ Legal & Privacy

### Distribution

- ✅ **Open Source** - Apache 2.0 License
- ✅ **GitHub Distribution** - Direct APK downloads
- ❌ **NOT on Play Store** - Avoids policy restrictions
- ✅ **No telemetry or analytics** - Your data stays local

### Privacy Policy

- **NO data collection** - কোনো data server এ যায় না
- **NO third-party services** - কোনো external API call নেই
- **Local-only encryption** - সব data device এ encrypted
- **Open source audit** - যে কেউ code review করতে পারে

### Compliance

This app complies with:
- GDPR (EU data protection)
- COPPA (Children's Online Privacy Protection)
- Android security guidelines

---

<div align="center">

**Made with ❤️ for children's digital safety**

[⬆ Back to Top](#-safekids---শিশু-নিরাপদ-অ্যাপ)

</div>
