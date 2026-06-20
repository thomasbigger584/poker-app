import 'package:flutter/material.dart';

import '../theme/app_colors.dart';

/// Brand mark: a stylised gold chip/spade lockup with the app name.
class AppLogo extends StatelessWidget {
  const AppLogo({super.key, this.size = 96, this.showWordmark = true});

  final double size;
  final bool showWordmark;

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Container(
          width: size,
          height: size,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            gradient: const LinearGradient(
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
              colors: [AppColors.goldBright, AppColors.gold],
            ),
            boxShadow: [
              BoxShadow(
                color: AppColors.gold.withValues(alpha: 0.35),
                blurRadius: 28,
                spreadRadius: 2,
              ),
            ],
            border: Border.all(color: AppColors.feltDark.withValues(alpha: 0.25), width: size * 0.04),
          ),
          child: Center(
            child: Text(
              '♠',
              style: TextStyle(
                fontSize: size * 0.5,
                height: 1,
                color: AppColors.feltDark,
                fontWeight: FontWeight.w900,
              ),
            ),
          ),
        ),
        if (showWordmark) ...[
          SizedBox(height: size * 0.28),
          const Text(
            'TWB POKER',
            style: TextStyle(
              color: AppColors.textPrimary,
              fontSize: 26,
              fontWeight: FontWeight.w800,
              letterSpacing: 4,
            ),
          ),
          const SizedBox(height: 6),
          Text(
            'TEXAS HOLD’EM',
            style: TextStyle(
              color: AppColors.gold.withValues(alpha: 0.85),
              fontSize: 12,
              fontWeight: FontWeight.w600,
              letterSpacing: 6,
            ),
          ),
        ],
      ],
    );
  }
}
