import 'package:flutter_test/flutter_test.dart';
import 'package:pokerapp/core/error/failure.dart';
import 'package:pokerapp/core/error/result.dart';
import 'package:pokerapp/features/auth/domain/entities/auth_tokens.dart';

void main() {
  group('Result', () {
    test('Ok folds to the value branch', () {
      const Result<int> r = Ok(42);
      expect(r.isOk, isTrue);
      expect(r.fold((v) => v, (_) => -1), 42);
    });

    test('Err folds to the failure branch', () {
      const Result<int> r = Err(NetworkFailure());
      expect(r.isErr, isTrue);
      expect(r.fold((v) => 'ok', (f) => f.message), 'Network error');
    });
  });

  group('AuthTokens', () {
    test('reports an expired access token', () {
      final tokens = AuthTokens(
        accessToken: 'a',
        refreshToken: 'r',
        idToken: null,
        accessTokenExpiry: DateTime.now().toUtc().subtract(const Duration(minutes: 1)),
      );
      expect(tokens.isAccessTokenExpired, isTrue);
    });

    test('reports a valid access token', () {
      final tokens = AuthTokens(
        accessToken: 'a',
        refreshToken: 'r',
        idToken: null,
        accessTokenExpiry: DateTime.now().toUtc().add(const Duration(minutes: 5)),
      );
      expect(tokens.isAccessTokenExpired, isFalse);
    });
  });
}
