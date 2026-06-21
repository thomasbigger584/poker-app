import 'package:permission_handler/permission_handler.dart';

import '../../../core/logging/app_logger.dart';
import '../../../core/platform/platform_info.dart';

/// Requests the runtime permissions the app needs at startup. Mirrors the
/// Android `BasePermissionsActivity` (only POST_NOTIFICATIONS is requested at
/// runtime; the rest are install-time).
class PermissionService {
  /// Best-effort, non-blocking: a denied notification permission never stops
  /// the user from playing — just as on Android.
  Future<void> requestStartupPermissions() async {
    if (!PlatformInfo.isAndroid) return;
    try {
      final status = await Permission.notification.status;
      if (status.isDenied) {
        await Permission.notification.request();
      }
    } catch (e) {
      log.w('Notification permission request failed (ignored): $e');
    }
  }
}
