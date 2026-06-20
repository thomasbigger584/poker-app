import 'dart:convert';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';

import '../../../../core/logging/app_logger.dart';
import '../../domain/entities/auth_tokens.dart';

/// Persists the OAuth token set in the platform secure store
/// (Keystore/Keychain/libsecret/DPAPI), mirroring the Android
/// `EncryptedSharedPreferences`-backed `AuthStateManager`.
class AuthTokenStore {
  AuthTokenStore([FlutterSecureStorage? storage])
      : _storage = storage ??
            const FlutterSecureStorage(
              iOptions: IOSOptions(accessibility: KeychainAccessibility.first_unlock),
            );

  final FlutterSecureStorage _storage;

  static const _key = 'com.twb.pokerapp.auth.tokens';

  Future<AuthTokens?> read() async {
    try {
      final raw = await _storage.read(key: _key);
      if (raw == null) return null;
      return AuthTokens.fromJson(jsonDecode(raw) as Map<String, dynamic>);
    } catch (e, st) {
      // Corrupt/undecryptable payload — treat as logged out.
      log.w('Failed to read stored tokens', error: e, stackTrace: st);
      await clear();
      return null;
    }
  }

  Future<void> write(AuthTokens tokens) async {
    try {
      await _storage.write(key: _key, value: jsonEncode(tokens.toJson()));
    } catch (e, st) {
      log.e('Failed to persist tokens', error: e, stackTrace: st);
    }
  }

  Future<void> clear() async {
    try {
      await _storage.delete(key: _key);
    } catch (e, st) {
      log.w('Failed to clear tokens', error: e, stackTrace: st);
    }
  }
}
