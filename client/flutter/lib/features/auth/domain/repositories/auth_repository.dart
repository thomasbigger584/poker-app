import '../../../../core/error/result.dart';
import '../entities/user_profile.dart';

/// Contract for everything auth-related. Implemented in the data layer; the
/// presentation layer only ever depends on this abstraction.
abstract interface class AuthRepository {
  /// Restores a persisted session on app start, refreshing the access token if
  /// needed. Returns the user on success, or an [Err] when no valid session
  /// exists (the user must log in).
  Future<Result<UserProfile>> restoreSession();

  /// Runs the full interactive OIDC Authorization Code + PKCE login flow.
  Future<Result<UserProfile>> login();

  /// Clears the local session and best-effort ends the Keycloak SSO session.
  Future<void> logout();

  /// Returns a currently-valid access token (refreshing if necessary) for use
  /// as a bearer token on REST/WebSocket calls, or null if unauthenticated.
  Future<String?> currentAccessToken();
}
