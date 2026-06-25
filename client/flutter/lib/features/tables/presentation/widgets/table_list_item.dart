import 'package:flutter/material.dart';

import '../../../../core/proto/gen/poker/enums.pb.dart';
import '../../../../core/proto/gen/poker/rest.pb.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/util/game_display.dart';
import '../../../../core/util/money.dart';

/// A single lobby row for an available table. Presents the table as a premium
/// "card at the casino": a gold-accented game badge, the table name, a live
/// seat-capacity bar, and the key stakes/format stats at a glance, with a
/// state-aware join/reconnect call to action.
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
    final maxPlayers = table.maxPlayers;
    final seated = entry.playersConnected;
    final isFull = maxPlayers > 0 && seated >= maxPlayers;

    return DecoratedBox(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(18),
        gradient: const LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [AppColors.surfaceHigh, AppColors.surface],
        ),
        border: Border.all(color: AppColors.feltLight.withValues(alpha: 0.35)),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.35),
            blurRadius: 14,
            offset: const Offset(0, 6),
          ),
        ],
      ),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(18),
        child: Material(
          type: MaterialType.transparency,
          child: InkWell(
            onTap: _canReconnect ? onReconnect : (isFull ? null : onConnect),
            child: Padding(
              padding: const EdgeInsets.fromLTRB(16, 16, 16, 16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // ---- Header: badge + name/subtitle + status ----
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      _GameBadge(gameType: table.gameType),
                      const SizedBox(width: 14),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              table.name,
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                              softWrap: false,
                              style: const TextStyle(
                                color: AppColors.textPrimary,
                                fontWeight: FontWeight.w800,
                                fontSize: 18,
                                height: 1.1,
                              ),
                            ),
                            const SizedBox(height: 3),
                            Text(
                              GameDisplay.gameType(table.gameType),
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                              softWrap: false,
                              style: TextStyle(
                                color: AppColors.gold.withValues(alpha: 0.95),
                                fontSize: 12.5,
                                fontWeight: FontWeight.w700,
                                letterSpacing: 0.3,
                              ),
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(width: 8),
                      if (_canReconnect)
                        const _StatusPill(label: 'Seated', color: AppColors.gold)
                      else if (isFull)
                        const _StatusPill(label: 'Full', color: AppColors.error)
                      else
                        const _StatusPill(label: 'Open', color: AppColors.success),
                    ],
                  ),
                  const SizedBox(height: 16),
                  // ---- Seat-capacity bar ----
                  _SeatCapacity(seated: seated, maxPlayers: maxPlayers),
                  const SizedBox(height: 14),
                  // ---- Stats + CTA ----
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.end,
                    children: [
                      Expanded(
                        child: Wrap(
                          spacing: 8,
                          runSpacing: 8,
                          children: [
                            _MetaChip(
                              icon: Icons.savings_rounded,
                              label:
                                  '${Money.compact(table.minBuyin)}–${Money.compact(table.maxBuyin)}',
                              tooltip: 'Buy-in range',
                            ),
                            if (table.totalRounds > 0)
                              _MetaChip(
                                icon: Icons.replay_rounded,
                                label: '${table.totalRounds} rounds',
                                tooltip: 'Rounds in this game',
                              ),
                            if (table.speedMultiplier > 1.0)
                              _MetaChip(
                                icon: Icons.bolt_rounded,
                                label: '${_trimSpeed(table.speedMultiplier)}× speed',
                                tooltip: 'Turn timer speed',
                                accent: true,
                              ),
                          ],
                        ),
                      ),
                      const SizedBox(width: 12),
                      _ActionButton(
                        canReconnect: _canReconnect,
                        isFull: isFull,
                        onConnect: onConnect,
                        onReconnect: onReconnect,
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  static String _trimSpeed(double value) {
    final asInt = value.truncateToDouble();
    return value == asInt
        ? value.toStringAsFixed(0)
        : value.toStringAsFixed(1);
  }
}

/// Gold-rimmed square badge carrying the game's icon.
class _GameBadge extends StatelessWidget {
  const _GameBadge({required this.gameType});

  final GameType gameType;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 54,
      height: 54,
      decoration: BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [
            AppColors.gold.withValues(alpha: 0.22),
            AppColors.feltDark.withValues(alpha: 0.65),
          ],
        ),
        borderRadius: BorderRadius.circular(15),
        border: Border.all(color: AppColors.gold.withValues(alpha: 0.55)),
      ),
      child: Icon(
        GameDisplay.gameTypeIcon(gameType),
        color: AppColors.goldBright,
        size: 28,
      ),
    );
  }
}

