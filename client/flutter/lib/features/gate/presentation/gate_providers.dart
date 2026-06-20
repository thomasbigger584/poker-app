import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../data/connectivity_service.dart';
import '../data/permission_service.dart';
import '../data/tailscale_launcher.dart';
import '../data/tailscale_service.dart';
import 'gate_controller.dart';
import 'gate_state.dart';

final connectivityServiceProvider =
    Provider<ConnectivityService>((ref) => ConnectivityService());

final tailscaleServiceProvider =
    Provider<TailscaleService>((ref) => TailscaleService());

final permissionServiceProvider =
    Provider<PermissionService>((ref) => PermissionService());

final tailscaleLauncherProvider =
    Provider<TailscaleLauncher>((ref) => TailscaleLauncher());

final gateControllerProvider =
    NotifierProvider<GateController, GateState>(GateController.new);
