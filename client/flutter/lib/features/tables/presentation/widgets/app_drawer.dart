import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/network/api_exception.dart';
import '../../../../core/router/app_routes.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/util/money.dart';
import '../../../../core/widgets/alert_modal_dialog.dart';
import '../../../auth/domain/entities/auth_state.dart';
import '../../../auth/domain/entities/user_profile.dart';
import '../../../auth/presentation/auth_providers.dart';
import '../../../user/presentation/user_providers.dart';

/// Side navigation drawer (burger menu) — ports the Android `TableListActivity`
/// drawer: profile header with live funds, the feature destinations, plus
/// Reset Funds and Logout.
class AppDrawer extends ConsumerWidget {
  const AppDrawer({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final auth = ref.watch(authControllerProvider);
    final user = auth is Authenticated ? auth.user : null;
    final funds = ref.watch(currentUserProvider).value?.totalFunds;

    return Drawer(
      child: Column(
        children: [
          _DrawerHeader(user: user, funds: funds),
          Expanded(
            child: ListView(
              padding: EdgeInsets.zero,
              children: [
                _DrawerItem(
                  icon: Icons.account_balance_wallet_rounded,
                  label: 'Funds',
                  onTap: () => _go(context, AppRoutes.fundsName),
                ),
                _DrawerItem(
                  icon: Icons.receipt_long_rounded,
                  label: 'Transaction History',
                  onTap: () => _go(context, AppRoutes.transactionsName),
                ),
                _DrawerItem(
                  icon: Icons.bar_chart_rounded,
                  label: 'Player Stats',
                  onTap: () => _go(context, AppRoutes.statsName),
                ),
                _DrawerItem(
                  icon: Icons.emoji_events_rounded,
                  label: 'Achievements',
                  onTap: () => _go(context, AppRoutes.achievementsName),
                ),
                _DrawerItem(
                  icon: Icons.leaderboard_rounded,
                  label: 'Leaderboards',
                  onTap: () => _go(context, AppRoutes.leaderboardsName),
                ),
                const Divider(indent: 16, endIndent: 16),
                _DrawerItem(
                  icon: Icons.restart_alt_rounded,
                  label: 'Reset Funds',
                  onTap: () => _confirmResetFunds(context, ref),
                ),
              ],
            ),
          ),
          const Divider(height: 1),
          _DrawerItem(
            icon: Icons.logout_rounded,
            label: 'Logout',
            iconColor: AppColors.error,
            onTap: () => _confirmLogout(context, ref),
          ),
          const SizedBox(height: 8),
        ],
      ),
    );
  }

  void _go(BuildContext context, String routeName) {
    Navigator.of(context).pop(); // close drawer
    context.pushNamed(routeName);
  }

  Future<void> _confirmResetFunds(BuildContext context, WidgetRef ref) async {
    // Capture the messenger (lives above the drawer) before any async gap.
    final messenger = ScaffoldMessenger.of(context);
    Navigator.of(context).pop(); // close drawer
    final confirmed = await AlertModalDialog.show(
      context,
      type: AlertModalType.warning,
      title: 'Reset funds?',
      message: 'This returns your balance to the starting amount. '
          'Existing transactions are kept in your history.',
      confirmText: 'Reset',
    );
    if (confirmed != true) return;

    try {
      final user = await ref.read(currentUserProvider.notifier).resetFunds();
      messenger
        ..clearSnackBars()
        ..showSnackBar(SnackBar(
          content: Text('Funds reset to ${Money.compact(user.totalFunds)}.'),
        ));
    } on ApiException catch (e) {
      messenger
        ..clearSnackBars()
        ..showSnackBar(SnackBar(content: Text(e.message)));
    }
  }

  Future<void> _confirmLogout(BuildContext context, WidgetRef ref) async {
    final confirmed = await AlertModalDialog.show(
      context,
      type: AlertModalType.warning,
      title: 'Log out?',
      message: 'You will need to sign in again to rejoin a table.',
      confirmText: 'Log out',
    );
    if (confirmed == true) {
      await ref.read(authControllerProvider.notifier).logout();
    }
  }
}

class _DrawerHeader extends StatelessWidget {
  const _DrawerHeader({this.user, this.funds});

  final UserProfile? user;
  final String? funds;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding:
          EdgeInsets.fromLTRB(20, MediaQuery.of(context).padding.top + 28, 20, 24),
      decoration: const BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [AppColors.primaryGreen, AppColors.primaryGreenDark],
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisSize: MainAxisSize.min,
        children: [
          Row(
            children: [
              CircleAvatar(
                radius: 28,
                backgroundColor: AppColors.gold,
                child: Text(
                  user?.initials ?? '?',
                  style: const TextStyle(
                    color: AppColors.feltDark,
                    fontWeight: FontWeight.w800,
                    fontSize: 20,
                  ),
                ),
              ),
              const Spacer(),
              _FundsChip(funds: funds),
            ],
          ),
          const SizedBox(height: 16),
          Text(
            user?.displayName ?? user?.username ?? 'Player',
            style: const TextStyle(
              color: Colors.white,
              fontSize: 18,
              fontWeight: FontWeight.w700,
            ),
            overflow: TextOverflow.ellipsis,
          ),
          if (user?.email != null)
            Text(
              user!.email!,
              style:
                  TextStyle(color: Colors.white.withValues(alpha: 0.8), fontSize: 13),
              overflow: TextOverflow.ellipsis,
            ),
        ],
      ),
    );
  }
}

class _FundsChip extends StatelessWidget {
  const _FundsChip({this.funds});

  /// BigDecimal-as-string total funds, or null while loading.
  final String? funds;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 7),
      decoration: BoxDecoration(
        color: Colors.black.withValues(alpha: 0.25),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Icon(Icons.toll_rounded, color: AppColors.gold, size: 16),
          const SizedBox(width: 6),
          Text(
            funds == null ? '—' : Money.compact(funds),
            style: const TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.w700,
              fontSize: 13,
            ),
          ),
        ],
      ),
    );
  }
}

class _DrawerItem extends StatelessWidget {
  const _DrawerItem({
    required this.icon,
    required this.label,
    required this.onTap,
    this.iconColor,
  });

  final IconData icon;
  final String label;
  final VoidCallback onTap;
  final Color? iconColor;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: Icon(icon, color: iconColor ?? AppColors.gold),
      title: Text(label),
      onTap: onTap,
    );
  }
}
