import '../../../core/proto/gen/poker/domain.pb.dart';
import '../../../core/proto/gen/poker/enums.pb.dart';

/// Everything needed to enter a table, passed to the game route via go_router
/// `extra`. Mirrors the Android `TexasGameActivity.startActivity(...)` extras
/// (table, connection type, buy-in, reconnect).
class GameLaunchArgs {
  const GameLaunchArgs({
    required this.table,
    required this.connectionType,
    required this.buyInAmount,
    this.reconnect = false,
  });

  final TableDTO table;
  final ConnectionType connectionType;

  /// Buy-in as a BigDecimal string ("0" for viewers).
  final String buyInAmount;

  /// True when rejoining an existing session (skips the buy-in screen).
  final bool reconnect;
}
