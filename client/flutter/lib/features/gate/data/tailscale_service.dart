import 'package:dio/dio.dart';

import '../../../core/config/app_config.dart';
import '../../../core/logging/app_logger.dart';

/// Determines whether the Tailscale-hosted backend is reachable — the Flutter
/// equivalent of the Android `TailscaleController.isTailscaleConnected()`.
///
/// The native client also inspects `NetworkCapabilities.TRANSPORT_VPN`, but a
/// reachability probe to the `.ts.net` host is the meaningful, cross-platform
/// signal: if we can reach the host, we are on the tailnet.
class TailscaleService {
  TailscaleService([Dio? dio])
      : _dio = dio ??
            Dio(BaseOptions(
              connectTimeout: const Duration(seconds: 6),
              receiveTimeout: const Duration(seconds: 6),
              // Any HTTP response (even 401/404) proves reachability.
              validateStatus: (_) => true,
            ));

  final Dio _dio;

  /// Only required when the backend lives on a `.ts.net` tailnet.
  bool get isRequired => AppConfig.isTailscaleRequired;

  Future<bool> isConnected() async {
    if (!isRequired) return true;
    final probeUrl = '${AppConfig.useHttps ? 'https' : 'http'}://${AppConfig.apiHost}/';
    try {
      await _dio.head<void>(probeUrl);
      return true;
    } on DioException catch (e) {
      // A response with a status code still means we reached the host.
      if (e.response != null) return true;
      log.d('Tailscale probe failed: ${e.type}');
      return false;
    } catch (e) {
      log.d('Tailscale probe error: $e');
      return false;
    }
  }
}
