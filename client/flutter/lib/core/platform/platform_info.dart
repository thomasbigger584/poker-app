import 'package:flutter/foundation.dart';

/// Thin, testable wrapper over platform detection so feature code never imports
/// `dart:io` directly (which would break web compilation).
abstract final class PlatformInfo {
  static bool get isWeb => kIsWeb;

  static bool get isAndroid =>
      !kIsWeb && defaultTargetPlatform == TargetPlatform.android;

  static bool get isIOS =>
      !kIsWeb && defaultTargetPlatform == TargetPlatform.iOS;

  static bool get isMacOS =>
      !kIsWeb && defaultTargetPlatform == TargetPlatform.macOS;

  static bool get isWindows =>
      !kIsWeb && defaultTargetPlatform == TargetPlatform.windows;

  static bool get isLinux =>
      !kIsWeb && defaultTargetPlatform == TargetPlatform.linux;

  static bool get isMobile => isAndroid || isIOS;

  /// Windows/Linux desktop, where the OAuth redirect uses a loopback server.
  static bool get isDesktopLoopback => isWindows || isLinux;

  /// Platforms that capture the OAuth redirect via a custom URI scheme
  /// (ASWebAuthenticationSession on Apple, Auth Tab on Android).
  static bool get usesCustomScheme => isAndroid || isIOS || isMacOS;
}
