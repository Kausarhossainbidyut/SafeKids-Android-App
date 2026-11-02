# 🤝 Contributing to SafeKids

প্রথমে, SafeKids project এ contribute করার জন্য আপনাকে ধন্যবাদ! 🎉

এই guide টি আপনাকে contribution process বুঝতে সাহায্য করবে।

---

## 📋 Table of Contents

1. [Code of Conduct](#code-of-conduct)
2. [Getting Started](#getting-started)
3. [How to Contribute](#how-to-contribute)
4. [Development Workflow](#development-workflow)
5. [Coding Standards](#coding-standards)
6. [Commit Guidelines](#commit-guidelines)
7. [Pull Request Process](#pull-request-process)
8. [Testing Guidelines](#testing-guidelines)
9. [Documentation](#documentation)
10. [Community](#community)

---

## 📜 Code of Conduct

### Our Pledge

SafeKids project একটি harassment-free environment maintain করে। আমরা চাই:

- ✅ সবার সাথে সম্মানজনক আচরণ
- ✅ Constructive feedback
- ✅ Open-minded discussion
- ❌ Harassment বা discriminatory language
- ❌ Personal attacks

### Reporting

যদি কোনো inappropriate behavior দেখেন, contact করুন: [YOUR_EMAIL@example.com]

---

## 🚀 Getting Started

### Prerequisites

- **Git** installed
- **Android Studio** Ladybug 2024.2.1+ (বা latest)
- **JDK 17** (LTS)
- **GitHub account**
- Basic knowledge of Kotlin & Android development

### Fork & Clone

1. **Fork** this repository (click "Fork" button উপরে)

2. **Clone your fork:**
   ```bash
   git clone https://github.com/YOUR_USERNAME/SafeKids-Android.git
   cd SafeKids-Android
   ```

3. **Add upstream remote:**
   ```bash
   git remote add upstream https://github.com/ORIGINAL_OWNER/SafeKids-Android.git
   ```

4. **Verify remotes:**
   ```bash
   git remote -v
   # origin    https://github.com/YOUR_USERNAME/SafeKids-Android.git (fetch)
   # origin    https://github.com/YOUR_USERNAME/SafeKids-Android.git (push)
   # upstream  https://github.com/ORIGINAL_OWNER/SafeKids-Android.git (fetch)
   # upstream  https://github.com/ORIGINAL_OWNER/SafeKids-Android.git (push)
   ```

### Setup Development Environment

1. **Open in Android Studio:**
   - File → Open → Select cloned folder
   - Wait for Gradle sync

2. **Configure Keystore (optional for debug):**
   ```bash
   cp keystore.properties.template keystore.properties
   # Edit if needed, or skip for debug builds
   ```

3. **Build & Run:**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run tests:**
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## 💡 How to Contribute

### Types of Contributions

#### 🐛 Bug Reports
- Search existing [issues](https://github.com/ORIGINAL_OWNER/SafeKids-Android/issues) first
- Use issue template
- Include:
  - Clear description
  - Steps to reproduce
  - Expected vs actual behavior
  - Screenshots (if applicable)
  - Device info (Android version, manufacturer)
  - App version

#### ✨ Feature Requests
- Check [roadmap](docs/ROADMAP.md) first
- Open a [discussion](https://github.com/ORIGINAL_OWNER/SafeKids-Android/discussions) first
- Explain:
  - Problem it solves
  - Proposed solution
  - Alternative solutions considered
  - Impact on existing features

#### 🔧 Code Contributions
- Bug fixes
- New features
- Performance improvements
- Code refactoring
- Test coverage

#### 📝 Documentation
- README improvements
- Code comments
- API documentation
- Tutorials
- Translations

#### 🎨 Design
- UI/UX improvements
- Icons & graphics
- Material 3 design compliance

---

## 🔄 Development Workflow

### Branch Strategy

```
main
├── develop (development branch)
│   ├── feature/feature-name
│   ├── bugfix/issue-123
│   └── refactor/component-name
└── release/v1.0.0
```

### Creating a Branch

```bash
# Update your fork
git checkout develop
git pull upstream develop

# Create feature branch
git checkout -b feature/your-feature-name

# Or for bug fix
git checkout -b bugfix/issue-123
```

### Branch Naming Convention

| Type | Format | Example |
|------|--------|---------|
| Feature | `feature/short-description` | `feature/add-time-limits` |
| Bug Fix | `bugfix/issue-number` | `bugfix/issue-123` |
| Refactor | `refactor/component-name` | `refactor/auth-repository` |
| Docs | `docs/what-you-changed` | `docs/update-readme` |
| Test | `test/what-you-test` | `test/login-viewmodel` |

---

## 📏 Coding Standards

### Kotlin Style Guide

Follow [Official Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)

**Key Points:**

#### Naming
```kotlin
// Classes: PascalCase
class UserRepository

// Functions: camelCase
fun getUserData()

// Constants: UPPER_SNAKE_CASE
const val MAX_RETRY_COUNT = 3

// Private properties: camelCase with underscore prefix
private val _userData = MutableStateFlow<User?>(null)
val userData = _userData.asStateFlow()
```

#### Formatting
```kotlin
// 4 spaces indentation (not tabs)
class Example {
    fun doSomething() {
        if (condition) {
            // code
        }
    }
}

// Line length: 120 characters max
// Break long lines intelligently
```

#### Imports
```kotlin
// Organize imports
// Remove unused imports
// No wildcard imports (import com.example.*)
```

### Architecture Guidelines

#### MVVM Pattern
```
UI Layer (Compose)
    ↓
ViewModel
    ↓
Repository (Domain)
    ↓
Data Source (Room/API)
```

#### Clean Architecture
```kotlin
// Domain models (no Android dependencies)
data class User(val id: String, val name: String)

// Data layer (Android-specific)
@Entity(tableName = "users")
data class UserEntity(...)

// Mapping between layers
fun UserEntity.toDomain(): User
```

#### Dependency Injection (Hilt)
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SafeKidsDatabase
}
```

### Compose Guidelines

```kotlin
// Stateless composables
@Composable
fun UserCard(user: User, onUserClick: (User) -> Unit) {
    // UI only, no business logic
}

// State hoisting
@Composable
fun UserScreen(viewModel: UserViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    UserContent(uiState, onAction = viewModel::handleAction)
}

// Preview
@Preview(showBackground = true)
@Composable
fun UserCardPreview() {
    SafeKidsTheme {
        UserCard(User("1", "John"), {})
    }
}
```

### Error Handling

```kotlin
// Use sealed class for results
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// Repository pattern
suspend fun getUser(id: String): Result<User> {
    return try {
        val user = database.userDao().getUser(id)
        Result.Success(user)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
```

---

## 📝 Commit Guidelines

### Commit Message Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

| Type | Description | Example |
|------|-------------|---------|
| `feat` | New feature | `feat(auth): add biometric login` |
| `fix` | Bug fix | `fix(database): resolve migration crash` |
| `docs` | Documentation | `docs(readme): update installation steps` |
| `style` | Code style (formatting) | `style(login): reformat LoginScreen` |
| `refactor` | Code refactoring | `refactor(repo): simplify UserRepository` |
| `test` | Add/update tests | `test(auth): add LoginViewModel tests` |
| `chore` | Maintenance | `chore(deps): update Compose to 1.6.0` |
| `perf` | Performance improvement | `perf(db): optimize query performance` |
| `ci` | CI/CD changes | `ci(actions): add release workflow` |

### Examples

#### Good ✅
```bash
git commit -m "feat(blocking): add YouTube Shorts detection"

git commit -m "fix(auth): prevent crash on empty password

- Added input validation before encryption
- Show error message to user
- Added unit test for edge case

Fixes #123"

git commit -m "docs(contributing): add commit guidelines section"
```

#### Bad ❌
```bash
git commit -m "fixed bug"
git commit -m "changes"
git commit -m "asdfasdf"
git commit -m "WIP"
```

### Tips

- ✅ Present tense: "add feature" not "added feature"
- ✅ Imperative mood: "fix bug" not "fixes bug"
- ✅ Lowercase subject line
- ✅ No period at the end
- ✅ Body explains "what" and "why", not "how"
- ✅ Reference issues: "Fixes #123", "Closes #456"

---

## 🔀 Pull Request Process

### Before Creating PR

1. ✅ **Update from upstream:**
   ```bash
   git checkout develop
   git pull upstream develop
   git checkout your-branch
   git rebase develop
   ```

2. ✅ **Run tests:**
   ```bash
   ./gradlew testDebugUnitTest
   ./gradlew connectedDebugAndroidTest
   ```

3. ✅ **Build successfully:**
   ```bash
   ./gradlew assembleDebug
   ```

4. ✅ **Lint check:**
   ```bash
   ./gradlew ktlintCheck
   ```

5. ✅ **Code review yourself:**
   - Check for debug logs
   - Remove unused code
   - Add comments for complex logic

### Creating Pull Request

1. **Push to your fork:**
   ```bash
   git push origin your-branch
   ```

2. **Open PR on GitHub:**
   - Go to original repository
   - Click "New Pull Request"
   - Select your branch
   - Fill PR template

### PR Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Related Issue
Fixes #(issue number)

## Changes Made
- Change 1
- Change 2
- Change 3

## Screenshots (if applicable)
[Add screenshots]

## Testing
- [ ] Unit tests passed
- [ ] Tested on device/emulator
- [ ] No new warnings

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-reviewed the code
- [ ] Commented complex code
- [ ] Updated documentation
- [ ] No breaking changes (or documented)
```

### PR Review Process

1. **Automated checks** will run (CI/CD)
2. **Reviewers** will be assigned
3. **Address feedback:**
   ```bash
   # Make changes
   git add .
   git commit -m "fix: address review feedback"
   git push origin your-branch
   ```
4. **Approval** required before merge
5. **Squash and merge** to main/develop

### After Merge

```bash
# Delete local branch
git checkout develop
git branch -D your-branch

# Delete remote branch
git push origin --delete your-branch

# Update local develop
git pull upstream develop
```

---

## 🧪 Testing Guidelines

### Test Structure

```
app/src/
├── test/              # Unit tests
│   └── java/com/safekids/
│       ├── viewmodel/
│       ├── repository/
│       └── util/
└── androidTest/       # Instrumented tests
    └── java/com/safekids/
        ├── database/
        └── ui/
```

### Unit Tests

```kotlin
@Test
fun `login with valid credentials returns success`() = runTest {
    // Given
    val credentials = LoginData("user@test.com", "password123")
    
    // When
    val result = repository.login(credentials)
    
    // Then
    assertTrue(result is Result.Success)
}
```

### Compose UI Tests

```kotlin
@Test
fun loginScreen_displaysErrorOnInvalidInput() {
    composeTestRule.setContent {
        SafeKidsTheme {
            LoginScreen()
        }
    }
    
    composeTestRule.onNodeWithTag("loginButton").performClick()
    composeTestRule.onNodeWithText("Invalid input").assertIsDisplayed()
}
```

### Test Coverage

- **Minimum:** 60% coverage for new code
- **Target:** 80% coverage
- **Critical paths:** 100% coverage (auth, security, data encryption)

---

## 📚 Documentation

### Code Comments

```kotlin
/**
 * Encrypts user password using PBKDF2 with random salt.
 *
 * @param password Plain text password to encrypt
 * @return Encrypted password with format: "salt$hash"
 * @throws SecurityException if encryption fails
 */
fun encryptPassword(password: String): String
```

### README Updates

যদি নতুন feature add করেন:
- Update feature list in README
- Add usage example
- Update screenshots (if UI changed)

### API Documentation

Public APIs এর জন্য KDoc লিখুন:
```kotlin
/**
 * Repository for managing parent user authentication.
 *
 * This repository handles user login, registration, and password recovery.
 * All operations are performed on background thread using Coroutines.
 *
 * Example usage:
 * ```
 * val result = authRepository.login(credentials)
 * when (result) {
 *     is Result.Success -> // Handle success
 *     is Result.Error -> // Handle error
 * }
 * ```
 */
interface AuthRepository
```

---

## 👥 Community

### Communication Channels

- 💬 **GitHub Discussions:** Questions, ideas, show & tell
- 🐛 **GitHub Issues:** Bug reports, feature requests
- 📧 **Email:** [YOUR_EMAIL@example.com] for sensitive matters

### Getting Help

**Before asking:**
1. Check [README](README.md)
2. Search [existing issues](https://github.com/ORIGINAL_OWNER/SafeKids-Android/issues)
3. Read [documentation](docs/)

**When asking:**
- Provide context
- Include error messages
- Share relevant code snippets
- Mention what you've tried

### Recognition

Contributors will be:
- Listed in [CONTRIBUTORS.md](CONTRIBUTORS.md)
- Mentioned in release notes
- Given credit in commit history

---

## 🎯 Good First Issues

নতুন? এই issues দিয়ে শুরু করুন:

- Label: `good first issue`
- Label: `help wanted`
- Label: `documentation`

[View Good First Issues](https://github.com/ORIGINAL_OWNER/SafeKids-Android/issues?q=is%3Aissue+is%3Aopen+label%3A%22good+first+issue%22)

---

## 📊 Project Priorities

### High Priority
- 🔴 Security vulnerabilities
- 🔴 Critical bugs (crashes, data loss)
- 🟡 Performance issues

### Medium Priority
- 🟡 Feature requests from roadmap
- 🟡 UI/UX improvements
- 🟢 Code refactoring

### Low Priority
- 🟢 Nice-to-have features
- 🟢 Documentation improvements
- 🟢 Code style fixes

---

## ✅ Checklist for Contributors

Before submitting PR, verify:

- [ ] Code compiles without errors
- [ ] All tests pass
- [ ] No new lint warnings
- [ ] Code follows style guide
- [ ] Complex code is commented
- [ ] Documentation updated (if needed)
- [ ] Commit messages follow convention
- [ ] PR description is clear
- [ ] Screenshots added (for UI changes)
- [ ] No hardcoded strings (use strings.xml)
- [ ] No security vulnerabilities introduced
- [ ] Backward compatible (or breaking change documented)

---

## 🙏 Thank You!

আপনার contribution SafeKids কে আরো ভালো করবে! 

Questions? Open a [discussion](https://github.com/ORIGINAL_OWNER/SafeKids-Android/discussions)!

---

<div align="center">

**Happy Contributing! 🎉**

[⬆ Back to Top](#-contributing-to-safekids)

</div>
