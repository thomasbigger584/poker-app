import 'package:flutter/material.dart';

/// Poker-table palette — values mirror the native Android client's
/// `res/values/colors.xml` so both clients read as one product.
abstract final class AppColors {
  // Casino gold accent (Android `colorAccent` / `tableGold`).
  static const Color gold = Color(0xFFC8961E); // tableGoldDeep / colorAccent
  static const Color goldBright = Color(0xFFFFD24A); // tableGold

  // Felt greens (Android felt + room colors).
  static const Color feltDark = Color(0xFF04140A); // roomDarker
  static const Color felt = Color(0xFF0A2412); // roomDark
  static const Color feltMid = Color(0xFF0E3D1B); // feltGreenDark
  static const Color feltLight = Color(0xFF1E6B30); // feltGreenMid
  static const Color feltBright = Color(0xFF2E8B45); // feltGreenLight

  // Brand green (Android colorPrimary family).
  static const Color primaryGreen = Color(0xFF1E7D3A);
  static const Color primaryGreenLight = Color(0xFF3CA75A);
  static const Color primaryGreenDark = Color(0xFF0E4D22);

  // Surfaces / cards.
  static const Color surface = Color(0xFF0E3D1B);
  static const Color surfaceHigh = Color(0xFF15532A);

  // Text.
  static const Color textPrimary = Color(0xFFF3F6F4);
  static const Color textSecondary = Color(0xFFA9BDB2);

  // Status.
  static const Color success = Color(0xFF4CAF50);
  static const Color warning = Color(0xFFF0AD4E);
  static const Color error = Color(0xFFF44336);

  // Playing-card red.
  static const Color cardRed = Color(0xFFD0021B);

  static const List<Color> feltGradient = [feltLight, feltMid, feltDark];
}
