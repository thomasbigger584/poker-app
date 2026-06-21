import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/network/api_exception.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/util/money.dart';
import '../../../../core/widgets/felt_background.dart';
import '../user_providers.dart';

/// Manage funds — deposit / withdraw / reset. The backend exposes deposit and
/// withdraw endpoints (`AppUserResource`) that the Android client never surfaced
/// (it only had reset); this screen adds them.
class FundsPage extends ConsumerStatefulWidget {
  const FundsPage({super.key});

  @override
  ConsumerState<FundsPage> createState() => _FundsPageState();
}

class _FundsPageState extends ConsumerState<FundsPage> {
  final _amount = TextEditingController();
  bool _busy = false;

  @override
  void dispose() {
    _amount.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final userAsync = ref.watch(currentUserProvider);

    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: AppBar(title: const Text('Funds')),
      body: FeltBackground(
        child: SafeArea(
          child: ListView(
            padding: const EdgeInsets.fromLTRB(16, 12, 16, 28),
            children: [
              _BalanceCard(
                funds: userAsync.value?.totalFunds,
                loading: userAsync.isLoading,
              ),
              const SizedBox(height: 20),
              Text('Amount', style: Theme.of(context).textTheme.labelLarge),
              const SizedBox(height: 8),
              TextField(
                controller: _amount,
                keyboardType: TextInputType.number,
                inputFormatters: [FilteringTextInputFormatter.digitsOnly],
                decoration: const InputDecoration(
                  prefixIcon: Icon(Icons.toll_rounded),
                  hintText: 'e.g. 5000',
                  border: OutlineInputBorder(),
                ),
              ),
              const SizedBox(height: 16),
              Row(
                children: [
                  Expanded(
                    child: FilledButton.icon(
                      onPressed: _busy ? null : () => _run(_Action.deposit),
                      icon: const Icon(Icons.add_rounded),
                      label: const Text('Deposit'),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: FilledButton.tonalIcon(
                      onPressed: _busy ? null : () => _run(_Action.withdraw),
                      icon: const Icon(Icons.remove_rounded),
                      label: const Text('Withdraw'),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              TextButton.icon(
                onPressed: _busy ? null : () => _run(_Action.reset),
                icon: const Icon(Icons.restart_alt_rounded),
                label: const Text('Reset to starting balance'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _run(_Action action) async {
    final messenger = ScaffoldMessenger.of(context);
    final controller = ref.read(currentUserProvider.notifier);

    if (action != _Action.reset) {
      final amount = int.tryParse(_amount.text.trim()) ?? 0;
      if (amount <= 0) {
        messenger
          ..clearSnackBars()
          ..showSnackBar(const SnackBar(content: Text('Enter an amount first.')));
        return;
      }
    }

    setState(() => _busy = true);
    FocusScope.of(context).unfocus();
    try {
      final amount = _amount.text.trim();
      final user = switch (action) {
        _Action.deposit => await controller.deposit(amount),
        _Action.withdraw => await controller.withdraw(amount),
        _Action.reset => await controller.resetFunds(),
      };
      if (!mounted) return;
      _amount.clear();
      messenger
        ..clearSnackBars()
        ..showSnackBar(SnackBar(
          content: Text('${action.pastTense} — balance '
              '${Money.compact(user.totalFunds)} chips.'),
        ));
    } on ApiException catch (e) {
      if (!mounted) return;
      messenger
        ..clearSnackBars()
        ..showSnackBar(SnackBar(content: Text(e.message)));
    } finally {
      if (mounted) setState(() => _busy = false);
    }
  }
}

enum _Action {
  deposit('Deposited'),
  withdraw('Withdrew'),
  reset('Funds reset');

  const _Action(this.pastTense);
  final String pastTense;
}

class _BalanceCard extends StatelessWidget {
  const _BalanceCard({required this.funds, required this.loading});

  final String? funds;
  final bool loading;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 22),
        child: Column(
          children: [
            const Text(
              'CURRENT BALANCE',
              style: TextStyle(
                color: AppColors.textSecondary,
                letterSpacing: 1.2,
                fontWeight: FontWeight.w700,
                fontSize: 12,
              ),
            ),
            const SizedBox(height: 12),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Icon(Icons.toll_rounded, color: AppColors.gold, size: 30),
                const SizedBox(width: 10),
                if (loading && funds == null)
                  const SizedBox(
                    width: 24,
                    height: 24,
                    child: CircularProgressIndicator(strokeWidth: 2),
                  )
                else
                  Text(
                    Money.compact(funds),
                    style: const TextStyle(
                      color: AppColors.textPrimary,
                      fontWeight: FontWeight.w900,
                      fontSize: 34,
                    ),
                  ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
