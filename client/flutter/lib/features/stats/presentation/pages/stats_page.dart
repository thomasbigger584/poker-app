import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/network/api_exception.dart';
import '../../../../core/proto/gen/poker/enums.pb.dart';
import '../../../../core/proto/gen/poker/rest.pb.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/util/game_display.dart';
import '../../../../core/util/money.dart';
import '../../../../core/widgets/felt_background.dart';
import '../stats_providers.dart';

/// Player statistics dashboard. Replaces the Android "Not Implemented" stub with
/// a real, data-rich screen backed by GET /api/stats/current (aggregate queries
/// over the gameplay tables).
class StatsPage extends ConsumerWidget {
  const StatsPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final statsAsync = ref.watch(playerStatsProvider);

    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: AppBar(title: const Text('Player Stats')),
      body: FeltBackground(
        child: SafeArea(
          child: RefreshIndicator(
            onRefresh: () => ref.refresh(playerStatsProvider.future),
            child: statsAsync.when(
              skipLoadingOnRefresh: true,
              data: (stats) => _StatsDashboard(stats: stats),
              error: (error, _) => _ErrorState(
                message: error is ApiException
                    ? error.message
                    : 'Could not load your stats.',
                onRetry: () => ref.invalidate(playerStatsProvider),
              ),
              loading: () => const Center(child: CircularProgressIndicator()),
            ),
          ),
        ),
      ),
    );
  }
}

class _StatsDashboard extends StatelessWidget {
  const _StatsDashboard({required this.stats});

  final PlayerStatsDTO stats;

  bool get _hasPlayed => stats.handsPlayed > 0;

  @override
  Widget build(BuildContext context) {
    return ListView(
      physics: const AlwaysScrollableScrollPhysics(),
      padding: const EdgeInsets.fromLTRB(16, 12, 16, 28),
      children: [
        _ProfitHero(stats: stats),
        const SizedBox(height: 22),
        if (!_hasPlayed) const _NoGamesNote(),
        const _SectionTitle('Performance'),
        const SizedBox(height: 10),
        _StatGrid(
          tiles: [
            _Stat('Hands played', '${stats.handsPlayed}',
                Icons.style_rounded),
            _Stat('Rounds won', '${stats.roundsWon}',
                Icons.emoji_events_rounded),
            _Stat('Win rate', _percent(stats.winRate),
                Icons.percent_rounded),
            _Stat('Tables joined', '${stats.tablesJoined}',
                Icons.table_bar_rounded),
          ],
        ),
        const SizedBox(height: 22),
        const _SectionTitle('Bankroll'),
        const SizedBox(height: 10),
        _StatGrid(
          tiles: [
            _Stat('Total winnings', Money.compact(stats.totalWinnings),
                Icons.trending_up_rounded,
                accent: AppColors.success),
            _Stat('Biggest pot', Money.compact(stats.biggestPotWon),
                Icons.savings_rounded, accent: AppColors.gold),
            _Stat('Total wagered', Money.compact(stats.totalWagered),
                Icons.casino_rounded),
            _Stat('Buy-ins', Money.compact(stats.totalBuyIns),
                Icons.login_rounded),
            _Stat('Cash-outs', Money.compact(stats.totalCashOuts),
                Icons.logout_rounded),
            _Stat('Current chips', Money.compact(stats.currentFunds),
                Icons.toll_rounded, accent: AppColors.gold),
          ],
        ),
        const SizedBox(height: 22),
        const _SectionTitle('Highlights'),
        const SizedBox(height: 10),
        Row(
          children: [
            Expanded(
              child: _HighlightCard(
                icon: Icons.auto_awesome_rounded,
                label: 'Best hand',
                value: GameDisplay.handType(stats.bestHand),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: _HighlightCard(
                icon: Icons.bolt_rounded,
                label: 'Favourite move',
                value: stats.favoriteAction ==
                        ActionType.ACTION_TYPE_UNSPECIFIED
                    ? '—'
                    : GameDisplay.actionType(stats.favoriteAction),
              ),
            ),
          ],
        ),
        const SizedBox(height: 22),
        const _SectionTitle('Play style'),
        const SizedBox(height: 10),
        _ActionBreakdown(stats: stats),
      ],
    );
  }

  static String _percent(double rate) => '${(rate * 100).toStringAsFixed(1)}%';
}

class _ProfitHero extends StatelessWidget {
  const _ProfitHero({required this.stats});

