import 'package:android_intent_plus/android_intent.dart';
import 'package:android_intent_plus/flag.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../../core/logging/app_logger.dart';
import '../../../core/platform/platform_info.dart';

/// Sends the user to the Tailscale app (to enable the VPN) when off-tailnet —
/// the cross-platform version of the Android dialog's "Confirm" action.
class TailscaleLauncher {
  static const String _androidPackage = 'com.tailscale.ipn';
  static const String _iosAppStore =
      'https://apps.apple.com/app/tailscale/id1470499037';
  static const String _downloadPage = 'https://tailscale.com/download';

  Future<void> open() async {
    if (PlatformInfo.isAndroid) {
      await _openAndroid();
    } else if (PlatformInfo.isIOS) {
      await _launchUrl(_iosAppStore);
    } else {
      // macOS / Windows / Linux / Web — open the download/sign-in page.
      await _launchUrl(_downloadPage);
    }
  }

  /// Mirrors `getLaunchIntentForPackage("com.tailscale.ipn")` with the same
  /// market:// → https Play Store fallback chain as the Android client.
  Future<void> _openAndroid() async {
    try {
      const intent = AndroidIntent(
        action: 'android.intent.action.MAIN',
        category: 'android.intent.category.LAUNCHER',
        package: _androidPackage,
        flags: <int>[Flag.FLAG_ACTIVITY_NEW_TASK],
      );
      final canOpen = await intent.canResolveActivity() ?? false;
      if (canOpen) {
        await intent.launch();
        return;
      }
    } catch (e) {
      log.w('Launching Tailscale app failed, falling back to store: $e');
    }
    if (!await _launchUrl('market://details?id=$_androidPackage')) {
      await _launchUrl(
        'https://play.google.com/store/apps/details?id=$_androidPackage',
      );
    }
  }

  Future<bool> _launchUrl(String url) async {
    try {
      final uri = Uri.parse(url);
      if (await canLaunchUrl(uri)) {
        return launchUrl(uri, mode: LaunchMode.externalApplication);
      }
    } catch (e) {
      log.w('Could not launch $url: $e');
    }
    return false;
  }
}
