import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/proto/gen/poker/domain.pb.dart';
import '../../auth/presentation/auth_providers.dart';
import '../data/transaction_repository.dart';

final transactionRepositoryProvider = Provider<TransactionRepository>(
  (ref) => TransactionRepository(ref.watch(protoApiProvider)),
);

/// Currently-selected history view (All vs Simplified).
final transactionViewProvider =
    NotifierProvider<TransactionViewController, TransactionView>(
  TransactionViewController.new,
);

class TransactionViewController extends Notifier<TransactionView> {
  @override
  TransactionView build() => TransactionView.all;

  void select(TransactionView view) => state = view;
}

/// Transaction history for the selected view. Re-fetches when the view changes.
final transactionHistoryProvider =
    FutureProvider<List<TransactionHistoryDTO>>((ref) {
  final view = ref.watch(transactionViewProvider);
  return ref.watch(transactionRepositoryProvider).current(view);
});
