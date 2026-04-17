# Personal Android App Store — Project Plan

## Overview

A self-hosted personal Android app store for distributing apps built by the user or Claude.
Hosted on **GitHub** (free, version-controlled, no server required).

---

## Architecture

```
GitHub Repository (app-store)
├── catalog.json          ← app metadata index (served via raw GitHub / GitHub Pages)
├── icons/                ← app icons (PNG)
├── screenshots/          ← optional screenshots
└── PLAN.md

GitHub Releases           ← APK binaries (attached to tagged releases)

Android Client App        ← the "store" app installed on device
```

### Why GitHub?

- **GitHub Releases** can host large binary files (APKs) for free
- **Raw GitHub / GitHub Pages** serves the catalog JSON
- No backend server or compute costs
- Git history gives you a full audit trail of catalog changes
- Easy to automate with scripts

---

## Components

### 1. Catalog (`catalog.json`)

Hosted at a stable URL (e.g., GitHub Pages or raw.githubusercontent.com).
Describes every available app.

```json
{
  "store_version": 1,
  "updated": "2026-04-17",
  "apps": [
    {
      "id": "com.example.myapp",
      "name": "My App",
      "description": "Short description",
      "version_name": "1.0.0",
      "version_code": 1,
      "min_sdk": 26,
      "apk_url": "https://github.com/USER/app-store/releases/download/myapp-v1.0.0/myapp.apk",
      "icon_url": "https://raw.githubusercontent.com/USER/app-store/main/icons/myapp.png",
      "category": "Utilities",
      "changelog": "Initial release",
      "size_bytes": 2048000
    }
  ]
}
```

### 2. Android Store Client App

A native Android app (Kotlin + Jetpack Compose) that:

- Fetches and parses `catalog.json` from the configured URL
- Displays a browsable, searchable list of apps
- Shows app details: description, version, changelog, screenshots
- Downloads APKs and triggers system install via `FileProvider` + `Intent`
- Checks installed version vs catalog version (update badge)
- Stores the catalog URL in settings (so it can be changed)

**Key Android permissions:**
- `INTERNET` — fetch catalog + download APKs
- `REQUEST_INSTALL_PACKAGES` — trigger APK installation
- `READ/WRITE` external storage (Android ≤ 9) or scoped storage

### 3. Catalog Management Script (`scripts/add_app.py`)

A Python CLI to add/update apps in the catalog and create GitHub releases:

```
python add_app.py \
  --apk path/to/app.apk \
  --icon path/to/icon.png \
  --description "My cool app" \
  --category Utilities
```

Automatically:
- Reads APK metadata (package name, version) using `aapt2` or `androguard`
- Uploads APK to a new GitHub Release via the GitHub API
- Updates `catalog.json`
- Commits and pushes changes

---

## Repository Structure

```
app-store/                        ← this repo (GitHub hosted)
├── catalog.json
├── icons/
│   └── *.png
├── screenshots/
│   └── appid/
│       └── *.png
├── scripts/
│   └── add_app.py
├── store-app/                    ← Android client source
│   ├── app/
│   │   └── src/main/
│   │       ├── AndroidManifest.xml
│   │       ├── java/com/personal/store/
│   │       └── res/
│   └── build.gradle.kts
└── PLAN.md
```

---

## Build Phases

### Phase 1 — Catalog & Hosting Setup
- [ ] Create GitHub repo `app-store`
- [ ] Enable GitHub Pages (serve from `main` branch root or `/docs`)
- [ ] Define and commit initial `catalog.json` schema
- [ ] Add first placeholder app entry

### Phase 2 — Android Client MVP
- [ ] New Android project (Kotlin, Compose, min SDK 26)
- [ ] Hard-code catalog URL; fetch + parse JSON (Retrofit or Ktor)
- [ ] App list screen: name, icon, version, install button
- [ ] APK download + install flow (FileProvider)
- [ ] Installed version check (PackageManager)

### Phase 3 — Polish
- [ ] App detail screen (description, changelog, screenshots)
- [ ] Settings screen (editable catalog URL)
- [ ] Update badges on installed apps
- [ ] Pull-to-refresh
- [ ] Search / filter by category

### Phase 4 — Catalog Tooling
- [ ] `add_app.py` script
- [ ] GitHub Actions workflow: auto-update catalog when a Release is published

---

## Device Setup (one-time)

On the Android device:

1. **Settings → Apps → Special app access → Install unknown apps**
   Enable for the store app (or for your file manager/browser during initial install).
2. Install the store APK manually the first time (sideload via USB or download link).
3. After that, the store app handles all future installs.

---

## Key Decisions / Open Questions

| Decision | Default | Notes |
|---|---|---|
| Catalog URL type | GitHub Pages | Stable, free, custom domain possible |
| APK hosting | GitHub Releases | 2 GB per file limit — plenty |
| Android min SDK | 26 (Android 8) | Covers ~95% of active devices |
| HTTP client | Ktor (async) | Lightweight, Kotlin-native |
| Image loading | Coil | Compose-native |
| Auth/private repo | None (public) | Add token in settings if repo goes private |

---

## Tech Stack Summary

| Layer | Choice |
|---|---|
| Store hosting | GitHub Pages + GitHub Releases |
| Catalog format | JSON |
| Android language | Kotlin |
| UI framework | Jetpack Compose |
| Networking | Ktor Client |
| Image loading | Coil |
| Catalog tooling | Python 3 + PyGithub |