  final PlayerStatsDTO stats;

  @override
  Widget build(BuildContext context) {
    final net = Money.parse(stats.netTableProfit);
    final positive = net >= 0;
    final color = positive ? AppColors.success : AppColors.error;

    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(horizontal: 22, vertical: 24),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(20),
        gradient: const LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [AppColors.surfaceHigh, AppColors.feltMid],
        ),
        border: Border.all(color: AppColors.feltLight.withValues(alpha: 0.5)),
      ),
      child: Column(
        children: [
          const Text(
            'NET TABLE PROFIT',
            style: TextStyle(
              color: AppColors.textSecondary,
              letterSpacing: 1.4,
              fontWeight: FontWeight.w700,
              fontSize: 12,
            ),
          ),
          const SizedBox(height: 10),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                positive
                    ? Icons.arrow_upward_rounded
                    : Icons.arrow_downward_rounded,
                color: color,
                size: 30,
              ),
              const SizedBox(width: 6),
              Flexible(
                child: FittedBox(
                  child: Text(
                    Money.signed(stats.netTableProfit, positive: positive),
                    style: TextStyle(
                      color: color,
                      fontWeight: FontWeight.w900,
                      fontSize: 40,
                    ),
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 4),
          Text(
            'Cash-outs minus buy-ins across every table',
            style: TextStyle(
              color: AppColors.textSecondary.withValues(alpha: 0.9),
              fontSize: 12.5,
            ),
          ),
        ],
      ),
    );
  }
}

class _NoGamesNote extends StatelessWidget {
  const _NoGamesNote();

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 22),
      child: Container(
        padding: const EdgeInsets.all(14),
        decoration: BoxDecoration(
          color: AppColors.gold.withValues(alpha: 0.12),
          borderRadius: BorderRadius.circular(14),
        ),
        child: const Row(
          children: [
            Icon(Icons.info_outline_rounded, color: AppColors.gold, size: 20),
            SizedBox(width: 10),
            Expanded(
              child: Text(
                'Play a few hands to start building your stats.',
                style: TextStyle(color: AppColors.textPrimary, fontSize: 13),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _SectionTitle extends StatelessWidget {
  const _SectionTitle(this.title);

  final String title;

  @override
  Widget build(BuildContext context) {
    return Text(
      title.toUpperCase(),
      style: const TextStyle(
        color: AppColors.gold,
        fontWeight: FontWeight.w800,
        letterSpacing: 0.8,
        fontSize: 12.5,
      ),
    );
  }
}

/// A label/value/icon metric.
class _Stat {
  const _Stat(this.label, this.value, this.icon, {this.accent});

  final String label;
  final String value;
  final IconData icon;
  final Color? accent;
}

/// Responsive grid of stat tiles (2 columns on phones, 3 on wider screens).
class _StatGrid extends StatelessWidget {
  const _StatGrid({required this.tiles});

  final List<_Stat> tiles;

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final columns = constraints.maxWidth >= 520 ? 3 : 2;
        const spacing = 12.0;
        final width =
            (constraints.maxWidth - spacing * (columns - 1)) / columns;
        return Wrap(
          spacing: spacing,
          runSpacing: spacing,
          children: [
            for (final tile in tiles)
              SizedBox(width: width, child: _StatTile(stat: tile)),
          ],
        );
      },
    );
  }
}

class _StatTile extends StatelessWidget {
  const _StatTile({required this.stat});

  final _Stat stat;

  @override
  Widget build(BuildContext context) {
    final accent = stat.accent ?? AppColors.textPrimary;
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 14),
      decoration: BoxDecoration(
        color: Colors.black.withValues(alpha: 0.22),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: AppColors.feltLight.withValues(alpha: 0.3)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(stat.icon, color: AppColors.gold, size: 20),
          const SizedBox(height: 12),
          FittedBox(
            fit: BoxFit.scaleDown,
            alignment: Alignment.centerLeft,
            child: Text(
              stat.value,
              style: TextStyle(
                color: accent,
                fontWeight: FontWeight.w900,
                fontSize: 22,
              ),
            ),
          ),
          const SizedBox(height: 2),
          Text(
            stat.label,
            style: const TextStyle(
              color: AppColors.textSecondary,
              fontSize: 12.5,
            ),
          ),
        ],
      ),
    );
  }
}

