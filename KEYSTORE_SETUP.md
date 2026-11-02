# 🔐 Keystore Setup Guide

## For Development Team

### Initial Setup (Already Done)

The project already has a release keystore configured. If you're setting up locally:

1. **Get the keystore file** from the team lead
2. **Place it in project root**: `release.keystore`
3. **Create `keystore.properties`** from template

### Creating keystore.properties

```bash
# Copy the template
cp keystore.properties.template keystore.properties

# Edit with your credentials
# storeFile=release.keystore
# storePassword=YOUR_PASSWORD
# keyAlias=safekids
# keyPassword=YOUR_PASSWORD
```

### For CI/CD (GitHub Actions)

The following secrets need to be configured in GitHub repository:

- `KEYSTORE_BASE64`: Base64 encoded keystore file
- `KEYSTORE_PASSWORD`: Store password
- `KEY_ALIAS`: Key alias (safekids)
- `KEY_PASSWORD`: Key password

### Generating New Keystore (If Needed)

```bash
keytool -genkeypair -v \
  -keystore release.keystore \
  -alias safekids \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass YOUR_PASSWORD \
  -keypass YOUR_PASSWORD \
  -dname "CN=SafeKids, OU=ParentalControl, O=SafeKids, L=Dhaka, ST=Dhaka, C=BD"
```

### Verifying Keystore

```bash
keytool -list -v -keystore release.keystore -storepass YOUR_PASSWORD
```

### Important Notes

⚠️ **NEVER commit `keystore.properties` or `release.keystore` to Git!**

✅ Only commit `keystore.properties.template`

📝 Keep backup of keystore file in secure location

🔒 Store passwords in secure password manager

---

## Current Keystore Info

- **Alias**: safekids
- **Algorithm**: SHA256withRSA (2048-bit)
- **Valid Until**: 2054
- **SHA256 Fingerprint**: `53:89:EF:C6:76:4B:E0:6E:20:C7:4D:21:6B:ED:08:87:BC:EA:85:A0:AC:76:26:CF:3C:2F:5F:C0:28:DF:AD:C0`

This fingerprint is used for:
- Google Play Console (if publishing)
- Firebase configuration
- Social login provider configuration (Google, Facebook)
- Third-party API integrations
