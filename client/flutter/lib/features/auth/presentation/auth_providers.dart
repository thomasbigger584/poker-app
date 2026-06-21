import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/network/dio_client.dart';
import '../data/datasources/auth_token_store.dart';
import '../data/datasources/oidc_remote_data_source.dart';
import '../data/repositories/auth_repository_impl.dart';
import '../domain/entities/auth_state.dart';
import '../domain/repositories/auth_repository.dart';
import 'auth_controller.dart';

/// Unauthenticated client for OIDC discovery + token exchange.
final _plainDioProvider = Provider<Dio>((ref) => DioClient.plain());

final authTokenStoreProvider = Provider<AuthTokenStore>((ref) => AuthTokenStore());

final _oidcRemoteDataSourceProvider = Provider<OidcRemoteDataSource>(
  (ref) => OidcRemoteDataSource(ref.read(_plainDioProvider)),
);

final authRepositoryProvider = Provider<AuthRepository>(
  (ref) => AuthRepositoryImpl(
    ref.read(_oidcRemoteDataSourceProvider),
    ref.read(authTokenStoreProvider),
  ),
);

final authControllerProvider =
    NotifierProvider<AuthController, AuthState>(AuthController.new);

/// Authenticated REST client for feature repositories (e.g. table list). Logs
/// the user out when the backend rejects the token (401).
final authedDioProvider = Provider<Dio>((ref) {
  final repo = ref.read(authRepositoryProvider);
  return DioClient.authenticated(
    tokenProvider: repo.currentAccessToken,
    onUnauthorized: () =>
        ref.read(authControllerProvider.notifier).onUnauthorized(),
  );
});
