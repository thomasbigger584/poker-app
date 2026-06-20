import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'core/router/app_router.dart';
import 'core/theme/app_theme.dart';
import 'features/gate/presentation/app_gate.dart';

/// Root widget: themed [MaterialApp.router] with the connectivity/Tailscale
/// [AppGate] overlaid on every screen.
class PokerApp extends ConsumerWidget {
  const PokerApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final router = ref.watch(routerProvider);
    return MaterialApp.router(
      title: 'TWB Poker',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.dark,
      themeMode: ThemeMode.dark,
      routerConfig: router,
      builder: (context, child) =>
          AppGate(child: child ?? const SizedBox.shrink()),
    );
  }
}