/// Horizontal capacity bar showing how many seats are taken.
class _SeatCapacity extends StatelessWidget {
  const _SeatCapacity({required this.seated, required this.maxPlayers});

  final int seated;
  final int maxPlayers;

  @override
  Widget build(BuildContext context) {
    final fraction = maxPlayers > 0
        ? (seated / maxPlayers).clamp(0.0, 1.0)
        : 0.0;
    final isFull = maxPlayers > 0 && seated >= maxPlayers;
    final barColor = isFull ? AppColors.error : AppColors.feltBright;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            const Icon(
              Icons.event_seat_rounded,
              size: 15,
              color: AppColors.textSecondary,
            ),
            const SizedBox(width: 6),
            Text(
              'Players',
              style: const TextStyle(
                color: AppColors.textSecondary,
                fontSize: 12,
                fontWeight: FontWeight.w600,
              ),
            ),
            const Spacer(),
            Text(
              '$seated / $maxPlayers',
              style: const TextStyle(
                color: AppColors.textPrimary,
                fontSize: 12.5,
                fontWeight: FontWeight.w800,
              ),
            ),
          ],
        ),
        const SizedBox(height: 7),
        ClipRRect(
          borderRadius: BorderRadius.circular(6),
          child: TweenAnimationBuilder<double>(
            tween: Tween(begin: 0, end: fraction),
            duration: const Duration(milliseconds: 450),
            curve: Curves.easeOutCubic,
            builder: (context, value, _) => LinearProgressIndicator(
              value: value,
              minHeight: 7,
              backgroundColor: Colors.black.withValues(alpha: 0.28),
              valueColor: AlwaysStoppedAnimation<Color>(barColor),
            ),
          ),
        ),
      ],
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
    return FilledButton.icon(
      onPressed: isFull ? null : onConnect,
      icon: Icon(isFull ? Icons.lock_rounded : Icons.login_rounded, size: 18),
      label: Text(isFull ? 'Full' : 'Connect'),
    );
  }
}

class _MetaChip extends StatelessWidget {
  const _MetaChip({
    required this.icon,
    required this.label,
    this.tooltip,
    this.accent = false,
  });

  final IconData icon;
  final String label;
  final String? tooltip;
  final bool accent;

  @override
  Widget build(BuildContext context) {
    final fg = accent ? AppColors.goldBright : AppColors.textPrimary;
    final chip = Container(
      padding: const EdgeInsets.symmetric(horizontal: 11, vertical: 6),
      decoration: BoxDecoration(
        color: accent
            ? AppColors.gold.withValues(alpha: 0.14)
            : Colors.black.withValues(alpha: 0.22),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(
          color: accent
              ? AppColors.gold.withValues(alpha: 0.45)
              : Colors.white.withValues(alpha: 0.06),
        ),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 14, color: accent ? AppColors.goldBright : AppColors.textSecondary),
          const SizedBox(width: 6),
          Text(
            label,
            style: TextStyle(
              color: fg,
              fontSize: 12.5,
              fontWeight: FontWeight.w700,
            ),
          ),
        ],
      ),
    );
    return tooltip == null ? chip : Tooltip(message: tooltip!, child: chip);
  }
}

class _StatusPill extends StatelessWidget {
  const _StatusPill({required this.label, required this.color});

  final String label;
  final Color color;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 9, vertical: 4),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.18),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: color.withValues(alpha: 0.45)),
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
