import 'package:flutter/foundation.dart';
import 'package:jwt_decoder/jwt_decoder.dart';

/// The signed-in user's identity, derived from the OIDC id/access token claims.
@immutable
class UserProfile {
  const UserProfile({
    required this.subject,
    required this.username,
    this.email,
    this.displayName,
  });

  final String subject;
  final String username;
  final String? email;
  final String? displayName;

  String get initials {
    final source = (displayName?.trim().isNotEmpty ?? false)
        ? displayName!.trim()
        : username;
    if (source.isEmpty) return '?';
    final parts = source.split(RegExp(r'\s+')).where((p) => p.isNotEmpty).toList();
    if (parts.isEmpty) return '?';
    String firstChar(String s) => s.substring(0, 1).toUpperCase();
    if (parts.length == 1) return firstChar(parts.first);
    return firstChar(parts.first) + firstChar(parts.last);
  }

  /// Parses the standard Keycloak claims out of a JWT (id token preferred,
  /// access token as fallback).
  factory UserProfile.fromJwt(String jwt) {
    final claims = JwtDecoder.decode(jwt);
    return UserProfile(
      subject: claims['sub'] as String? ?? '',
      username: claims['preferred_username'] as String? ??
          claims['email'] as String? ??
          'player',
      email: claims['email'] as String?,
      displayName: claims['name'] as String? ??
          claims['given_name'] as String? ??
          claims['preferred_username'] as String?,
    );
  }
}
