import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/proto/gen/poker/rest.pb.dart';
import '../../auth/presentation/auth_providers.dart';
import '../data/stats_repository.dart';

final statsRepositoryProvider = Provider<StatsRepository>(
  (ref) => StatsRepository(ref.watch(protoApiProvider)),
);

/// The current user's lifetime statistics. Refetched when invalidated
/// (pull-to-refresh on the stats screen).
final playerStatsProvider = FutureProvider<PlayerStatsDTO>(
  (ref) => ref.watch(statsRepositoryProvider).current(),
);