class _HighlightCard extends StatelessWidget {
  const _HighlightCard({
    required this.icon,
    required this.label,
    required this.value,
  });

  final IconData icon;
  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 18),
      decoration: BoxDecoration(
        color: Colors.black.withValues(alpha: 0.22),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: AppColors.gold.withValues(alpha: 0.35)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(icon, color: AppColors.gold, size: 22),
          const SizedBox(height: 12),
          Text(
            value,
            style: const TextStyle(
              color: AppColors.textPrimary,
              fontWeight: FontWeight.w800,
              fontSize: 16,
            ),
          ),
          const SizedBox(height: 2),
          Text(
            label,
            style: const TextStyle(
              color: AppColors.textSecondary,
              fontSize: 12.5,
            ),
          ),
        ],
      ),
    );
  }
}

class _ActionBreakdown extends StatelessWidget {
  const _ActionBreakdown({required this.stats});

  final PlayerStatsDTO stats;

  @override
  Widget build(BuildContext context) {
    final rows = <(String, int)>[
      ('Check', stats.checks),
      ('Bet', stats.bets),
      ('Call', stats.calls),
      ('Raise', stats.raises),
      ('Fold', stats.folds),
      ('All-in', stats.allIns),
    ];
    final max = rows.fold<int>(1, (m, r) => r.$2 > m ? r.$2 : m);

    return Container(
      padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
      decoration: BoxDecoration(
        color: Colors.black.withValues(alpha: 0.22),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: AppColors.feltLight.withValues(alpha: 0.3)),
      ),
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text(
                'Aggression factor',
                style: TextStyle(color: AppColors.textSecondary, fontSize: 13),
              ),
              Text(
                stats.aggressionFactor.toStringAsFixed(2),
                style: const TextStyle(
                  color: AppColors.gold,
                  fontWeight: FontWeight.w800,
                  fontSize: 15,
                ),
              ),
            ],
          ),
          const Divider(height: 22),
          for (final row in rows)
            Padding(
              padding: const EdgeInsets.only(bottom: 12),
              child: _ActionBar(label: row.$1, count: row.$2, max: max),
            ),
        ],
      ),
    );
  }
}

class _ActionBar extends StatelessWidget {
  const _ActionBar({
    required this.label,
    required this.count,
    required this.max,
  });

  final String label;
  final int count;
  final int max;

  @override
  Widget build(BuildContext context) {
    final fraction = max == 0 ? 0.0 : count / max;
    return Row(
      children: [
        SizedBox(
          width: 56,
          child: Text(
            label,
            style: const TextStyle(
              color: AppColors.textPrimary,
              fontSize: 13,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
        Expanded(
          child: ClipRRect(
            borderRadius: BorderRadius.circular(6),
            child: Container(
              height: 10,
              color: Colors.black.withValues(alpha: 0.3),
              alignment: Alignment.centerLeft,
              child: FractionallySizedBox(
                widthFactor: fraction.clamp(0.0, 1.0),
                child: Container(
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(6),
                    gradient: const LinearGradient(
                      colors: [AppColors.feltBright, AppColors.gold],
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
        SizedBox(
          width: 44,
          child: Text(
            '$count',
            textAlign: TextAlign.right,
            style: const TextStyle(
              color: AppColors.textSecondary,
              fontSize: 13,
              fontWeight: FontWeight.w700,
            ),
          ),
        ),
      ],
    );
  }
}

class _ErrorState extends StatelessWidget {
  const _ErrorState({required this.message, required this.onRetry});

  final String message;
  final VoidCallback onRetry;

  @override
  Widget build(BuildContext context) {
    return ListView(
      physics: const AlwaysScrollableScrollPhysics(),
      children: [
        SizedBox(height: MediaQuery.of(context).size.height * 0.2),
        const Icon(Icons.cloud_off_rounded, size: 60, color: AppColors.error),
        const SizedBox(height: 14),
        Text(
          message,
          textAlign: TextAlign.center,
          style: const TextStyle(color: AppColors.textPrimary, fontSize: 16),
        ),
        const SizedBox(height: 18),
        Center(
          child: OutlinedButton.icon(
            onPressed: onRetry,
            icon: const Icon(Icons.refresh_rounded),
            label: const Text('Try again'),
          ),
        ),
      ],
    );
  }
}
