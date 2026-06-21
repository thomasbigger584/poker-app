import 'package:flutter/material.dart';

import '../proto/gen/poker/enums.pb.dart';

/// Presentation helpers for the proto enums — display labels and icons — so the
/// UI never shows the raw prefixed proto names (e.g. `GAME_TYPE_TEXAS_HOLDEM`).
abstract final class GameDisplay {
  static String gameType(GameType type) => switch (type) {
        GameType.GAME_TYPE_TEXAS_HOLDEM => "Texas Hold'em",
        GameType.GAME_TYPE_BLACKJACK => 'Blackjack',
        _ => 'Unknown',
      };

  static IconData gameTypeIcon(GameType type) => switch (type) {
        GameType.GAME_TYPE_TEXAS_HOLDEM => Icons.style_rounded,
        GameType.GAME_TYPE_BLACKJACK => Icons.casino_rounded,
        _ => Icons.help_outline_rounded,
      };

  /// Game types a user can actually create / play from this client.
  static const List<GameType> playableGameTypes = [
    GameType.GAME_TYPE_TEXAS_HOLDEM,
    GameType.GAME_TYPE_BLACKJACK,
  ];

  static String connectionType(ConnectionType type) => switch (type) {
        ConnectionType.CONNECTION_TYPE_PLAYER => 'Player',
        ConnectionType.CONNECTION_TYPE_LISTENER => 'Viewer',
        _ => 'Unknown',
      };

  static String handType(HandType type) => switch (type) {
        HandType.HAND_TYPE_ROYAL_FLUSH => 'Royal Flush',
        HandType.HAND_TYPE_STRAIGHT_FLUSH => 'Straight Flush',
        HandType.HAND_TYPE_FOUR_OF_A_KIND => 'Four of a Kind',
        HandType.HAND_TYPE_FULL_HOUSE => 'Full House',
        HandType.HAND_TYPE_FLUSH => 'Flush',
        HandType.HAND_TYPE_STRAIGHT => 'Straight',
        HandType.HAND_TYPE_THREE_OF_A_KIND => 'Three of a Kind',
        HandType.HAND_TYPE_TWO_PAIR => 'Two Pair',
        HandType.HAND_TYPE_PAIR => 'Pair',
        HandType.HAND_TYPE_HIGH_CARD => 'High Card',
        _ => '—',
      };

  static String actionType(ActionType type) => switch (type) {
        ActionType.ACTION_TYPE_CHECK => 'Check',
        ActionType.ACTION_TYPE_BET => 'Bet',
        ActionType.ACTION_TYPE_CALL => 'Call',
        ActionType.ACTION_TYPE_RAISE => 'Raise',
        ActionType.ACTION_TYPE_FOLD => 'Fold',
        ActionType.ACTION_TYPE_ALL_IN => 'All-in',
        _ => '—',
      };
}

/// Presentation helpers for transaction history rows.
abstract final class TransactionDisplay {
  static String label(TransactionHistoryType type) => switch (type) {
        TransactionHistoryType.TRANSACTION_HISTORY_TYPE_DEPOSIT => 'Deposit',
        TransactionHistoryType.TRANSACTION_HISTORY_TYPE_WITHDRAW => 'Withdrawal',
        TransactionHistoryType.TRANSACTION_HISTORY_TYPE_BUYIN => 'Table buy-in',
        TransactionHistoryType.TRANSACTION_HISTORY_TYPE_CASHOUT => 'Table cash-out',
        TransactionHistoryType.TRANSACTION_HISTORY_TYPE_RESET => 'Funds reset',
        _ => 'Transaction',
      };

  static IconData icon(TransactionHistoryType type) => switch (type) {
        TransactionHistoryType.TRANSACTION_HISTORY_TYPE_DEPOSIT =>
          Icons.south_west_rounded,
        TransactionHistoryType.TRANSACTION_HISTORY_TYPE_WITHDRAW =>
          Icons.north_east_rounded,
        TransactionHistoryType.TRANSACTION_HISTORY_TYPE_BUYIN =>
          Icons.login_rounded,
        TransactionHistoryType.TRANSACTION_HISTORY_TYPE_CASHOUT =>
          Icons.logout_rounded,
        TransactionHistoryType.TRANSACTION_HISTORY_TYPE_RESET =>
          Icons.restart_alt_rounded,
        _ => Icons.swap_horiz_rounded,
      };

  /// True when the transaction increases the user's balance (shown green / +).
  static bool isCredit(TransactionHistoryType type) => switch (type) {
        TransactionHistoryType.TRANSACTION_HISTORY_TYPE_DEPOSIT ||
        TransactionHistoryType.TRANSACTION_HISTORY_TYPE_CASHOUT ||
        TransactionHistoryType.TRANSACTION_HISTORY_TYPE_RESET =>
          true,
        _ => false,
      };
}
