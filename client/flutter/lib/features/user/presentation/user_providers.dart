import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/proto/gen/poker/domain.pb.dart';
import '../../auth/presentation/auth_providers.dart';
import '../data/app_user_repository.dart';

final appUserRepositoryProvider = Provider<AppUserRepository>(
  (ref) => AppUserRepository(ref.watch(protoApiProvider)),
);

/// The signed-in user's profile + funds. Shared by the drawer header, the funds
/// screen and anywhere the balance is shown; mutating actions update its state
/// so every listener refreshes at once.
final currentUserProvider =
    AsyncNotifierProvider<CurrentUserController, AppUserDTO>(
  CurrentUserController.new,
);

class CurrentUserController extends AsyncNotifier<AppUserDTO> {
  AppUserRepository get _repo => ref.read(appUserRepositoryProvider);

  @override
  Future<AppUserDTO> build() => _repo.current();

  Future<void> refresh() async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(_repo.current);
  }

  Future<AppUserDTO> resetFunds() => _apply(_repo.resetFunds);

  Future<AppUserDTO> deposit(String amount) => _apply(() => _repo.deposit(amount));

  Future<AppUserDTO> withdraw(String amount) =>
      _apply(() => _repo.withdraw(amount));

  /// Runs a funds mutation and publishes the returned profile to all listeners.
  /// Rethrows so the caller can surface validation/errors; leaves state intact.
  Future<AppUserDTO> _apply(Future<AppUserDTO> Function() action) async {
    final updated = await action();
    state = AsyncValue.data(updated);
    return updated;
  }
}
