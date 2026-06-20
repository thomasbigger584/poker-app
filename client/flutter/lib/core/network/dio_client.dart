import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';

import '../config/app_config.dart';
import 'auth_interceptor.dart';

/// Builds configured [Dio] instances.
abstract final class DioClient {
  static BaseOptions _baseOptions({String? baseUrl}) => BaseOptions(
        baseUrl: baseUrl ?? '',
        connectTimeout: const Duration(seconds: 15),
        receiveTimeout: const Duration(seconds: 20),
        sendTimeout: const Duration(seconds: 15),
        headers: const {'Accept': 'application/json'},
        // Don't throw on non-2xx for the OAuth flow — we inspect bodies/codes.
        validateStatus: (code) => code != null && code < 500,
      );

  /// Unauthenticated client used for OIDC discovery + token exchange.
  static Dio plain() {
    final dio = Dio(_baseOptions());
    if (kDebugMode) {
      dio.interceptors.add(LogInterceptor(requestBody: false, responseBody: false));
    }
    return dio;
  }

  /// Authenticated client for REST API calls — attaches the bearer token and
  /// refreshes on 401. (Used by feature repositories such as the table list.)
  static Dio authenticated({
    required TokenProvider tokenProvider,
    required Future<void> Function() onUnauthorized,
  }) {
    final dio = Dio(_baseOptions(baseUrl: AppConfig.apiBaseUrl));
    dio.interceptors.add(AuthInterceptor(
      tokenProvider: tokenProvider,
      onUnauthorized: onUnauthorized,
    ));
    if (kDebugMode) {
      dio.interceptors.add(LogInterceptor(requestBody: false, responseBody: false));
    }
    return dio;
  }
}
