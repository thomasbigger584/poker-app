import 'package:dio/dio.dart';

/// Supplies a currently-valid access token (refreshing if necessary).
typedef TokenProvider = Future<String?> Function();

/// Attaches the OAuth bearer token to outgoing requests and notifies the app
/// when the server rejects it (401), mirroring the Android `AuthInterceptor` +
/// `TokenAuthenticator` pair.
class AuthInterceptor extends Interceptor {
  AuthInterceptor({
    required this.tokenProvider,
    required this.onUnauthorized,
  });

  final TokenProvider tokenProvider;
  final Future<void> Function() onUnauthorized;

  @override
  Future<void> onRequest(
    RequestOptions options,
    RequestInterceptorHandler handler,
  ) async {
    final token = await tokenProvider();
    if (token != null && token.isNotEmpty) {
      options.headers['Authorization'] = 'Bearer $token';
    }
    handler.next(options);
  }

  @override
  Future<void> onError(
    DioException err,
    ErrorInterceptorHandler handler,
  ) async {
    if (err.response?.statusCode == 401) {
      await onUnauthorized();
    }
    handler.next(err);
  }
}
