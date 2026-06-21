import '../../../core/network/proto_api.dart';
import '../../../core/proto/gen/poker/domain.pb.dart';
import '../../../core/proto/gen/poker/rest.pb.dart';

/// REST access to the current user's profile and funds (`AppUserResource`).
class AppUserRepository {
  AppUserRepository(this._api);

  final ProtoApi _api;

  /// GET /api/app-user/current
  Future<AppUserDTO> current() =>
      _api.getMessage('/app-user/current', parse: AppUserDTO.fromBuffer);

  /// POST /api/app-user/reset-funds
  Future<AppUserDTO> resetFunds() =>
      _api.postMessage('/app-user/reset-funds', parse: AppUserDTO.fromBuffer);

  /// POST /api/app-user/deposit
  Future<AppUserDTO> deposit(String amount) => _api.postMessage(
        '/app-user/deposit',
        body: UserAmountDTO(amount: amount),
        parse: AppUserDTO.fromBuffer,
      );

  /// POST /api/app-user/withdraw
  Future<AppUserDTO> withdraw(String amount) => _api.postMessage(
        '/app-user/withdraw',
        body: UserAmountDTO(amount: amount),
        parse: AppUserDTO.fromBuffer,
      );
}
