import 'package:flutter/material.dart';

import '../theme/app_colors.dart';

/// Severity of an [AlertModalDialog] — mirrors the Android
/// `AlertModalDialog.AlertModalType`.
enum AlertModalType {
  info(Icons.info_outline_rounded, AppColors.gold),
  success(Icons.check_circle_outline_rounded, AppColors.success),
  warning(Icons.warning_amber_rounded, AppColors.warning),
  error(Icons.error_outline_rounded, AppColors.error);

  const AlertModalType(this.icon, this.color);

  final IconData icon;
  final Color color;
}

/// A blocking, non-dismissible alert dialog — the Flutter equivalent of the
/// Android `AlertModalDialog`. Used by the connectivity / Tailscale gate.
///
/// Returns `true` when the user taps the confirm button, `false`/`null`
/// otherwise. For WARNING/ERROR types a cancel button is shown by default.
class AlertModalDialog extends StatelessWidget {
  const AlertModalDialog({
    super.key,
    required this.type,
    required this.message,
    this.title,
    this.confirmText = 'Confirm',
    this.cancelText = 'Cancel',
    this.showCancel,
    this.barrierDismissible = false,
  });

  final AlertModalType type;
  final String message;
  final String? title;
  final String confirmText;
  final String cancelText;

  /// When null, cancel is shown for warning/error and hidden otherwise.
  final bool? showCancel;
  final bool barrierDismissible;

  String get _resolvedTitle {
    if (title != null) return title!;
    return switch (type) {
      AlertModalType.info => 'Info',
      AlertModalType.success => 'Success',
      AlertModalType.warning => 'Warning',
      AlertModalType.error => 'Error',
    };
  }

  bool get _resolvedShowCancel =>
      showCancel ?? (type == AlertModalType.warning || type == AlertModalType.error);

  /// Shows the dialog. By default it cannot be dismissed by tapping outside or
  /// the back button — matching the Android `setCancelable(false)` behaviour.
  static Future<bool?> show(
    BuildContext context, {
    required AlertModalType type,
    required String message,
    String? title,
    String confirmText = 'Confirm',
    String cancelText = 'Cancel',
    bool? showCancel,
    bool barrierDismissible = false,
  }) {
    return showDialog<bool>(
      context: context,
      barrierDismissible: barrierDismissible,
      barrierColor: Colors.black.withValues(alpha: 0.65),
      builder: (_) => AlertModalDialog(
        type: type,
        message: message,
        title: title,
        confirmText: confirmText,
        cancelText: cancelText,
        showCancel: showCancel,
        barrierDismissible: barrierDismissible,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: barrierDismissible,
      child: AlertDialog(
        icon: Container(
          width: 64,
          height: 64,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: type.color.withValues(alpha: 0.15),
          ),
          child: Icon(type.icon, color: type.color, size: 34),
        ),
        title: Text(_resolvedTitle, textAlign: TextAlign.center),
        content: Text(message, textAlign: TextAlign.center),
        actionsAlignment: MainAxisAlignment.center,
        actions: [
          if (_resolvedShowCancel)
            OutlinedButton(
              onPressed: () => Navigator.of(context).pop(false),
              child: Text(cancelText),
            ),
          FilledButton(
            style: FilledButton.styleFrom(
              backgroundColor: type.color,
              foregroundColor: AppColors.feltDark,
            ),
            onPressed: () => Navigator.of(context).pop(true),
            child: Text(confirmText),
          ),
        ],
      ),
    );
  }
}
