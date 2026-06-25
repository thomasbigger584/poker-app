import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/network/api_exception.dart';
import '../../../../core/proto/gen/poker/domain.pb.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/util/game_display.dart';
import '../../../../core/util/money.dart';
import '../../../../core/util/responsive.dart';
import '../../../../core/widgets/felt_background.dart';
import '../../data/transaction_repository.dart';
import '../transactions_providers.dart';

/// Transaction history — ports the Android `TransactionHistoryActivity`. Filter
/// chips replace the spinner; rows are colour-coded credits/debits.
class TransactionHistoryPage extends ConsumerWidget {
  const TransactionHistoryPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final view = ref.watch(transactionViewProvider);
    final historyAsync = ref.watch(transactionHistoryProvider);

    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: AppBar(title: const Text('Transaction History')),
      body: FeltBackground(
        child: SafeArea(
          // Centre + cap the content so chips and rows align and don't stretch
          // edge-to-edge on desktop / web.
          child: Center(
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 1000),
              child: Column(
                children: [
                  Padding(
                    // Top pad clears the transparent (extended) app bar.
                    padding: const EdgeInsets.fromLTRB(16, kToolbarHeight + 8, 16, 4),
                    child: Row(
                      children: [
                        for (final v in TransactionView.values)
                          Padding(
                            padding: const EdgeInsets.only(right: 8),
                            child: ChoiceChip(
                              label: Text(v.label),
                              selected: view == v,
                              onSelected: (_) => ref
                                  .read(transactionViewProvider.notifier)
                                  .select(v),
                            ),
                          ),
                      ],
                    ),
                  ),
                  Expanded(
                    child: RefreshIndicator(
                      onRefresh: () =>
                          ref.refresh(transactionHistoryProvider.future),
                      child: historyAsync.when(
                        skipLoadingOnRefresh: true,
                        data: (items) => _HistoryList(items: items),
                        error: (error, _) => _ErrorState(
                          message: error is ApiException
                              ? error.message
                              : 'Could not load history.',
                          onRetry: () =>
                              ref.invalidate(transactionHistoryProvider),
                        ),
                        loading: () =>
                            const Center(child: CircularProgressIndicator()),
                      ),
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

class _HistoryList extends StatelessWidget {
  const _HistoryList({required this.items});

  final List<TransactionHistoryDTO> items;

  @override
  Widget build(BuildContext context) {
    if (items.isEmpty) {
      return ListView(
        physics: const AlwaysScrollableScrollPhysics(),
        children: [
          SizedBox(height: MediaQuery.of(context).size.height * 0.2),
          const Icon(Icons.receipt_long_rounded,
              size: 60, color: AppColors.gold),
          const SizedBox(height: 14),
          const Text(
            'No transactions yet',
            textAlign: TextAlign.center,
            style: TextStyle(color: AppColors.textPrimary, fontSize: 16),
          ),
        ],
      );
    }

    // One column on phones, two on wider screens — uses the horizontal space
    // instead of stretching each row across the whole window.
    return AdaptiveCardGrid(
      itemCount: items.length,
      maxContentWidth: 1000,
      maxColumns: 2,
      spacing: 10,
      padding: const EdgeInsets.fromLTRB(12, 8, 12, 24),
      itemBuilder: (context, i) => _TransactionTile(item: items[i]),
    );
  }
}

class _TransactionTile extends StatelessWidget {
  const _TransactionTile({required this.item});

  final TransactionHistoryDTO item;

  @override
  Widget build(BuildContext context) {
    final isCredit = TransactionDisplay.isCredit(item.type);
    final color = isCredit ? AppColors.success : AppColors.error;

    return Card(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
        child: Row(
          children: [
            CircleAvatar(
              radius: 20,
              backgroundColor: color.withValues(alpha: 0.16),
              child: Icon(TransactionDisplay.icon(item.type),
                  color: color, size: 20),
            ),
            const SizedBox(width: 14),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    TransactionDisplay.label(item.type),
                    style: const TextStyle(
                      color: AppColors.textPrimary,
                      fontWeight: FontWeight.w700,
                      fontSize: 15,
                    ),
                  ),
                  const SizedBox(height: 2),
                  Text(
                    _formatDate(item.createdDateTime),
                    style: const TextStyle(
                      color: AppColors.textSecondary,
                      fontSize: 12.5,
                    ),
                  ),
                ],
              ),
            ),
            Text(
              Money.signed(item.amount, positive: isCredit),
              style: TextStyle(
                color: color,
                fontWeight: FontWeight.w800,
                fontSize: 15,
              ),
            ),
          ],
        ),
      ),
    );
  }

  /// Trims the ISO-8601 timestamp to a compact "YYYY-MM-DD HH:MM" display
  /// without pulling in `intl`.
  static String _formatDate(String iso) {
    if (iso.length < 16) return iso;
    return '${iso.substring(0, 10)}  ${iso.substring(11, 16)}';
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
