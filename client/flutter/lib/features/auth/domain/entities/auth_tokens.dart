import 'package:flutter/foundation.dart';

/// OAuth2/OIDC token set returned by Keycloak's token endpoint.
@immutable
class AuthTokens {
  const AuthTokens({
    required this.accessToken,
    required this.refreshToken,
    required this.idToken,
    required this.accessTokenExpiry,
    this.refreshTokenExpiry,
  });

  final String accessToken;
  final String refreshToken;
  final String? idToken;

  /// Absolute expiry of the access token (UTC).
  final DateTime accessTokenExpiry;

  /// Absolute expiry of the refresh token, if known (UTC).
  final DateTime? refreshTokenExpiry;

  /// Refresh slightly early, mirroring the Android 10s leeway.
  static const Duration _leeway = Duration(seconds: 15);

  bool get isAccessTokenExpired =>
      DateTime.now().toUtc().isAfter(accessTokenExpiry.subtract(_leeway));

  bool get isRefreshTokenExpired {
    final expiry = refreshTokenExpiry;
    if (expiry == null) return false;
    return DateTime.now().toUtc().isAfter(expiry.subtract(_leeway));
  }

  AuthTokens copyWith({
    String? accessToken,
    String? refreshToken,
    String? idToken,
    DateTime? accessTokenExpiry,
    DateTime? refreshTokenExpiry,
  }) {
    return AuthTokens(
      accessToken: accessToken ?? this.accessToken,
      refreshToken: refreshToken ?? this.refreshToken,
      idToken: idToken ?? this.idToken,
      accessTokenExpiry: accessTokenExpiry ?? this.accessTokenExpiry,
      refreshTokenExpiry: refreshTokenExpiry ?? this.refreshTokenExpiry,
    );
  }

  Map<String, dynamic> toJson() => {
        'accessToken': accessToken,
        'refreshToken': refreshToken,
        'idToken': idToken,
        'accessTokenExpiry': accessTokenExpiry.toIso8601String(),
        'refreshTokenExpiry': refreshTokenExpiry?.toIso8601String(),
      };

  factory AuthTokens.fromJson(Map<String, dynamic> json) => AuthTokens(
        accessToken: json['accessToken'] as String,
        refreshToken: json['refreshToken'] as String,
        idToken: json['idToken'] as String?,
        accessTokenExpiry: DateTime.parse(json['accessTokenExpiry'] as String),
        refreshTokenExpiry: json['refreshTokenExpiry'] == null
            ? null
            : DateTime.parse(json['refreshTokenExpiry'] as String),
      );

  /// Builds a token set from a raw Keycloak token-endpoint response.
  factory AuthTokens.fromTokenResponse(Map<String, dynamic> json) {
    final now = DateTime.now().toUtc();
    final expiresIn = (json['expires_in'] as num?)?.toInt() ?? 300;
    final refreshExpiresIn = (json['refresh_expires_in'] as num?)?.toInt();
    return AuthTokens(
      accessToken: json['access_token'] as String,
      refreshToken: json['refresh_token'] as String? ?? '',
      idToken: json['id_token'] as String?,
      accessTokenExpiry: now.add(Duration(seconds: expiresIn)),
      refreshTokenExpiry: (refreshExpiresIn == null || refreshExpiresIn == 0)
          ? null
          : now.add(Duration(seconds: refreshExpiresIn)),
    );
  }
}
