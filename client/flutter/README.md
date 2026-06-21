# TWB Poker — Flutter client

A cross-platform (Android · iOS · Windows · macOS · Linux · Web) Flutter client
for the TWB Poker backend. It is a clean re-implementation of the native Android
app's login flow, gating layer and navigation, built with current Flutter best
practices.

> Status: **login + lobby shell**. The live table list, connect/buy-in flow and
> the Texas Hold'em game screen are intentionally not implemented yet.

## Stack

| Concern            | Choice                                             |
| ------------------ | -------------------------------------------------- |
| State management / DI | [Riverpod](https://riverpod.dev) (`flutter_riverpod`) |
| Navigation         | [go_router](https://pub.dev/packages/go_router) with auth-guarded redirects |
| Networking         | [dio](https://pub.dev/packages/dio) (+ bearer/refresh interceptor) |
| Auth               | OAuth2 **Authorization Code + PKCE** against Keycloak, via `flutter_web_auth_2` |
| Secure storage     | `flutter_secure_storage` (Keystore / Keychain / libsecret / DPAPI) |
| Connectivity / gating | `connectivity_plus`, `permission_handler`, `url_launcher`, `android_intent_plus` |

## Architecture

Feature-first **clean architecture** — each feature is split into
`domain` (entities + repository contracts), `data` (data sources + repository
implementations) and `presentation` (Riverpod controllers + pages).

```
lib/
  app.dart                      MaterialApp.router + AppGate overlay
  main.dart                     bootstrap (ProviderScope)
  core/
    config/app_config.dart      all backend/Keycloak config (--dart-define overridable)
    error/                      Failure + Result<T>
    network/                    Dio factory + AuthInterceptor
    platform/                   platform detection (web-safe)
    router/                     go_router + auth redirects
    theme/                      Material 3 felt-green + gold theme
    widgets/                    shared UI (logo, felt background, alert dialog)
  features/
    auth/                       OIDC PKCE login (domain/data/presentation)
    gate/                       internet → Tailscale → permission gating
    tables/                     lobby placeholder + navigation drawer
```

### App flow

`Splash` (restores a saved session) → `Login` (Keycloak) → `Tables` (lobby with
a navigation drawer). Routing is driven declaratively by the global `AuthState`
(`AuthInitializing` / `Authenticated` / `Unauthenticated`).

### Pre-condition gate

`AppGate` wraps every screen and re-checks on resume, mirroring the Android
`BasePermissionsActivity → BaseNetworkActivity → BaseAuthActivity` chain:

1. **Permissions** — requests `POST_NOTIFICATIONS` on Android 13+.
2. **Internet** — blocks with a dialog when offline.
3. **Tailscale** — when the backend host ends in `.ts.net`, it probes
   reachability; if the tailnet isn't up it shows a non-dismissible dialog whose
   **Connect** button opens/installs the Tailscale app (`com.tailscale.ipn` on
   Android, the App Store on iOS, the download page elsewhere).

## Configuration

All config lives in `lib/core/config/app_config.dart` and defaults to the
production backend (`poker-app.dinosaur-emperor.ts.net`, realm `poker-app`,
public client `poker-game-android-client`). Override per run without touching
code:

```sh
# Point at a local Keycloak/API over plain HTTP
flutter run --dart-define=API_HOST=localhost:8080 --dart-define=HTTPS=false
```

## Running

```sh
flutter pub get

flutter run -d linux         # or: windows / macos / chrome
flutter run -d <device-id>   # Android / iOS
```

The default backend is on a **Tailscale** tailnet — you must be connected to the
tailnet (and the server must be up) for login to succeed; otherwise the
Tailscale gate will (correctly) block the app.

### Linux desktop prerequisites

The Linux desktop build links a few native libraries. **`webkit2gtk-4.1` is the
one that trips people up:** `flutter_web_auth_2` pulls in `desktop_webview_window`,
whose CMake needs the `webkit2gtk-4.1` (libsoup **3**) path. If only the older
`webkit2gtk-4.0` (libsoup **2**) is installed, CMake silently falls back to it and
the build fails with:

```
desktop_webview_window/linux/webview_window.cc:292:
  error: no matching function for call to 'g_date_time_get_seconds'
```

`libsecret` is likewise required by `flutter_secure_storage`. Install the dev
packages for your distro:

```sh
# Arch / CachyOS
sudo pacman -S --needed base-devel cmake ninja pkgconf gtk3 webkit2gtk-4.1 libsecret

# Debian / Ubuntu
sudo apt-get install -y clang cmake ninja-build pkg-config liblzma-dev \
  libgtk-3-dev libsecret-1-dev libwebkit2gtk-4.1-dev libsoup-3.0-dev
```

If a build already failed before installing these, run `flutter clean` first so
CMake re-detects `webkit2gtk-4.1` instead of reusing the cached fallback. (CI
installs the equivalent `-dev` packages automatically, so this is local-dev only.)

### Per-platform OAuth redirect

`flutter_web_auth_2` captures the OAuth redirect differently per platform, all
handled in `OidcRemoteDataSource`:

| Platform            | redirect_uri                              |
| ------------------- | ----------------------------------------- |
| Android / iOS / macOS | `com.twb.pokerapp:/oauth2redirect` (custom scheme) |
| Windows / Linux     | `http://localhost:8484/oauth2redirect` (loopback server) |
| Web                 | `<origin>/auth.html` (popup postMessage)  |

### ⚠️ Keycloak realm update required for desktop/web

The native Android client only registered the custom-scheme redirect. To let
desktop and web clients log in, `server/keycloak/poker-app-realm.json`
(`poker-game-android-client`) was updated to also allow the loopback / web
redirect URIs and `"+"` web origins (CORS). **This only takes effect after the
realm is re-imported / the Keycloak deployment is refreshed** (or the URIs are
added manually in the Keycloak admin console).

## Launcher icons

Generated for every platform from `assets/images/app_icon.png`:

```sh
dart run flutter_launcher_icons
```

## Tests

```sh
flutter test
```
