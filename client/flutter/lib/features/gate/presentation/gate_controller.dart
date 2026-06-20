import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/logging/app_logger.dart';
import '../data/connectivity_service.dart';
import '../data/tailscale_service.dart';
import 'gate_providers.dart';
import 'gate_state.dart';

/// Orchestrates the startup pre-condition checks (permissions → internet →
/// Tailscale) and re-evaluates them on connectivity changes / app resume,
/// mirroring the Android base-activity lifecycle gating.
class GateController extends Notifier<GateState> {
  late final ConnectivityService _connectivity;
  late final TailscaleService _tailscale;
  bool _running = false;

  @override
  GateState build() {
    _connectivity = ref.read(connectivityServiceProvider);
    _tailscale = ref.read(tailscaleServiceProvider);

    final sub = _connectivity.onChanged.listen((_) => recheck());
    ref.onDispose(sub.cancel);

    // Request notification permission once, then run the first evaluation.
    Future.microtask(() async {
      await ref.read(permissionServiceProvider).requestStartupPermissions();
      await recheck();
    });

    return const GateState(checking: true);
  }

  /// Re-evaluates the gate. Internet is checked first; only if it passes do we
  /// probe Tailscale (same precedence as Android).
  Future<void> recheck() async {
    if (_running) return;
    _running = true;
    try {
      final hasInternet = await _connectivity.hasInternet();
      if (!hasInternet) {
        state = const GateState(checking: false, blocker: GateBlocker.offline);
        return;
      }
      final tailscaleOk = await _tailscale.isConnected();
      if (!tailscaleOk) {
        state = const GateState(checking: false, blocker: GateBlocker.tailscale);
        return;
      }
      state = const GateState(checking: false);
    } catch (e, st) {
      log.w('Gate recheck failed', error: e, stackTrace: st);
      state = const GateState(checking: false);
    } finally {
      _running = false;
    }
  }
}
