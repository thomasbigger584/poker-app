import 'package:flutter/material.dart';

import '../../../../core/proto/gen/poker/enums.pb.dart';
import '../../../../core/proto/gen/poker/rest.pb.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/util/game_display.dart';
import '../../../../core/util/money.dart';

/// A single lobby row for an available table. Improves on the Android card by
/// surfacing player count, buy-in range and a join/reconnect state at a glance.
class TableListItem extends StatelessWidget {
  const TableListItem({
    super.key,
    required this.entry,
    required this.onConnect,
    required this.onReconnect,
  });

  final AvailableTableDTO entry;
  final VoidCallback onConnect;
  final VoidCallback onReconnect;

  bool get _canReconnect =>
      entry.currentUserConnected &&
      entry.hasReconnectMillisRemaining() &&
      entry.reconnectMillisRemaining.toInt() > 0;

  @override
  Widget build(BuildContext context) {
    final table = entry.table;
    final isFull = entry.playersConnected >= table.maxPlayers;

    return Card(
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: _canReconnect ? onReconnect : (isFull ? null : onConnect),
        child: Padding(
          padding: const EdgeInsets.fromLTRB(16, 14, 12, 14),
          child: Row(
            children: [
              _GameBadge(gameType: table.gameType),
              const SizedBox(width: 14),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Expanded(
                          child: Text(
                            table.name,
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                            style: const TextStyle(
                              color: AppColors.textPrimary,
                              fontWeight: FontWeight.w800,
                              fontSize: 16,
                            ),
                          ),
                        ),
                        if (_canReconnect)
                          const _StatusPill(
                            label: 'Seated',
                            color: AppColors.gold,
                          )
                        else if (isFull)
                          const _StatusPill(
                            label: 'Full',
                            color: AppColors.error,
                          ),
                      ],
                    ),
                    const SizedBox(height: 4),
                    Text(
                      GameDisplay.gameType(table.gameType),
                      style: const TextStyle(
                        color: AppColors.textSecondary,
                        fontSize: 13,
                      ),
                    ),
                    const SizedBox(height: 10),
                    Wrap(
                      spacing: 8,
                      runSpacing: 6,
                      children: [
                        _MetaChip(
                          icon: Icons.people_alt_rounded,
                          label:
                              '${entry.playersConnected}/${table.maxPlayers}',
                        ),
                        _MetaChip(
                          icon: Icons.toll_rounded,
                          label:
                              '${Money.compact(table.minBuyin)}–${Money.compact(table.maxBuyin)}',
                        ),
                      ],
                    ),
                  ],
                ),
              ),
              const SizedBox(width: 8),
              _ActionButton(
                canReconnect: _canReconnect,
                isFull: isFull,
                onConnect: onConnect,
                onReconnect: onReconnect,
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _GameBadge extends StatelessWidget {
  const _GameBadge({required this.gameType});

  final GameType gameType;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 52,
      height: 52,
      decoration: BoxDecoration(
        color: AppColors.feltDark.withValues(alpha: 0.6),
        borderRadius: BorderRadius.circular(14),
        border: Border.all(color: AppColors.feltLight.withValues(alpha: 0.4)),
      ),
      child: Icon(
        GameDisplay.gameTypeIcon(gameType),
        color: AppColors.gold,
        size: 26,
      ),
    );
  }
}

class _ActionButton extends StatelessWidget {
  const _ActionButton({
    required this.canReconnect,
    required this.isFull,
    required this.onConnect,
    required this.onReconnect,
  });

  final bool canReconnect;
  final bool isFull;
  final VoidCallback onConnect;
  final VoidCallback onReconnect;

  @override
  Widget build(BuildContext context) {
    if (canReconnect) {
      return FilledButton.tonalIcon(
        onPressed: onReconnect,
        icon: const Icon(Icons.replay_rounded, size: 18),
        label: const Text('Rejoin'),
      );
    }
    return FilledButton(
      onPressed: isFull ? null : onConnect,
      child: const Text('Connect'),
    );
  }
}

class _MetaChip extends StatelessWidget {
  const _MetaChip({required this.icon, required this.label});

  final IconData icon;
  final String label;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
      decoration: BoxDecoration(
        color: Colors.black.withValues(alpha: 0.2),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 14, color: AppColors.textSecondary),
          const SizedBox(width: 5),
          Text(
            label,
            style: const TextStyle(
              color: AppColors.textPrimary,
              fontSize: 12.5,
              fontWeight: FontWeight.w600,
            ),
          ),
        ],
      ),
    );
  }
}

class _StatusPill extends StatelessWidget {
  const _StatusPill({required this.label, required this.color});

  final String label;
  final Color color;

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(left: 8),
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.18),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Text(
        label.toUpperCase(),
        style: TextStyle(
          color: color,
          fontSize: 10.5,
          fontWeight: FontWeight.w800,
          letterSpacing: 0.6,
        ),
      ),
    );
  }
}
