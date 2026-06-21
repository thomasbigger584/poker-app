import '../../../core/network/proto_api.dart';
import '../../../core/proto/gen/poker/rest.pb.dart';

/// REST access to the current user's aggregated statistics (`StatsResource`).
class StatsRepository {
  StatsRepository(this._api);

  final ProtoApi _api;

  /// GET /api/stats/current
  Future<PlayerStatsDTO> current() async {
    final response = await _api.getMessage(
      '/stats/current',
      parse: PlayerStatsResponse.fromBuffer,
    );
    return response.stats;
  }
}
