import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/theme/app_colors.dart';
import '../../../../core/widgets/alert_modal_dialog.dart';
import '../../../auth/domain/entities/auth_state.dart';
import '../../../auth/domain/entities/user_profile.dart';
import '../../../auth/presentation/auth_providers.dart';

/// Side navigation drawer (burger menu) — ports the Android `TableListActivity`
/// drawer: profile header + Stats / Transactions / Achievements / Leaderboards,
/// plus Reset Funds and Logout.
class AppDrawer extends ConsumerWidget {
  const AppDrawer({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final auth = ref.watch(authControllerProvider);
    final user = auth is Authenticated ? auth.user : null;

    return Drawer(
      child: Column(
        children: [
          _DrawerHeader(user: user),
          Expanded(
            child: ListView(
              padding: EdgeInsets.zero,
              children: [
                _DrawerItem(
                  icon: Icons.bar_chart_rounded,
                  label: 'Player Stats',
                  onTap: () => _comingSoon(context, 'Player Stats'),
                ),
                _DrawerItem(
                  icon: Icons.receipt_long_rounded,
                  label: 'Transaction History',
                  onTap: () => _comingSoon(context, 'Transaction History'),
                ),
                _DrawerItem(
                  icon: Icons.emoji_events_rounded,
                  label: 'Achievements',
                  onTap: () => _comingSoon(context, 'Achievements'),
                ),
                _DrawerItem(
                  icon: Icons.leaderboard_rounded,
                  label: 'Leaderboards',
                  onTap: () => _comingSoon(context, 'Leaderboards'),
                ),
                const Divider(indent: 16, endIndent: 16),
                _DrawerItem(
                  icon: Icons.account_balance_wallet_rounded,
                  label: 'Reset Funds',
                  onTap: () => _comingSoon(context, 'Reset Funds'),
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

  void _comingSoon(BuildContext context, String feature) {
    Navigator.of(context).pop(); // close drawer
    ScaffoldMessenger.of(context)
      ..clearSnackBars()
      ..showSnackBar(SnackBar(content: Text('$feature — coming soon')));
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
  const _DrawerHeader({this.user});

  final UserProfile? user;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: EdgeInsets.fromLTRB(20, MediaQuery.of(context).padding.top + 28, 20, 24),
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
              const _FundsChip(amount: null),
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
              style: TextStyle(color: Colors.white.withValues(alpha: 0.8), fontSize: 13),
              overflow: TextOverflow.ellipsis,
            ),
        ],
      ),
    );
  }
}

class _FundsChip extends StatelessWidget {
  const _FundsChip({this.amount});

  final num? amount;

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
            amount == null ? '—' : amount!.toStringAsFixed(0),
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
