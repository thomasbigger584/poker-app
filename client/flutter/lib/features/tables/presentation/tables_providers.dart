import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/proto/gen/poker/domain.pb.dart';
import '../../../core/proto/gen/poker/rest.pb.dart';
import '../../auth/presentation/auth_providers.dart';
import '../data/table_repository.dart';

final tableRepositoryProvider = Provider<TableRepository>(
  (ref) => TableRepository(ref.watch(protoApiProvider)),
);

/// The lobby list of available tables. Refreshable for pull-to-refresh.
final tableListProvider =
    AsyncNotifierProvider<TableListController, List<AvailableTableDTO>>(
  TableListController.new,
);

class TableListController extends AsyncNotifier<List<AvailableTableDTO>> {
  TableRepository get _repo => ref.read(tableRepositoryProvider);

  @override
  Future<List<AvailableTableDTO>> build() => _repo.available();

  /// Reloads the list without flashing the spinner over existing content
  /// (keeps the current rows visible while the request is in flight).
  Future<void> refresh() async {
    state = await AsyncValue.guard(_repo.available);
  }

  /// Creates a table then refreshes the list so it appears immediately.
  Future<TableDTO> create(CreateTableDTO request) async {
    final created = await _repo.create(request);
    await refresh();
    return created;
  }
}
