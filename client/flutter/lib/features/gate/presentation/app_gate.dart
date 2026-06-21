import 'package:app_settings/app_settings.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/config/app_config.dart';
import '../../../core/platform/platform_info.dart';
import '../../../core/theme/app_colors.dart';
import '../../../core/widgets/alert_modal_dialog.dart';
import 'gate_providers.dart';
import 'gate_state.dart';

/// Wraps the whole app and overlays a blocking, non-dismissible dialog whenever
/// a pre-condition fails (no internet / not on Tailscale). Re-evaluates on app
/// resume. Flutter port of the Android base-activity gate.
class AppGate extends ConsumerStatefulWidget {
  const AppGate({super.key, required this.child});

  final Widget child;

  @override
  ConsumerState<AppGate> createState() => _AppGateState();
}

class _AppGateState extends ConsumerState<AppGate> with WidgetsBindingObserver {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      ref.read(gateControllerProvider.notifier).recheck();
    }
  }

  @override
  Widget build(BuildContext context) {
    final gate = ref.watch(gateControllerProvider);
    return Stack(
      children: [
        widget.child,
        if (gate.isBlocked)
          Positioned.fill(
            child: _GateOverlay(blocker: gate.blocker!),
          ),
      ],
    );
  }
}

class _GateOverlay extends ConsumerWidget {
  const _GateOverlay({required this.blocker});

  final GateBlocker blocker;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final (title, message, primaryLabel, onPrimary) = switch (blocker) {
      GateBlocker.offline => (
          'No internet connection',
          'You are not connected to the internet. Please check your network settings and try again.',
          PlatformInfo.isMobile ? 'Open settings' : 'Retry',
          () async {
            if (PlatformInfo.isMobile) {
              await AppSettings.openAppSettings(type: AppSettingsType.wifi);
            } else {
              await ref.read(gateControllerProvider.notifier).recheck();
            }
          },
        ),
      GateBlocker.tailscale => (
          'Tailscale required',
          'You are not connected to the Tailscale VPN or the appropriate network. '
              'Tap Connect to open or install Tailscale, then make sure this device '
              'can reach ${AppConfig.backendHost}.',
          'Connect',
          () async {
            await ref.read(tailscaleLauncherProvider).open();
          },
        ),
    };

    return Material(
      type: MaterialType.transparency,
      child: Stack(
        children: [
          const ModalBarrier(dismissible: false, color: Colors.black87),
          Center(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(24),
              child: ConstrainedBox(
                constraints: const BoxConstraints(maxWidth: 420),
                child: _AlertCard(
                  type: AlertModalType.warning,
                  title: title,
                  message: message,
                  primaryLabel: primaryLabel,
                  onPrimary: onPrimary,
                  onSecondary: () =>
                      ref.read(gateControllerProvider.notifier).recheck(),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _AlertCard extends StatelessWidget {
  const _AlertCard({
    required this.type,
    required this.title,
    required this.message,
    required this.primaryLabel,
    required this.onPrimary,
    required this.onSecondary,
  });

  final AlertModalType type;
  final String title;
  final String message;
  final String primaryLabel;
  final Future<void> Function() onPrimary;
  final VoidCallback onSecondary;

  @override
  Widget build(BuildContext context) {
    return Card(
      color: AppColors.surfaceHigh,
      child: Padding(
        padding: const EdgeInsets.fromLTRB(28, 32, 28, 24),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: 68,
              height: 68,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: type.color.withValues(alpha: 0.15),
              ),
              child: Icon(type.icon, color: type.color, size: 36),
            ),
            const SizedBox(height: 20),
            Text(
              title,
              textAlign: TextAlign.center,
              style: const TextStyle(
                color: AppColors.textPrimary,
                fontSize: 21,
                fontWeight: FontWeight.w700,
              ),
            ),
            const SizedBox(height: 12),
            Text(
              message,
              textAlign: TextAlign.center,
              style: const TextStyle(
                color: AppColors.textSecondary,
                fontSize: 15,
                height: 1.45,
              ),
            ),
            const SizedBox(height: 28),
            FilledButton(
              style: FilledButton.styleFrom(
                backgroundColor: type.color,
                foregroundColor: AppColors.feltDark,
              ),
              onPressed: () => onPrimary(),
              child: Text(primaryLabel),
            ),
            const SizedBox(height: 10),
            TextButton(
              onPressed: onSecondary,
              child: const Text('Retry'),
            ),
          ],
        ),
      ),
    );
  }
}
