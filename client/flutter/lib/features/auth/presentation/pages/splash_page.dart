import 'package:flutter/material.dart';

import '../../../../core/theme/app_colors.dart';
import '../../../../core/widgets/app_logo.dart';
import '../../../../core/widgets/felt_background.dart';

/// Shown while the persisted session is being restored on launch.
class SplashPage extends StatelessWidget {
  const SplashPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: FeltBackground(
        child: Center(
          child: TweenAnimationBuilder<double>(
            duration: const Duration(milliseconds: 700),
            curve: Curves.easeOutCubic,
            tween: Tween(begin: 0, end: 1),
            builder: (context, t, child) => Opacity(
              opacity: t.clamp(0, 1),
              child: Transform.scale(scale: 0.9 + 0.1 * t, child: child),
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: const [
                AppLogo(size: 120),
                SizedBox(height: 56),
                SizedBox(
                  width: 30,
                  height: 30,
                  child: CircularProgressIndicator(
                    strokeWidth: 2.6,
                    valueColor: AlwaysStoppedAnimation(AppColors.gold),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
