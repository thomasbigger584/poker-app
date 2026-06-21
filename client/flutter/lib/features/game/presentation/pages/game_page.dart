import 'package:flutter/material.dart';

import '../../../../core/theme/app_colors.dart';
import '../../../../core/util/game_display.dart';
import '../../../../core/util/money.dart';
import '../../../../core/widgets/felt_background.dart';
import '../game_launch_args.dart';

/// Placeholder for the in-game Texas Hold'em table. The real-time gameplay
/// screen (the Android `TexasGameActivity`, WebSocket/STOMP + protobuf) is a
/// later migration step; this confirms the full browse → connect → enter flow
/// works and echoes back the connection the user chose.
class GamePage extends StatelessWidget {
  const GamePage({super.key, required this.args});

  final GameLaunchArgs args;

  @override
  Widget build(BuildContext context) {
    final table = args.table;
    final theme = Theme.of(context);

    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: AppBar(title: Text(table.name)),
      body: FeltBackground(
        child: SafeArea(
          child: Center(
            child: Padding(
              padding: const EdgeInsets.all(28),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Container(
                    width: 110,
                    height: 110,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      color: Colors.black.withValues(alpha: 0.25),
                      border: Border.all(
                        color: AppColors.gold.withValues(alpha: 0.6),
                        width: 2,
                      ),
                    ),
                    child: Icon(
                      GameDisplay.gameTypeIcon(table.gameType),
                      size: 52,
                      color: AppColors.gold,
                    ),
                  ),
                  const SizedBox(height: 24),
                  Text(
                    'Table ready',
                    style: theme.textTheme.headlineSmall?.copyWith(
                      color: AppColors.textPrimary,
                      fontWeight: FontWeight.w800,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'The live ${GameDisplay.gameType(table.gameType)} table is '
                    'coming in a future update.',
                    textAlign: TextAlign.center,
                    style: const TextStyle(
                      color: AppColors.textSecondary,
                      height: 1.5,
                    ),
                  ),
                  const SizedBox(height: 28),
                  _SummaryCard(args: args),
                  const SizedBox(height: 28),
                  SizedBox(
                    width: double.infinity,
                    child: FilledButton.icon(
                      onPressed: () => Navigator.of(context).maybePop(),
                      icon: const Icon(Icons.arrow_back_rounded),
                      label: const Text('Back to lobby'),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class _SummaryCard extends StatelessWidget {
  const _SummaryCard({required this.args});

  final GameLaunchArgs args;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
        child: Column(
          children: [
            _row('Joining as', GameDisplay.connectionType(args.connectionType)),
            const Divider(height: 20),
            _row('Buy-in', '${Money.compact(args.buyInAmount)} chips'),
            const Divider(height: 20),
            _row('Game', GameDisplay.gameType(args.table.gameType)),
          ],
        ),
      ),
    );
  }

  Widget _row(String label, String value) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label, style: const TextStyle(color: AppColors.textSecondary)),
        Text(
          value,
          style: const TextStyle(
            color: AppColors.textPrimary,
            fontWeight: FontWeight.w700,
          ),
        ),
      ],
    );
  }
}
