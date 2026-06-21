import 'package:flutter/material.dart';

import '../theme/app_colors.dart';

/// Radial felt-green gradient used as the backdrop for the auth/lobby screens.
class FeltBackground extends StatelessWidget {
  const FeltBackground({super.key, required this.child});

  final Widget child;

  @override
  Widget build(BuildContext context) {
    return DecoratedBox(
      decoration: const BoxDecoration(
        gradient: RadialGradient(
          center: Alignment(0, -0.35),
          radius: 1.25,
          colors: [AppColors.feltLight, AppColors.felt, AppColors.feltDark],
          stops: [0.0, 0.55, 1.0],
        ),
      ),
      child: child,
    );
  }
}
