# 🚀 GitHub Actions Workflows Guide

## Available Workflows

### 1. 🔨 Android CI (`android-ci.yml`)

**Triggers:**
- Push to `main` or `develop` branch
- Pull request to `main` or `develop` branch

**What it does:**
- ✅ Builds debug APK
- ✅ Runs unit tests
- ✅ Uploads APK as artifact (7 days retention)
- ✅ Uploads test results

**Artifacts:**
- `safekids-debug-apk` - Debug APK for testing

---

### 2. 📦 Release Build (`release.yml`)

**Triggers:**
- Push tag with format `v*.*.*` (e.g., `v1.0.0`)
- Manual trigger via GitHub Actions UI

**What it does:**
- ✅ Builds signed release APK
- ✅ Creates GitHub Release
- ✅ Uploads APK to release
- ✅ Stores artifact (90 days)

**How to create release:**

#### Option A: Using Git Tags
```bash
# Create and push tag
git tag v1.0.0
git push origin v1.0.0
```

#### Option B: Manual Trigger
1. Go to **Actions** tab
2. Select **"Release Build"**
3. Click **"Run workflow"**
4. Enter version (e.g., `1.0.0`)
5. Click **"Run workflow"**

**Output:**
- GitHub Release with APK file
- File name: `SafeKids-v{VERSION}.apk`
- Retention: 90 days as artifact

---

### 3. ✅ PR Build Check (`pr-check.yml`)

**Triggers:**
- Pull request opened, synchronized, or reopened

**What it does:**
- ✅ Checks code style (ktlint)
- ✅ Builds debug APK
- ✅ Runs unit tests
- ✅ Posts build status comment on PR
- ✅ Generates test report

**PR Comments:**
Automatically comments on PR with:
- ✅/❌ Build status
- Commit SHA
- Workflow name

---

## Workflow Diagram

```
┌─────────────────────────────────────────────────┐
│  Developer Activity                             │
└─────────────────────────────────────────────────┘
              │
              ├─── Push to main/develop
              │    └──> Android CI (builds debug APK)
              │
              ├─── Create Pull Request
              │    └──> PR Build Check (builds + tests + comments)
              │
              └─── Push tag v1.0.0
                   └──> Release Build (signed APK + GitHub Release)
```

---

## Build Times

Approximate build times on GitHub Actions:

- **Debug Build:** 3-5 minutes
- **Release Build:** 5-8 minutes
- **Tests:** 1-2 minutes

Total: ~6-10 minutes per workflow

---

## Artifacts & Retention

| Workflow | Artifact Name | Retention | Size |
|----------|--------------|-----------|------|
| Android CI | safekids-debug-apk | 7 days | ~10-20 MB |
| Release | safekids-release-apk-v{VERSION} | 90 days | ~8-15 MB |
| PR Check | test-results | 7 days | ~1 MB |

---

## First Time Setup Checklist

Before running workflows:

1. ✅ Create repository on GitHub
2. ✅ Push code to GitHub
3. ✅ Configure secrets (see `.github/SECRETS_SETUP.md`)
4. ✅ Enable GitHub Actions (if disabled)
5. ✅ Make first push to trigger CI

---

## Troubleshooting

### Build fails with "Keystore not found"
- Check if `KEYSTORE_BASE64` secret is set correctly
- Verify base64 encoding is correct

### Build fails with "Signing failed"
- Verify `KEYSTORE_PASSWORD` and `KEY_PASSWORD` are correct
- Check `KEY_ALIAS` matches keystore alias

### Tests fail
- Run tests locally first: `./gradlew testDebugUnitTest`
- Check test logs in workflow artifacts

### Workflow doesn't trigger
- Check branch names (must be `main` or `develop`)
- Verify workflow files are in `.github/workflows/`
- Check repository Actions settings (Actions may be disabled)

---

## Release Process

### Recommended Release Flow:

1. **Development:**
   ```bash
   git checkout develop
   # Make changes, commit
   git push origin develop
   ```
   → Triggers Android CI

2. **Create Release Branch:**
   ```bash
   git checkout -b release/1.0.0
   # Update version in build.gradle.kts
   # Update CHANGELOG.md
   git commit -am "Prepare release 1.0.0"
   git push origin release/1.0.0
   ```

3. **Merge to Main:**
   ```bash
   git checkout main
   git merge release/1.0.0
   git push origin main
   ```

4. **Create Tag:**
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```
   → Triggers Release Build
   → Creates GitHub Release with signed APK

5. **Share Release:**
   - Go to GitHub Releases page
   - Edit release notes
   - Share download link with users

---

## Version Naming

Follow Semantic Versioning:

- **Major:** Breaking changes (e.g., `v2.0.0`)
- **Minor:** New features (e.g., `v1.1.0`)
- **Patch:** Bug fixes (e.g., `v1.0.1`)

Examples:
- `v1.0.0` - Initial release
- `v1.1.0` - Added child monitoring features
- `v1.1.1` - Fixed login bug
- `v2.0.0` - Complete UI redesign

---

## Monitoring

### View Workflow Status:

1. Go to **Actions** tab in GitHub
2. See all workflow runs
3. Click on any run for details
4. Download artifacts if needed

### Badges (Optional):

Add to README.md:

```markdown
![Android CI](https://github.com/YOUR_USERNAME/SafeKids-Android/workflows/Android%20CI/badge.svg)
![Release](https://github.com/YOUR_USERNAME/SafeKids-Android/workflows/Release%20Build/badge.svg)
```
