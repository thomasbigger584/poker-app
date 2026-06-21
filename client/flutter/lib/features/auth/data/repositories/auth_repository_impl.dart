import 'package:dio/dio.dart';

import '../../../../core/error/failure.dart';
import '../../../../core/error/result.dart';
import '../../../../core/logging/app_logger.dart';
import '../../domain/entities/auth_tokens.dart';
import '../../domain/entities/user_profile.dart';
import '../../domain/repositories/auth_repository.dart';
import '../datasources/auth_token_store.dart';
import '../datasources/oidc_remote_data_source.dart';

class AuthRepositoryImpl implements AuthRepository {
  AuthRepositoryImpl(this._remote, this._store);

  final OidcRemoteDataSource _remote;
  final AuthTokenStore _store;

  UserProfile _profileFrom(AuthTokens tokens) =>
      UserProfile.fromJwt(tokens.idToken ?? tokens.accessToken);

  @override
  Future<Result<UserProfile>> restoreSession() async {
    final tokens = await _store.read();
    if (tokens == null) {
      return const Err(AuthFailure('No saved session'));
    }
    if (!tokens.isAccessTokenExpired) {
      return Ok(_profileFrom(tokens));
    }
    // Access token expired — try to refresh silently.
    if (tokens.refreshToken.isEmpty || tokens.isRefreshTokenExpired) {
      await _store.clear();
      return const Err(AuthFailure('Session expired'));
    }
    try {
      final refreshed = await _remote.refresh(tokens.refreshToken);
      await _store.write(refreshed);
      return Ok(_profileFrom(refreshed));
    } catch (e, st) {
      log.w('Silent refresh failed', error: e, stackTrace: st);
      await _store.clear();
      return const Err(AuthFailure('Could not restore session'));
    }
  }

  @override
  Future<Result<UserProfile>> login() async {
    try {
      final tokens = await _remote.login();
      await _store.write(tokens);
      return Ok(_profileFrom(tokens));
    } on AuthCancelledException {
      return const Err(CancelledFailure('Login cancelled'));
    } on DioException catch (e, st) {
      log.e('Login network error', error: e, stackTrace: st);
      return Err(_mapDioError(e));
    } catch (e, st) {
      log.e('Login failed', error: e, stackTrace: st);
      return Err(AuthFailure('Login failed. Please try again.', e));
    }
  }

  @override
  Future<void> logout() async {
    final tokens = await _store.read();
    if (tokens != null) {
      await _remote.endSession(tokens);
    }
    await _store.clear();
  }

  @override
  Future<String?> currentAccessToken() async {
    final tokens = await _store.read();
    if (tokens == null) return null;
    if (!tokens.isAccessTokenExpired) return tokens.accessToken;
    if (tokens.refreshToken.isEmpty || tokens.isRefreshTokenExpired) {
      await _store.clear();
      return null;
    }
    try {
      final refreshed = await _remote.refresh(tokens.refreshToken);
      await _store.write(refreshed);
      return refreshed.accessToken;
    } catch (_) {
      await _store.clear();
      return null;
    }
  }

  Failure _mapDioError(DioException e) {
    return switch (e.type) {
      DioExceptionType.connectionTimeout ||
      DioExceptionType.receiveTimeout ||
      DioExceptionType.sendTimeout ||
      DioExceptionType.connectionError =>
        const NetworkFailure('Could not reach the server'),
      _ => AuthFailure('Login failed (${e.response?.statusCode ?? 'network'})', e),
    };
  }
}
