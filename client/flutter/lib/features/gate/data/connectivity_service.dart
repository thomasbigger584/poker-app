import 'package:connectivity_plus/connectivity_plus.dart';

/// Reports device internet connectivity, mirroring the Android
/// `BaseNetworkActivity.isNetworkAvailable()` (transport capability check).
class ConnectivityService {
  ConnectivityService([Connectivity? connectivity])
      : _connectivity = connectivity ?? Connectivity();

  final Connectivity _connectivity;

  bool _hasConnection(List<ConnectivityResult> results) =>
      results.any((r) => r != ConnectivityResult.none);

  Future<bool> hasInternet() async {
    final results = await _connectivity.checkConnectivity();
    return _hasConnection(results);
  }

  /// Emits whenever connectivity changes (true = has a network transport).
  Stream<bool> get onChanged =>
      _connectivity.onConnectivityChanged.map(_hasConnection);
}
