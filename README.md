# RTA CarCare

A mobile-first car maintenance tracker built for the UAE — powered by Kotlin/JS and React. No backend, no login, everything stored locally in the browser.

---

## Features

- **Vehicle profile** — save your car name, model, year, plate number, and emirate
- **Book appointments** — schedule official RTA vehicle tests with a date/time slot picker
- **Build inspection bundles** — create named checklists tied to car zones (Hood, Body, Wheels, Brakes, Lights, Wipers)
- **Run inspections** — go through a bundle item by item, mark pass/fail, and see your car diagram light up green or red in real time
- **History** — full log of past appointments and bundle runs, expandable with cancel/reschedule actions
- **Settings & profile** — set your display name, browse FAQs
- **Arabic / English** — full bilingual support with one-tap language toggle; layout mirrors to RTL automatically
- **Offline-first** — all data lives in `localStorage`, nothing sent to any server

---

## Tech Stack

| Layer | Choice |
|---|---|
| Language | Kotlin 2.0.21 (JS/IR compiler) |
| UI | React 18 via [kotlin-wrappers](https://github.com/JetBrains/kotlin-wrappers) |
| Serialization | kotlinx.serialization JSON |
| Date utilities | kotlinx-datetime |
| Build | Gradle 8.14.2 |
| Bundler | Webpack 5 (via Kotlin/JS plugin) |
| Runtime (local) | GraalVM Java 19 |
| Runtime (CI) | Temurin Java 21 |

---

## Running Locally

### Prerequisites
- Java 17+ (21 recommended)
- Gradle (or use the included `./gradlew` wrapper)

### Start the dev server

```bash
./gradlew browserDevelopmentRun --no-daemon
```

Opens a Webpack dev server on **port 5000** with Hot Module Replacement. The app is accessible at `http://localhost:5000`.

### Production build

```bash
./gradlew browserDistribution --no-daemon
```

Output goes to `build/dist/js/productionExecutable/` — ready to serve as a static site.

---

## Project Layout

```
src/main/kotlin/
├── App.kt                      # Root component, navigation stack, RTL toggle
├── Screen.kt                   # Sealed screen hierarchy + nav tab mapping
├── Models.kt                   # AppData, VehicleInfo, Appointment, Bundle…
├── Translations.kt             # Full EN + AR string tables
├── Components.kt               # Shared UI: svgIcon(), bottomNav, sectionCard, buttons…
├── Storage.kt                  # localStorage save/load via JSON serialization
├── DateUtils.kt                # Date helpers using kotlinx-datetime
└── screens/
    ├── HomeScreen.kt           # Vehicle card, stats, recent activity, setup form
    ├── BookAppointmentScreen.kt
    ├── VehicleTestBookingScreen.kt  # 4-step booking wizard
    ├── BundleScreen.kt         # Build a named inspection bundle
    ├── RunBundleScreen.kt      # Run a bundle — pass/fail each item
    ├── HistoryScreen.kt        # Past appointments + bundle runs
    └── SettingsScreens.kt      # Settings, Profile, Help Center

src/main/resources/
├── index.html                  # App shell
└── styles.css                  # All CSS (variables, components, RTL overrides)

webpack.config.d/
└── devserver.js                # Allows all hosts (required for Replit proxy)
```

---

## Deployment — GitHub Pages

The repo includes a GitHub Actions workflow that builds and deploys automatically on every push to `main`.

**One-time setup:**
1. Go to your repo → **Settings** → **Pages**
2. Set Source to **GitHub Actions**
3. Save — the next push will deploy automatically

The workflow file is at `.github/workflows/main.yml`.

---

## Design

- **Theme** — light background (`#F5F5F5`), white cards, RTA red (`#CC0000`) accent
- **Mobile-first** — max-width 440px, centered on desktop
- **Typography** — Inter, tight letter-spacing, uppercase label system
- **Icons** — inline SVG only, no icon font dependency
- **RTL** — automatic layout mirroring when Arabic is active (`dir="rtl"` on `<html>`)
- **Animations** — card stagger on mount, screen slide-in, button press scale, SVG zone pulse (green/red)

---

## License

MIT
