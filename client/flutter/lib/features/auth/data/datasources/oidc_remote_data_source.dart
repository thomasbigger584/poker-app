import 'dart:convert';
import 'dart:math';

import 'package:crypto/crypto.dart';
import 'package:dio/dio.dart';
import 'package:flutter/services.dart';
import 'package:flutter_web_auth_2/flutter_web_auth_2.dart';

import '../../../../core/config/app_config.dart';
import '../../../../core/logging/app_logger.dart';
import '../../../../core/platform/platform_info.dart';
import '../../domain/entities/auth_tokens.dart';
import '../models/oidc_configuration.dart';

/// Raised when the user aborts the browser login (closed tab / pressed back).
class AuthCancelledException implements Exception {}

/// Per-platform redirect configuration for the Authorization Code flow.
class _Redirect {
  const _Redirect({
    required this.redirectUri,
    required this.callbackUrlScheme,
  });

  /// Exact `redirect_uri` sent to Keycloak (must be a registered URI).
  final String redirectUri;

  /// Scheme/host flutter_web_auth_2 listens on to capture the callback.
  /// On desktop (Windows/Linux) this is `http://localhost:<port>` and the
  /// plugin runs a loopback server + the system browser.
  final String callbackUrlScheme;
}

/// Talks to Keycloak's OIDC endpoints and drives the system browser via
/// `flutter_web_auth_2`. Stateless apart from a cached discovery document.
class OidcRemoteDataSource {
  OidcRemoteDataSource(this._dio);

  final Dio _dio;
  OidcConfiguration? _cachedConfig;

  Future<OidcConfiguration> _discover() async {
    if (_cachedConfig != null) return _cachedConfig!;
    final res = await _dio.get<Map<String, dynamic>>(AppConfig.discoveryUrl);
    final config = OidcConfiguration.fromJson(res.data!);
    _cachedConfig = config;
    return config;
  }

  _Redirect _resolveRedirect() {
    if (PlatformInfo.isWeb) {
      // Redirect back to a static page on the app's own origin (see web/auth.html).
      // Resolve against the document base so it works whether the app is served
      // from the origin root or a sub-path (e.g. /app/ behind nginx).
      return _Redirect(
        redirectUri: Uri.base.resolve('auth.html').toString(),
        callbackUrlScheme: Uri.base.scheme,
      );
    }
    if (PlatformInfo.isDesktopLoopback) {
      final scheme = 'http://localhost:${AppConfig.desktopRedirectPort}';
      return _Redirect(
        redirectUri: '$scheme/oauth2redirect',
        callbackUrlScheme: scheme,
      );
    }
    // Android / iOS / macOS — custom URI scheme.
    return _Redirect(
      redirectUri: '${AppConfig.mobileRedirectScheme}:/oauth2redirect',
      callbackUrlScheme: AppConfig.mobileRedirectScheme,
    );
  }

  /// Runs the interactive Authorization Code + PKCE login. Returns the token set.
  Future<AuthTokens> login() async {
    final config = await _discover();
    final redirect = _resolveRedirect();

    final verifier = _generateCodeVerifier();
    final challenge = _codeChallenge(verifier);
    final state = _randomString(24);

    final authUrl = Uri.parse(config.authorizationEndpoint).replace(
      queryParameters: {
        'client_id': AppConfig.clientId,
        'redirect_uri': redirect.redirectUri,
        'response_type': 'code',
        'scope': AppConfig.scopes,
        'code_challenge': challenge,
        'code_challenge_method': 'S256',
        'state': state,
      },
    ).toString();

    log.d('Starting OIDC login → ${config.authorizationEndpoint}');

    final String resultUrl;
    try {
      resultUrl = await FlutterWebAuth2.authenticate(
        url: authUrl,
        callbackUrlScheme: redirect.callbackUrlScheme,
        options: const FlutterWebAuth2Options(
          preferEphemeral: false,
          timeout: 300,
          // Linux/Windows only: use the loopback HttpServer + system browser
          // (the v3 behaviour this code is built around — callbackUrlScheme is
          // `http://localhost:<port>`) instead of the in-app webview. Keeps the
          // registered Keycloak redirect URIs valid. No effect on mobile/web/macOS.
          useWebview: false,
        ),
      );
    } on PlatformException catch (e) {
      if (e.code == 'CANCELED' || e.code.toLowerCase().contains('cancel')) {
        throw AuthCancelledException();
      }
      rethrow;
    }

    final callback = Uri.parse(resultUrl);
    final returnedState = callback.queryParameters['state'];
    final code = callback.queryParameters['code'];
    final error = callback.queryParameters['error'];

    if (error != null) {
      throw Exception('Authorization error: $error '
          '${callback.queryParameters['error_description'] ?? ''}');
    }
    if (returnedState != state) {
      throw Exception('OAuth state mismatch — possible CSRF, aborting.');
    }
    if (code == null) {
      throw Exception('No authorization code returned.');
    }

    return _exchangeCode(
      config: config,
      code: code,
      codeVerifier: verifier,
      redirectUri: redirect.redirectUri,
    );
  }

  Future<AuthTokens> _exchangeCode({
    required OidcConfiguration config,
    required String code,
    required String codeVerifier,
    required String redirectUri,
  }) async {
    final res = await _dio.post<Map<String, dynamic>>(
      config.tokenEndpoint,
      data: {
        'grant_type': 'authorization_code',
        'client_id': AppConfig.clientId,
        'code': code,
        'redirect_uri': redirectUri,
        'code_verifier': codeVerifier,
      },
      options: Options(contentType: Headers.formUrlEncodedContentType),
    );
    return AuthTokens.fromTokenResponse(res.data!);
  }

  /// Exchanges a refresh token for a fresh token set.
  Future<AuthTokens> refresh(String refreshToken) async {
    final config = await _discover();
    final res = await _dio.post<Map<String, dynamic>>(
      config.tokenEndpoint,
      data: {
        'grant_type': 'refresh_token',
        'client_id': AppConfig.clientId,
        'refresh_token': refreshToken,
      },
      options: Options(contentType: Headers.formUrlEncodedContentType),
    );
    return AuthTokens.fromTokenResponse(res.data!);
  }

  /// Best-effort Keycloak SSO logout via the refresh token.
  Future<void> endSession(AuthTokens tokens) async {
    final config = await _discover();
    final endpoint = config.endSessionEndpoint;
    if (endpoint == null) return;
    try {
      await _dio.post<void>(
        endpoint,
        data: {
          'client_id': AppConfig.clientId,
          'refresh_token': tokens.refreshToken,
        },
        options: Options(contentType: Headers.formUrlEncodedContentType),
      );
    } catch (e) {
      log.w('end-session request failed (ignored): $e');
    }
  }

  // ----- PKCE helpers -------------------------------------------------------

  static const _verifierChars =
      'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~';

  String _generateCodeVerifier() => _randomString(64);

  String _randomString(int length) {
    final rng = Random.secure();
    return List.generate(
      length,
      (_) => _verifierChars[rng.nextInt(_verifierChars.length)],
    ).join();
  }

  String _codeChallenge(String verifier) {
    final digest = sha256.convert(ascii.encode(verifier));
    return base64UrlEncode(digest.bytes).replaceAll('=', '');
  }
}
