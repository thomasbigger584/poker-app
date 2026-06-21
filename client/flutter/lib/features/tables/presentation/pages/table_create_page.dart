import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/network/api_exception.dart';
import '../../../../core/proto/gen/poker/enums.pb.dart';
import '../../../../core/proto/gen/poker/rest.pb.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/util/game_display.dart';
import '../../../../core/widgets/felt_background.dart';
import '../tables_providers.dart';

/// Create a new poker table. Ports the Android `TableCreateActivity` form with a
/// grouped layout, a segmented game-type selector and inline validation.
class TableCreatePage extends ConsumerStatefulWidget {
  const TableCreatePage({super.key});

  @override
  ConsumerState<TableCreatePage> createState() => _TableCreatePageState();
}

class _TableCreatePageState extends ConsumerState<TableCreatePage> {
  final _formKey = GlobalKey<FormState>();
  final _name = TextEditingController();
  final _speed = TextEditingController(text: '1');
  final _rounds = TextEditingController();
  final _minPlayers = TextEditingController(text: '2');
  final _maxPlayers = TextEditingController(text: '6');
  final _minBuyin = TextEditingController(text: '1000');
  final _maxBuyin = TextEditingController(text: '10000');

  GameType _gameType = GameType.GAME_TYPE_TEXAS_HOLDEM;
  bool _submitting = false;

