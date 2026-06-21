import 'package:flutter/foundation.dart';

/// Subset of the OpenID Connect discovery document
/// (`/.well-known/openid-configuration`) that we use.
@immutable
class OidcConfiguration {
  const OidcConfiguration({
    required this.issuer,
    required this.authorizationEndpoint,
    required this.tokenEndpoint,
    required this.endSessionEndpoint,
    required this.userInfoEndpoint,
  });

  final String issuer;
  final String authorizationEndpoint;
  final String tokenEndpoint;
  final String? endSessionEndpoint;
  final String? userInfoEndpoint;

  factory OidcConfiguration.fromJson(Map<String, dynamic> json) {
    return OidcConfiguration(
      issuer: json['issuer'] as String,
      authorizationEndpoint: json['authorization_endpoint'] as String,
      tokenEndpoint: json['token_endpoint'] as String,
      endSessionEndpoint: json['end_session_endpoint'] as String?,
      userInfoEndpoint: json['userinfo_endpoint'] as String?,
    );
  }
}
