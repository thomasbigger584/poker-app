import 'package:flutter/foundation.dart';

/// Central, immutable application configuration.
///
/// Mirrors the native Android client's `res/raw/auth_config.json` and
/// `BuildConfig` fields so both clients talk to the exact same backend.
///
/// Every value can be overridden at build/run time with `--dart-define`, e.g.
/// `flutter run --dart-define=API_HOST=localhost:8080 --dart-define=HTTPS=false`.
/// This keeps the binary identical across environments while letting us point
/// at a local Keycloak/API during development.
@immutable
class AppConfig {
  const AppConfig._();

  /// Backend host (REST API, WebSocket and Keycloak all live behind this host,
  /// reverse-proxied by nginx — see `server/nginx`). Tailscale MagicDNS name by
  /// default, matching the Android `API_BASE_URL`.
  static const String apiHost =
      String.fromEnvironment('API_HOST', defaultValue: 'poker-app.dinosaur-emperor.ts.net');

  /// Whether to use TLS. The real deployment is HTTPS-only; flip to false to
  /// hit a plain-HTTP local stack.
  static const bool useHttps =
      bool.fromEnvironment('HTTPS', defaultValue: true);

  /// Keycloak realm.
  static const String realm =
      String.fromEnvironment('KEYCLOAK_REALM', defaultValue: 'poker-app');

  /// Public OAuth2 client id (no secret — Authorization Code + PKCE).
  static const String clientId = String.fromEnvironment(
    'KEYCLOAK_CLIENT_ID',
    defaultValue: 'poker-game-android-client',
  );

  /// OIDC scopes requested at login.
  static const String scopes = String.fromEnvironment(
    'KEYCLOAK_SCOPES',
    defaultValue: 'openid email profile',
  );

  /// Keycloak is served under the `/auth` path prefix by nginx.
  static const String keycloakBasePath = '/auth';

  /// Fixed loopback port used for the OAuth redirect on desktop (Windows/Linux),
  /// where `flutter_web_auth_2` runs a local server. Must be registered as a
  /// redirect URI in Keycloak: `http://localhost:$desktopRedirectPort/oauth2redirect`.
  static const int desktopRedirectPort = 8484;

  /// Custom URI scheme registered for native mobile/macOS redirects.
  /// Mirrors Android's `com.twb.pokerapp:/oauth2redirect`.
  static const String mobileRedirectScheme = 'com.twb.pokerapp';

  // ----- Derived URLs -------------------------------------------------------

  static String get _scheme => useHttps ? 'https' : 'http';

  static String get apiBaseUrl => '$_scheme://$apiHost/api';

  static String get keycloakBaseUrl => '$_scheme://$apiHost$keycloakBasePath';

  static String get realmUrl => '$keycloakBaseUrl/realms/$realm';

  static String get discoveryUrl =>
      '$realmUrl/.well-known/openid-configuration';

  /// True when the backend lives on a Tailscale tailnet and therefore requires
  /// the VPN to be up (drives the Tailscale gate). Matches the Android
  /// `TailscaleController.isTailscaleCheckRequired()` (`host endsWith .ts.net`).
  static bool get isTailscaleRequired => apiHost.endsWith('.ts.net');

  /// Host shown to the user in the Tailscale warning dialog.
  static String get backendHost => apiHost;
}
