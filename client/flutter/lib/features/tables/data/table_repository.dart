import '../../../core/network/proto_api.dart';
import '../../../core/proto/gen/poker/domain.pb.dart';
import '../../../core/proto/gen/poker/rest.pb.dart';

/// REST access to the poker tables (`TableResource`).
class TableRepository {
  TableRepository(this._api);

  final ProtoApi _api;

  /// GET /api/poker-table — available tables with per-user connection state.
  Future<List<AvailableTableDTO>> available() async {
    final response = await _api.getMessage(
      '/poker-table',
      parse: AvailableTableListResponse.fromBuffer,
    );
    return response.tables;
  }

  /// POST /api/poker-table — create a new table, returns the created table.
  Future<TableDTO> create(CreateTableDTO request) => _api.postMessage(
        '/poker-table',
        body: request,
        parse: TableDTO.fromBuffer,
      );
}