  @override
  void dispose() {
    for (final c in [
      _name,
      _speed,
      _rounds,
      _minPlayers,
      _maxPlayers,
      _minBuyin,
      _maxBuyin,
    ]) {
      c.dispose();
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: AppBar(title: const Text('Create Table')),
      body: FeltBackground(
        child: SafeArea(
          child: Form(
            key: _formKey,
            child: ListView(
              padding: const EdgeInsets.fromLTRB(16, 8, 16, 28),
              children: [
                _Section(
                  title: 'Details',
                  children: [
                    _Field(
                      controller: _name,
                      label: 'Table name',
                      textInputAction: TextInputAction.next,
                      validator: (v) => (v == null || v.trim().isEmpty)
                          ? 'Enter a table name'
                          : null,
                    ),
                    const SizedBox(height: 16),
                    Text('Game type',
                        style: Theme.of(context).textTheme.labelLarge),
                    const SizedBox(height: 8),
                    _GameTypeSelector(
                      selected: _gameType,
                      onChanged: (g) => setState(() => _gameType = g),
                    ),
                  ],
                ),
                _Section(
                  title: 'Players',
                  children: [
                    Row(
                      children: [
                        Expanded(
                          child: _Field(
                            controller: _minPlayers,
                            label: 'Min',
                            keyboardType: TextInputType.number,
                            integer: true,
                            validator: _positiveInt,
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: _Field(
                            controller: _maxPlayers,
                            label: 'Max',
                            keyboardType: TextInputType.number,
                            integer: true,
                            validator: (v) {
                              final base = _positiveInt(v);
                              if (base != null) return base;
                              final min = int.tryParse(_minPlayers.text) ?? 0;
                              final max = int.tryParse(v!) ?? 0;
                              return max < min ? 'Max < min' : null;
                            },
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
                _Section(
                  title: 'Buy-in range (chips)',
                  children: [
                    Row(
                      children: [
                        Expanded(
                          child: _Field(
                            controller: _minBuyin,
                            label: 'Min',
                            keyboardType: TextInputType.number,
                            integer: true,
                            validator: _positiveInt,
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: _Field(
                            controller: _maxBuyin,
                            label: 'Max',
                            keyboardType: TextInputType.number,
                            integer: true,
                            validator: (v) {
                              final base = _positiveInt(v);
                              if (base != null) return base;
                              final min = int.tryParse(_minBuyin.text) ?? 0;
                              final max = int.tryParse(v!) ?? 0;
                              return max < min ? 'Max < min' : null;
                            },
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
                _Section(
                  title: 'Advanced (optional)',
                  children: [
                    _Field(
                      controller: _speed,
                      label: 'Speed multiplier',
                      keyboardType: const TextInputType.numberWithOptions(
                          decimal: true),
                      validator: (v) {
                        if (v == null || v.trim().isEmpty) return null;
                        final d = double.tryParse(v);
                        return (d == null || d <= 0)
                            ? 'Enter a positive number'
                            : null;
                      },
                    ),
                    const SizedBox(height: 16),
                    _Field(
                      controller: _rounds,
                      label: 'Total rounds (blank = unlimited)',
                      keyboardType: TextInputType.number,
                      integer: true,
                      validator: (v) {
                        if (v == null || v.trim().isEmpty) return null;
                        final n = int.tryParse(v);
                        return (n == null || n <= 0)
                            ? 'Enter a positive whole number'
                            : null;
                      },
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                FilledButton.icon(
                  onPressed: _submitting ? null : _submit,
                  icon: _submitting
                      ? const SizedBox(
                          width: 18,
                          height: 18,
                          child: CircularProgressIndicator(strokeWidth: 2),
                        )
                      : const Icon(Icons.add_rounded),
                  label: Text(_submitting ? 'Creating…' : 'Create table'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  String? _positiveInt(String? v) {
    if (v == null || v.trim().isEmpty) return 'Required';
    final n = int.tryParse(v);
    return (n == null || n <= 0) ? 'Enter a positive whole number' : null;
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    FocusScope.of(context).unfocus();
    setState(() => _submitting = true);

    final request = CreateTableDTO(
      name: _name.text.trim(),
      gameType: _gameType,
      minPlayers: int.parse(_minPlayers.text),
      maxPlayers: int.parse(_maxPlayers.text),
      minBuyin: int.parse(_minBuyin.text).toString(),
      maxBuyin: int.parse(_maxBuyin.text).toString(),
    );
    final speed = double.tryParse(_speed.text.trim());
    if (speed != null) request.speedMultiplier = speed;
    final rounds = int.tryParse(_rounds.text.trim());
    if (rounds != null) request.totalRounds = rounds;

    final messenger = ScaffoldMessenger.of(context);
    try {
      final created = await ref.read(tableListProvider.notifier).create(request);
      if (!mounted) return;
      messenger
        ..clearSnackBars()
        ..showSnackBar(
          SnackBar(content: Text('Created “${created.name}”.')),
        );
      context.pop();
    } on ApiException catch (e) {
      if (!mounted) return;
      setState(() => _submitting = false);
      messenger
        ..clearSnackBars()
        ..showSnackBar(SnackBar(content: Text(e.message)));
    }
  }
}

class _Section extends StatelessWidget {
  const _Section({required this.title, required this.children});

  final String title;
  final List<Widget> children;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 14),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(4, 4, 4, 10),
            child: Text(
              title.toUpperCase(),
              style: const TextStyle(
                color: AppColors.gold,
                fontWeight: FontWeight.w800,
                letterSpacing: 0.8,
                fontSize: 12,
              ),
            ),
          ),
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(children: children),
            ),
          ),
        ],
      ),
    );
  }
}

class _Field extends StatelessWidget {
  const _Field({
    required this.controller,
    required this.label,
    this.validator,
    this.keyboardType,
    this.textInputAction,
    this.integer = false,
  });

  final TextEditingController controller;
  final String label;
  final String? Function(String?)? validator;
  final TextInputType? keyboardType;
  final TextInputAction? textInputAction;
  final bool integer;

  @override
  Widget build(BuildContext context) {
    return TextFormField(
      controller: controller,
      keyboardType: keyboardType,
      textInputAction: textInputAction,
      validator: validator,
      inputFormatters:
          integer ? [FilteringTextInputFormatter.digitsOnly] : null,
      decoration: InputDecoration(
        labelText: label,
        border: const OutlineInputBorder(),
      ),
    );
  }
}

class _GameTypeSelector extends StatelessWidget {
  const _GameTypeSelector({required this.selected, required this.onChanged});

  final GameType selected;
  final ValueChanged<GameType> onChanged;

  @override
  Widget build(BuildContext context) {
    return Wrap(
      spacing: 10,
      children: [
        for (final type in GameDisplay.playableGameTypes)
          ChoiceChip(
            avatar: Icon(
              GameDisplay.gameTypeIcon(type),
              size: 18,
              color: selected == type ? AppColors.feltDark : AppColors.gold,
            ),
            label: Text(GameDisplay.gameType(type)),
            selected: selected == type,
            onSelected: (_) => onChanged(type),
          ),
      ],
    );
  }
}
