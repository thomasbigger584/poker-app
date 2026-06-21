import 'package:flutter/material.dart';

import '../theme/app_colors.dart';

/// Brand mark: the app logo in a gold-framed circle with the app name.
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
            boxShadow: [
              BoxShadow(
                color: AppColors.gold.withValues(alpha: 0.35),
                blurRadius: 28,
                spreadRadius: 2,
              ),
            ],
            border: Border.all(color: AppColors.gold.withValues(alpha: 0.6), width: size * 0.04),
          ),
          child: Padding(
            padding: EdgeInsets.all(size * 0.06),
            child: ClipOval(
              child: Image.asset(
                'assets/images/logo.png',
                fit: BoxFit.cover,
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
        ],
      ],
    );
  }
}
