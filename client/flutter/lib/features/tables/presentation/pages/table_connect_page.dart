import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/proto/gen/poker/domain.pb.dart';
import '../../../../core/proto/gen/poker/enums.pb.dart';
import '../../../../core/proto/gen/poker/rest.pb.dart';
import '../../../../core/router/app_routes.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/util/game_display.dart';
import '../../../../core/util/money.dart';
import '../../../../core/widgets/felt_background.dart';
import '../../../game/presentation/game_launch_args.dart';

/// Buy-in / connection screen shown before entering a table. Ports the Android
/// `TableConnectActivity`: choose Player vs Viewer and (for players) a buy-in
/// within the table's range, with min/max quick-fill and a slider.
class TableConnectPage extends StatefulWidget {
  const TableConnectPage({super.key, required this.entry});

  final AvailableTableDTO entry;

  @override
  State<TableConnectPage> createState() => _TableConnectPageState();
}

class _TableConnectPageState extends State<TableConnectPage> {
  late final double _min = Money.parse(widget.entry.table.minBuyin);
  late final double _max = Money.parse(widget.entry.table.maxBuyin);
  late final TextEditingController _buyIn =
      TextEditingController(text: _min.round().toString());

  ConnectionType _connectionType = ConnectionType.CONNECTION_TYPE_PLAYER;
  String? _error;

  bool get _isPlayer =>
      _connectionType == ConnectionType.CONNECTION_TYPE_PLAYER;

  @override
  void dispose() {
    _buyIn.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final table = widget.entry.table;

    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: AppBar(title: const Text('Connect to Table')),
      body: FeltBackground(
        child: SafeArea(
          child: ListView(
            padding: const EdgeInsets.fromLTRB(16, 8, 16, 28),
            children: [
              _TableHeader(table: table),
              const SizedBox(height: 18),
              SegmentedButton<ConnectionType>(
                segments: const [
                  ButtonSegment(
                    value: ConnectionType.CONNECTION_TYPE_PLAYER,
                    icon: Icon(Icons.sports_esports_rounded),
                    label: Text('Player'),
                  ),
                  ButtonSegment(
                    value: ConnectionType.CONNECTION_TYPE_LISTENER,
                    icon: Icon(Icons.visibility_rounded),
                    label: Text('Viewer'),
                  ),
                ],
                selected: {_connectionType},
                onSelectionChanged: (s) =>
                    setState(() => _connectionType = s.first),
              ),
              const SizedBox(height: 18),
              if (_isPlayer) _buildBuyInSection() else _buildViewerNote(),
              const SizedBox(height: 24),
              FilledButton.icon(
                onPressed: _connect,
                icon: const Icon(Icons.login_rounded),
                label: Text(_isPlayer ? 'Buy in & connect' : 'Watch table'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildBuyInSection() {
    final sliderValue = (Money.parse(_buyIn.text)).clamp(_min, _max).toDouble();
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Buy-in', style: Theme.of(context).textTheme.labelLarge),
            const SizedBox(height: 6),
            Text(
              'Allowed: ${Money.compact(widget.entry.table.minBuyin)} – '
              '${Money.compact(widget.entry.table.maxBuyin)} chips',
              style: const TextStyle(
                  color: AppColors.textSecondary, fontSize: 12.5),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: _buyIn,
              keyboardType: TextInputType.number,
              inputFormatters: [FilteringTextInputFormatter.digitsOnly],
              onChanged: (_) => setState(() => _error = null),
              decoration: InputDecoration(
                prefixIcon: const Icon(Icons.toll_rounded),
                border: const OutlineInputBorder(),
                errorText: _error,
              ),
            ),
            if (_max > _min)
              Slider(
                value: sliderValue,
                min: _min,
                max: _max,
                onChanged: (v) => setState(() {
                  _buyIn.text = v.round().toString();
                  _error = null;
                }),
              ),
            Row(
              children: [
                Expanded(
                  child: OutlinedButton(
                    onPressed: () => _setBuyIn(_min),
                    child: Text('Min ${Money.compact(widget.entry.table.minBuyin)}'),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: OutlinedButton(
                    onPressed: () => _setBuyIn(_max),
                    child: Text('Max ${Money.compact(widget.entry.table.maxBuyin)}'),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildViewerNote() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            const Icon(Icons.visibility_rounded, color: AppColors.gold),
            const SizedBox(width: 12),
            Expanded(
              child: Text(
                'Watch this table without joining. No buy-in is required and '
                'you won’t be dealt into hands.',
                style: TextStyle(
                  color: AppColors.textSecondary,
                  height: 1.4,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _setBuyIn(double value) {
    setState(() {
      _buyIn.text = value.round().toString();
      _error = null;
    });
  }

  void _connect() {
    var buyIn = '0';
    if (_isPlayer) {
      final amount = Money.parse(_buyIn.text);
      if (_buyIn.text.trim().isEmpty || amount <= 0) {
        setState(() => _error = 'Enter a buy-in amount');
        return;
      }
      if (amount < _min || amount > _max) {
        setState(() => _error =
            'Must be between ${Money.compact(widget.entry.table.minBuyin)} '
            'and ${Money.compact(widget.entry.table.maxBuyin)}');
        return;
      }
      buyIn = amount.round().toString();
    }

    FocusScope.of(context).unfocus();
    // Replace this screen so Back from the game returns to the lobby.
    context.pushReplacementNamed(
      AppRoutes.gameName,
      extra: GameLaunchArgs(
        table: widget.entry.table,
        connectionType: _connectionType,
        buyInAmount: buyIn,
      ),
    );
  }
}

class _TableHeader extends StatelessWidget {
  const _TableHeader({required this.table});

  final TableDTO table;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Container(
          width: 56,
          height: 56,
          decoration: BoxDecoration(
            color: AppColors.feltDark.withValues(alpha: 0.6),
            borderRadius: BorderRadius.circular(16),
            border:
                Border.all(color: AppColors.feltLight.withValues(alpha: 0.4)),
          ),
          child: Icon(GameDisplay.gameTypeIcon(table.gameType),
              color: AppColors.gold, size: 28),
        ),
        const SizedBox(width: 14),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                table.name,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
                style: const TextStyle(
                  color: AppColors.textPrimary,
                  fontWeight: FontWeight.w800,
                  fontSize: 18,
                ),
              ),
              const SizedBox(height: 2),
              Text(
                GameDisplay.gameType(table.gameType),
                style: const TextStyle(color: AppColors.textSecondary),
              ),
            ],
          ),
        ),
      ],
    );
  }
}
