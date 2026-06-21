import 'package:flutter/foundation.dart';

/// What is currently preventing app usage, if anything.
enum GateBlocker { offline, tailscale }

/// State of the pre-conditions gate (internet → Tailscale), evaluated in the
/// same order as the Android `BaseNetworkActivity`.
@immutable
class GateState {
  const GateState({this.checking = true, this.blocker});

  /// True during the very first evaluation (before we know anything).
  final bool checking;

  /// The active blocker, or null when all checks pass.
  final GateBlocker? blocker;

  bool get isBlocked => blocker != null;

  GateState copyWith({bool? checking, GateBlocker? blocker, bool clearBlocker = false}) {
    return GateState(
      checking: checking ?? this.checking,
      blocker: clearBlocker ? null : (blocker ?? this.blocker),
    );
  }
}
