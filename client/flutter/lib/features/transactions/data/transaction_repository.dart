import '../../../core/network/proto_api.dart';
import '../../../core/proto/gen/poker/domain.pb.dart';
import '../../../core/proto/gen/poker/rest.pb.dart';

/// View modes for the transaction history list, mirroring the Android spinner.
/// The backend `type` param selects the full ledger vs. a simplified view that
/// collapses related entries (see `TransactionHistoryService.findCurrent`).
enum TransactionView {
  all('ALL', 'All'),
  simplified('ALL_SIMPLIFIED', 'Simplified');

  const TransactionView(this.param, this.label);

  /// Value sent as the `?type=` query parameter.
  final String param;

  /// Human-readable label for the filter control.
  final String label;
}

/// REST access to the current user's transaction history
/// (`TransactionHistoryResource`).
class TransactionRepository {
  TransactionRepository(this._api);

  final ProtoApi _api;

  /// GET /api/transaction-history/current?type=...
  Future<List<TransactionHistoryDTO>> current(TransactionView view) async {
    final response = await _api.getMessage(
      '/transaction-history/current',
      query: {'type': view.param},
      parse: TransactionHistoryListResponse.fromBuffer,
    );
    return response.transactions;
  }
}
