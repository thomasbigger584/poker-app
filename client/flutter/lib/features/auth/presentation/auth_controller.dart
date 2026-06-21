import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/error/result.dart';
import '../domain/entities/auth_state.dart';
import '../domain/entities/user_profile.dart';
import '../domain/repositories/auth_repository.dart';
import 'auth_providers.dart';

/// Owns the global [AuthState] that drives routing. Thin orchestration over the
/// [AuthRepository]; all OAuth mechanics live in the data layer.
class AuthController extends Notifier<AuthState> {
  late final AuthRepository _repo;

  @override
  AuthState build() {
    _repo = ref.read(authRepositoryProvider);
    Future.microtask(_restore);
    return const AuthInitializing();
  }

  Future<void> _restore() async {
    final result = await _repo.restoreSession();
    state = result.fold(
      (user) => Authenticated(user),
      (_) => const Unauthenticated(),
    );
  }

  /// Runs the interactive login. Returns the [Result] so the UI can surface
  /// failures; on success the state flips to [Authenticated] and routing reacts.
  Future<Result<UserProfile>> login() async {
    final result = await _repo.login();
    result.fold(
      (user) => state = Authenticated(user),
      (failure) => state = Unauthenticated(failure),
    );
    return result;
  }

  Future<void> logout() async {
    await _repo.logout();
    state = const Unauthenticated();
  }

  /// Called when the API returns 401 — the session is no longer valid.
  Future<void> onUnauthorized() async {
    await _repo.logout();
    state = const Unauthenticated();
  }
}
