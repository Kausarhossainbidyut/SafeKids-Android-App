# 🔐 GitHub Secrets Setup Guide

GitHub Actions workflows এর জন্য repository secrets configure করতে হবে।

## Required Secrets

Navigate to: **Repository → Settings → Secrets and variables → Actions → New repository secret**

### 1. KEYSTORE_BASE64

**Description:** Base64 encoded release keystore file

**How to generate:**

```bash
# Windows (PowerShell)
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release.keystore")) | Set-Clipboard

# Linux/Mac
base64 release.keystore | pbcopy
# Or
cat release.keystore | base64 | xclip -selection clipboard
```

**Value:** Paste the base64 string (should be very long, ~2000-3000 characters)

---

### 2. KEYSTORE_PASSWORD

**Description:** Keystore store password

**Value:** 
```
safekids2024
```

---

### 3. KEY_ALIAS

**Description:** Key alias name

**Value:**
```
safekids
```

---

### 4. KEY_PASSWORD

**Description:** Key password

**Value:**
```
safekids2024
```

---

## Verification

After adding all secrets, your repository secrets should look like:

```
✅ KEYSTORE_BASE64      (Added)
✅ KEYSTORE_PASSWORD    (Added)
✅ KEY_ALIAS            (Added)
✅ KEY_PASSWORD         (Added)
```

## Testing

### Test with Manual Trigger:

1. Go to **Actions** tab
2. Select **"Release Build"** workflow
3. Click **"Run workflow"**
4. Enter version (e.g., `1.0.0`)
5. Click **"Run workflow"**

If successful, a release APK will be created!

---

## Security Notes

⚠️ **NEVER commit secrets to Git!**

✅ Only use GitHub Secrets for sensitive data

🔒 Secrets are encrypted and only exposed during workflow runs

📝 Rotate passwords periodically for better security

---

## Current Keystore Info

- **Alias:** safekids
- **Valid Until:** 2054
- **SHA256:** `53:89:EF:C6:76:4B:E0:6E:20:C7:4D:21:6B:ED:08:87:BC:EA:85:A0:AC:76:26:CF:3C:2F:5F:C0:28:DF:AD:C0`
