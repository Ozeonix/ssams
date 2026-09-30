# SSAMS Student Mobile App – Production Release & Distribution Guide

**Target Institution:** Shree Susanskrit Secondary School (`SHREE_SUSANSKRIT`)  
**Package Name:** `com.artms.ssams_student`  
**Application Name:** SSAMS Student & Parent Portal  
**Document Version:** 1.0.0 (Production Release)  

---

## 1. Overview & Distribution Strategy

To ensure seamless adoption across the Shree Susanskrit Secondary School student and parent body in Nepal, the SSAMS mobile application supports **two complementary distribution channels**:

1. **Direct School Hosted APK (Fastest & Zero Fee):**
   - The compiled release APK (`ssams-student-release.apk`) is hosted directly on the school's Nginx server (`https://susanskrit.edu.np/downloads/ssams-student.apk`).
   - Parents scan a QR code printed on term fee invoices or posters in the school administrative office to download and install instantly.
2. **Google Play Store (Recommended for Production & Auto-Updates):**
   - Distributed via Google Play Console using Android App Bundle (`.aab`).
   - Supports automated background updates, split APK delivery (smaller download size ~15 MB), and verified Google Play Protect security badges.

---

## 2. Generating the Production Release Keystore

The release keystore is used to digitally sign the Android APK/AAB.

### Step 2.1: Run `keytool`
Generate a 4096-bit RSA keystore (valid for 30 years):

```bash
keytool -genkey -v \
  -keystore ~/keystores/ssams-susanskrit-keystore.jks \
  -storetype JKS \
  -keyalg RSA \
  -keysize 4096 \
  -validity 10000 \
  -alias ssams-upload
```

During generation, you will be prompted for:
- Keystore & Key Password (store securely in password manager / vault)
- Organization Name: `Shree Susanskrit Secondary School`
- Country Code: `NP`

> [!CAUTION]
> **Backup the Keystore File:** If the `.jks` file is lost, you will NOT be able to push updates to installed apps on student/parent phones. Store an encrypted copy in off-site backup.

---

## 3. Configuring `key.properties`

In `mobile/ssams-student/android/`, copy the example file:

```bash
cp mobile/ssams-student/android/key.properties.example mobile/ssams-student/android/key.properties
```

Edit `mobile/ssams-student/android/key.properties`:

```properties
keyAlias=ssams-upload
keyPassword=YOUR_SECURE_KEY_PASSWORD
storeFile=/absolute/path/to/keystores/ssams-susanskrit-keystore.jks
storePassword=YOUR_SECURE_STORE_PASSWORD
```

*(Note: `key.properties` and `*.jks` are already added to `.gitignore` to prevent accidental credential leakage).*

---

## 4. Production Build Commands

All builds require injecting production runtime environment flags via `--dart-define`.

### 4.1 Build Single Universal APK (For Direct Download)
Best for direct school download where parents have diverse Android architectures:

```bash
cd mobile/ssams-student

flutter build apk --release \
  --dart-define=API_BASE_URL=https://api.susanskrit.edu.np/api/v1 \
  --dart-define=ENVIRONMENT=production \
  --dart-define=TENANT_CODE=SHREE_SUSANSKRIT
```
**Output Location:** `mobile/ssams-student/build/app/outputs/flutter-apk/app-release.apk`

---

### 4.2 Build ABI-Split APKs (Smallest Download Sizes)
Generates dedicated lightweight APKs (~14 MB each) tailored per CPU architecture:

```bash
cd mobile/ssams-student

flutter build apk --release --split-per-abi \
  --dart-define=API_BASE_URL=https://api.susanskrit.edu.np/api/v1 \
  --dart-define=ENVIRONMENT=production \
  --dart-define=TENANT_CODE=SHREE_SUSANSKRIT
```
**Generated Files:**
- `app-arm64-v8a-release.apk` (Most modern Android phones)
- `app-armeabi-v7a-release.apk` (Older budget 32-bit Android phones)
- `app-x86_64-release.apk` (Tablets / Chromebooks)

---

### 4.3 Build Android App Bundle (AAB for Google Play Console)

```bash
cd mobile/ssams-student

flutter build appbundle --release \
  --dart-define=API_BASE_URL=https://api.susanskrit.edu.np/api/v1 \
  --dart-define=ENVIRONMENT=production \
  --dart-define=TENANT_CODE=SHREE_SUSANSKRIT
```
**Output Location:** `mobile/ssams-student/build/app/outputs/bundle/release/app-release.aab`

---

## 5. Direct School Distribution (Nginx Setup)

Deploy the compiled APK to the school web server so parents can download it anytime:

```bash
# 1. Create downloads directory on server
sudo mkdir -p /var/www/susanskrit.edu.np/downloads

# 2. Copy the release APK
sudo cp build/app/outputs/flutter-apk/app-release.apk /var/www/susanskrit.edu.np/downloads/ssams-student.apk

# 3. Generate SHA256 checksum for verification
sha256sum /var/www/susanskrit.edu.np/downloads/ssams-student.apk > /var/www/susanskrit.edu.np/downloads/ssams-student.apk.sha256

# 4. Set web server permissions
sudo chown -R www-data:www-data /var/www/susanskrit.edu.np/downloads
sudo chmod 644 /var/www/susanskrit.edu.np/downloads/*
```

### Nginx Static File Block (`/etc/nginx/sites-available/susanskrit.conf`):
```nginx
location /downloads/ {
    alias /var/www/susanskrit.edu.np/downloads/;
    autoindex off;
    add_header Content-Disposition 'attachment';
    add_header Cache-Control 'no-cache';
}
```

---

## 6. Parent Installation Guide & SMS Notice Template

### 6.1 Parent Installation Instructions (For School Handout)
1. Open camera or QR scanner on mobile phone and scan the school QR code.
2. When prompted, tap **Download Anyway**.
3. Open the downloaded `ssams-student.apk` file.
4. If Android displays *"Install unknown apps"*, tap **Settings** -> Toggle **Allow from this source** -> Tap **Install**.
5. Launch **SSAMS Student**, enter the student admission number (`SK-2083-XXXX`), and log in.

### 6.2 SMS Broadcast Message to Parents
```text
Namaste Parents, Shree Susanskrit Sec School has launched its official Student & Fee Payment App! View terminal report cards & pay fees easily with eSewa. Download APK: https://susanskrit.edu.np/downloads/ssams-student.apk - Principal
```

---

## 7. Versioning & Continuous Upgrades

Follow Semantic Versioning in `mobile/ssams-student/pubspec.yaml`:

```yaml
version: 1.0.1+2
# Format: MAJOR.MINOR.PATCH+BUILD_NUMBER
# For Google Play, increment BUILD_NUMBER on every upload (e.g., +2, +3, +4).
```

Verify build health before release:
```bash
# Analyze code quality
flutter analyze

# Run unit and widget tests
flutter test
```
