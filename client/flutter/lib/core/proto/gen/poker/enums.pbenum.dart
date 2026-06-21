// This is a generated file - do not edit.
//
// Generated from poker/enums.proto.

// @dart = 3.3

// ignore_for_file: annotate_overrides, camel_case_types, comment_references
// ignore_for_file: constant_identifier_names
// ignore_for_file: curly_braces_in_flow_control_structures
// ignore_for_file: deprecated_member_use_from_same_package, library_prefixes
// ignore_for_file: non_constant_identifier_names, prefer_relative_imports

import 'dart:core' as $core;

import 'package:protobuf/protobuf.dart' as $pb;

class GameType extends $pb.ProtobufEnum {
  static const GameType GAME_TYPE_UNSPECIFIED =
      GameType._(0, _omitEnumNames ? '' : 'GAME_TYPE_UNSPECIFIED');
  static const GameType GAME_TYPE_TEXAS_HOLDEM =
      GameType._(1, _omitEnumNames ? '' : 'GAME_TYPE_TEXAS_HOLDEM');
  static const GameType GAME_TYPE_BLACKJACK =
      GameType._(2, _omitEnumNames ? '' : 'GAME_TYPE_BLACKJACK');

  static const $core.List<GameType> values = <GameType>[
    GAME_TYPE_UNSPECIFIED,
    GAME_TYPE_TEXAS_HOLDEM,
    GAME_TYPE_BLACKJACK,
  ];

  static final $core.List<GameType?> _byValue =
      $pb.ProtobufEnum.$_initByValueList(values, 2);
  static GameType? valueOf($core.int value) =>
      value < 0 || value >= _byValue.length ? null : _byValue[value];

  const GameType._(super.value, super.name);
}

class ConnectionType extends $pb.ProtobufEnum {
  static const ConnectionType CONNECTION_TYPE_UNSPECIFIED =
      ConnectionType._(0, _omitEnumNames ? '' : 'CONNECTION_TYPE_UNSPECIFIED');
  static const ConnectionType CONNECTION_TYPE_PLAYER =
      ConnectionType._(1, _omitEnumNames ? '' : 'CONNECTION_TYPE_PLAYER');
  static const ConnectionType CONNECTION_TYPE_LISTENER =
      ConnectionType._(2, _omitEnumNames ? '' : 'CONNECTION_TYPE_LISTENER');

  static const $core.List<ConnectionType> values = <ConnectionType>[
    CONNECTION_TYPE_UNSPECIFIED,
    CONNECTION_TYPE_PLAYER,
    CONNECTION_TYPE_LISTENER,
  ];

  static final $core.List<ConnectionType?> _byValue =
      $pb.ProtobufEnum.$_initByValueList(values, 2);
  static ConnectionType? valueOf($core.int value) =>
      value < 0 || value >= _byValue.length ? null : _byValue[value];

  const ConnectionType._(super.value, super.name);
}

class SessionState extends $pb.ProtobufEnum {
  static const SessionState SESSION_STATE_UNSPECIFIED =
      SessionState._(0, _omitEnumNames ? '' : 'SESSION_STATE_UNSPECIFIED');
  static const SessionState SESSION_STATE_CONNECTED =
      SessionState._(1, _omitEnumNames ? '' : 'SESSION_STATE_CONNECTED');
  static const SessionState SESSION_STATE_DISCONNECTED =
      SessionState._(2, _omitEnumNames ? '' : 'SESSION_STATE_DISCONNECTED');

  static const $core.List<SessionState> values = <SessionState>[
    SESSION_STATE_UNSPECIFIED,
    SESSION_STATE_CONNECTED,
    SESSION_STATE_DISCONNECTED,
  ];

  static final $core.List<SessionState?> _byValue =
      $pb.ProtobufEnum.$_initByValueList(values, 2);
  static SessionState? valueOf($core.int value) =>
      value < 0 || value >= _byValue.length ? null : _byValue[value];

  const SessionState._(super.value, super.name);
}

class TransactionHistoryType extends $pb.ProtobufEnum {
  static const TransactionHistoryType TRANSACTION_HISTORY_TYPE_UNSPECIFIED =
      TransactionHistoryType._(
          0, _omitEnumNames ? '' : 'TRANSACTION_HISTORY_TYPE_UNSPECIFIED');
  static const TransactionHistoryType TRANSACTION_HISTORY_TYPE_DEPOSIT =
      TransactionHistoryType._(
          1, _omitEnumNames ? '' : 'TRANSACTION_HISTORY_TYPE_DEPOSIT');
  static const TransactionHistoryType TRANSACTION_HISTORY_TYPE_WITHDRAW =
      TransactionHistoryType._(
          2, _omitEnumNames ? '' : 'TRANSACTION_HISTORY_TYPE_WITHDRAW');
  static const TransactionHistoryType TRANSACTION_HISTORY_TYPE_BUYIN =
      TransactionHistoryType._(
          3, _omitEnumNames ? '' : 'TRANSACTION_HISTORY_TYPE_BUYIN');
  static const TransactionHistoryType TRANSACTION_HISTORY_TYPE_CASHOUT =
      TransactionHistoryType._(
          4, _omitEnumNames ? '' : 'TRANSACTION_HISTORY_TYPE_CASHOUT');
  static const TransactionHistoryType TRANSACTION_HISTORY_TYPE_RESET =
      TransactionHistoryType._(
          5, _omitEnumNames ? '' : 'TRANSACTION_HISTORY_TYPE_RESET');

  static const $core.List<TransactionHistoryType> values =
      <TransactionHistoryType>[
    TRANSACTION_HISTORY_TYPE_UNSPECIFIED,
    TRANSACTION_HISTORY_TYPE_DEPOSIT,
    TRANSACTION_HISTORY_TYPE_WITHDRAW,
    TRANSACTION_HISTORY_TYPE_BUYIN,
    TRANSACTION_HISTORY_TYPE_CASHOUT,
    TRANSACTION_HISTORY_TYPE_RESET,
  ];

  static final $core.List<TransactionHistoryType?> _byValue =
      $pb.ProtobufEnum.$_initByValueList(values, 5);
  static TransactionHistoryType? valueOf($core.int value) =>
      value < 0 || value >= _byValue.length ? null : _byValue[value];

  const TransactionHistoryType._(super.value, super.name);
}

class CardType extends $pb.ProtobufEnum {
  static const CardType CARD_TYPE_UNSPECIFIED =
      CardType._(0, _omitEnumNames ? '' : 'CARD_TYPE_UNSPECIFIED');
  static const CardType CARD_TYPE_PLAYER_CARD_1 =
      CardType._(1, _omitEnumNames ? '' : 'CARD_TYPE_PLAYER_CARD_1');
  static const CardType CARD_TYPE_PLAYER_CARD_2 =
      CardType._(2, _omitEnumNames ? '' : 'CARD_TYPE_PLAYER_CARD_2');
  static const CardType CARD_TYPE_FLOP_CARD_1 =
      CardType._(3, _omitEnumNames ? '' : 'CARD_TYPE_FLOP_CARD_1');
  static const CardType CARD_TYPE_FLOP_CARD_2 =
      CardType._(4, _omitEnumNames ? '' : 'CARD_TYPE_FLOP_CARD_2');
  static const CardType CARD_TYPE_FLOP_CARD_3 =
      CardType._(5, _omitEnumNames ? '' : 'CARD_TYPE_FLOP_CARD_3');
  static const CardType CARD_TYPE_TURN_CARD =
      CardType._(6, _omitEnumNames ? '' : 'CARD_TYPE_TURN_CARD');
  static const CardType CARD_TYPE_RIVER_CARD =
      CardType._(7, _omitEnumNames ? '' : 'CARD_TYPE_RIVER_CARD');

  static const $core.List<CardType> values = <CardType>[
    CARD_TYPE_UNSPECIFIED,
    CARD_TYPE_PLAYER_CARD_1,
    CARD_TYPE_PLAYER_CARD_2,
    CARD_TYPE_FLOP_CARD_1,
    CARD_TYPE_FLOP_CARD_2,
    CARD_TYPE_FLOP_CARD_3,
    CARD_TYPE_TURN_CARD,
    CARD_TYPE_RIVER_CARD,
  ];

  static final $core.List<CardType?> _byValue =
      $pb.ProtobufEnum.$_initByValueList(values, 7);
  static CardType? valueOf($core.int value) =>
      value < 0 || value >= _byValue.length ? null : _byValue[value];

  const CardType._(super.value, super.name);
}

class RankType extends $pb.ProtobufEnum {
  static const RankType RANK_TYPE_UNSPECIFIED =
      RankType._(0, _omitEnumNames ? '' : 'RANK_TYPE_UNSPECIFIED');
  static const RankType RANK_TYPE_DEUCE =
      RankType._(1, _omitEnumNames ? '' : 'RANK_TYPE_DEUCE');
  static const RankType RANK_TYPE_TREY =
      RankType._(2, _omitEnumNames ? '' : 'RANK_TYPE_TREY');
  static const RankType RANK_TYPE_FOUR =
      RankType._(3, _omitEnumNames ? '' : 'RANK_TYPE_FOUR');
  static const RankType RANK_TYPE_FIVE =
      RankType._(4, _omitEnumNames ? '' : 'RANK_TYPE_FIVE');
  static const RankType RANK_TYPE_SIX =
      RankType._(5, _omitEnumNames ? '' : 'RANK_TYPE_SIX');
  static const RankType RANK_TYPE_SEVEN =
      RankType._(6, _omitEnumNames ? '' : 'RANK_TYPE_SEVEN');
  static const RankType RANK_TYPE_EIGHT =
      RankType._(7, _omitEnumNames ? '' : 'RANK_TYPE_EIGHT');
  static const RankType RANK_TYPE_NINE =
      RankType._(8, _omitEnumNames ? '' : 'RANK_TYPE_NINE');
  static const RankType RANK_TYPE_TEN =
      RankType._(9, _omitEnumNames ? '' : 'RANK_TYPE_TEN');
  static const RankType RANK_TYPE_JACK =
      RankType._(10, _omitEnumNames ? '' : 'RANK_TYPE_JACK');
  static const RankType RANK_TYPE_QUEEN =
      RankType._(11, _omitEnumNames ? '' : 'RANK_TYPE_QUEEN');
  static const RankType RANK_TYPE_KING =
      RankType._(12, _omitEnumNames ? '' : 'RANK_TYPE_KING');
  static const RankType RANK_TYPE_ACE =
      RankType._(13, _omitEnumNames ? '' : 'RANK_TYPE_ACE');

  static const $core.List<RankType> values = <RankType>[
    RANK_TYPE_UNSPECIFIED,
    RANK_TYPE_DEUCE,
    RANK_TYPE_TREY,
    RANK_TYPE_FOUR,
    RANK_TYPE_FIVE,
    RANK_TYPE_SIX,
    RANK_TYPE_SEVEN,
    RANK_TYPE_EIGHT,
    RANK_TYPE_NINE,
    RANK_TYPE_TEN,
    RANK_TYPE_JACK,
    RANK_TYPE_QUEEN,
    RANK_TYPE_KING,
    RANK_TYPE_ACE,
  ];

  static final $core.List<RankType?> _byValue =
      $pb.ProtobufEnum.$_initByValueList(values, 13);
  static RankType? valueOf($core.int value) =>
      value < 0 || value >= _byValue.length ? null : _byValue[value];

  const RankType._(super.value, super.name);
}

class SuitType extends $pb.ProtobufEnum {
  static const SuitType SUIT_TYPE_UNSPECIFIED =
      SuitType._(0, _omitEnumNames ? '' : 'SUIT_TYPE_UNSPECIFIED');
  static const SuitType SUIT_TYPE_CLUBS =
      SuitType._(1, _omitEnumNames ? '' : 'SUIT_TYPE_CLUBS');
  static const SuitType SUIT_TYPE_DIAMONDS =
      SuitType._(2, _omitEnumNames ? '' : 'SUIT_TYPE_DIAMONDS');
  static const SuitType SUIT_TYPE_HEARTS =
      SuitType._(3, _omitEnumNames ? '' : 'SUIT_TYPE_HEARTS');
  static const SuitType SUIT_TYPE_SPADES =
      SuitType._(4, _omitEnumNames ? '' : 'SUIT_TYPE_SPADES');

  static const $core.List<SuitType> values = <SuitType>[
    SUIT_TYPE_UNSPECIFIED,
    SUIT_TYPE_CLUBS,
    SUIT_TYPE_DIAMONDS,
    SUIT_TYPE_HEARTS,
    SUIT_TYPE_SPADES,
  ];

  static final $core.List<SuitType?> _byValue =
      $pb.ProtobufEnum.$_initByValueList(values, 4);
  static SuitType? valueOf($core.int value) =>
      value < 0 || value >= _byValue.length ? null : _byValue[value];

  const SuitType._(super.value, super.name);
}

class HandType extends $pb.ProtobufEnum {
  static const HandType HAND_TYPE_UNSPECIFIED =
      HandType._(0, _omitEnumNames ? '' : 'HAND_TYPE_UNSPECIFIED');
  static const HandType HAND_TYPE_ROYAL_FLUSH =
      HandType._(1, _omitEnumNames ? '' : 'HAND_TYPE_ROYAL_FLUSH');
  static const HandType HAND_TYPE_STRAIGHT_FLUSH =
      HandType._(2, _omitEnumNames ? '' : 'HAND_TYPE_STRAIGHT_FLUSH');
  static const HandType HAND_TYPE_FOUR_OF_A_KIND =
      HandType._(3, _omitEnumNames ? '' : 'HAND_TYPE_FOUR_OF_A_KIND');
  static const HandType HAND_TYPE_FULL_HOUSE =
      HandType._(4, _omitEnumNames ? '' : 'HAND_TYPE_FULL_HOUSE');
  static const HandType HAND_TYPE_FLUSH =
      HandType._(5, _omitEnumNames ? '' : 'HAND_TYPE_FLUSH');
  static const HandType HAND_TYPE_STRAIGHT =
      HandType._(6, _omitEnumNames ? '' : 'HAND_TYPE_STRAIGHT');
  static const HandType HAND_TYPE_THREE_OF_A_KIND =
      HandType._(7, _omitEnumNames ? '' : 'HAND_TYPE_THREE_OF_A_KIND');
  static const HandType HAND_TYPE_TWO_PAIR =
      HandType._(8, _omitEnumNames ? '' : 'HAND_TYPE_TWO_PAIR');
  static const HandType HAND_TYPE_PAIR =
      HandType._(9, _omitEnumNames ? '' : 'HAND_TYPE_PAIR');
  static const HandType HAND_TYPE_HIGH_CARD =
      HandType._(10, _omitEnumNames ? '' : 'HAND_TYPE_HIGH_CARD');
  static const HandType HAND_TYPE_EMPTY_HAND =
      HandType._(11, _omitEnumNames ? '' : 'HAND_TYPE_EMPTY_HAND');

  static const $core.List<HandType> values = <HandType>[
    HAND_TYPE_UNSPECIFIED,
    HAND_TYPE_ROYAL_FLUSH,
    HAND_TYPE_STRAIGHT_FLUSH,
    HAND_TYPE_FOUR_OF_A_KIND,
    HAND_TYPE_FULL_HOUSE,
    HAND_TYPE_FLUSH,
    HAND_TYPE_STRAIGHT,
    HAND_TYPE_THREE_OF_A_KIND,
    HAND_TYPE_TWO_PAIR,
    HAND_TYPE_PAIR,
    HAND_TYPE_HIGH_CARD,
    HAND_TYPE_EMPTY_HAND,
  ];

  static final $core.List<HandType?> _byValue =
      $pb.ProtobufEnum.$_initByValueList(values, 11);
  static HandType? valueOf($core.int value) =>
      value < 0 || value >= _byValue.length ? null : _byValue[value];

  const HandType._(super.value, super.name);
}

class BettingRoundType extends $pb.ProtobufEnum {
  static const BettingRoundType BETTING_ROUND_TYPE_UNSPECIFIED =
      BettingRoundType._(
          0, _omitEnumNames ? '' : 'BETTING_ROUND_TYPE_UNSPECIFIED');
  static const BettingRoundType BETTING_ROUND_TYPE_DEAL =
      BettingRoundType._(1, _omitEnumNames ? '' : 'BETTING_ROUND_TYPE_DEAL');
  static const BettingRoundType BETTING_ROUND_TYPE_FLOP =
      BettingRoundType._(2, _omitEnumNames ? '' : 'BETTING_ROUND_TYPE_FLOP');
  static const BettingRoundType BETTING_ROUND_TYPE_TURN =
      BettingRoundType._(3, _omitEnumNames ? '' : 'BETTING_ROUND_TYPE_TURN');
  static const BettingRoundType BETTING_ROUND_TYPE_RIVER =
      BettingRoundType._(4, _omitEnumNames ? '' : 'BETTING_ROUND_TYPE_RIVER');

  static const $core.List<BettingRoundType> values = <BettingRoundType>[
    BETTING_ROUND_TYPE_UNSPECIFIED,
    BETTING_ROUND_TYPE_DEAL,
    BETTING_ROUND_TYPE_FLOP,
    BETTING_ROUND_TYPE_TURN,
    BETTING_ROUND_TYPE_RIVER,
  ];

  static final $core.List<BettingRoundType?> _byValue =
      $pb.ProtobufEnum.$_initByValueList(values, 4);
  static BettingRoundType? valueOf($core.int value) =>
      value < 0 || value >= _byValue.length ? null : _byValue[value];

  const BettingRoundType._(super.value, super.name);
}

class BettingRoundState extends $pb.ProtobufEnum {
  static const BettingRoundState BETTING_ROUND_STATE_UNSPECIFIED =
      BettingRoundState._(
          0, _omitEnumNames ? '' : 'BETTING_ROUND_STATE_UNSPECIFIED');
  static const BettingRoundState BETTING_ROUND_STATE_IN_PROGRESS =
      BettingRoundState._(
          1, _omitEnumNames ? '' : 'BETTING_ROUND_STATE_IN_PROGRESS');
  static const BettingRoundState BETTING_ROUND_STATE_FINISHED =
      BettingRoundState._(
          2, _omitEnumNames ? '' : 'BETTING_ROUND_STATE_FINISHED');
  static const BettingRoundState BETTING_ROUND_STATE_FAILED =
      BettingRoundState._(
          3, _omitEnumNames ? '' : 'BETTING_ROUND_STATE_FAILED');

  static const $core.List<BettingRoundState> values = <BettingRoundState>[
    BETTING_ROUND_STATE_UNSPECIFIED,
    BETTING_ROUND_STATE_IN_PROGRESS,
    BETTING_ROUND_STATE_FINISHED,
    BETTING_ROUND_STATE_FAILED,
  ];

  static final $core.List<BettingRoundState?> _byValue =
      $pb.ProtobufEnum.$_initByValueList(values, 3);
  static BettingRoundState? valueOf($core.int value) =>
      value < 0 || value >= _byValue.length ? null : _byValue[value];

  const BettingRoundState._(super.value, super.name);
}

class RoundState extends $pb.ProtobufEnum {
  static const RoundState ROUND_STATE_UNSPECIFIED =
      RoundState._(0, _omitEnumNames ? '' : 'ROUND_STATE_UNSPECIFIED');
  static const RoundState ROUND_STATE_WAITING_FOR_PLAYERS =
      RoundState._(1, _omitEnumNames ? '' : 'ROUND_STATE_WAITING_FOR_PLAYERS');
  static const RoundState ROUND_STATE_INIT_DEAL =
      RoundState._(2, _omitEnumNames ? '' : 'ROUND_STATE_INIT_DEAL');
  static const RoundState ROUND_STATE_INIT_DEAL_BET =
      RoundState._(3, _omitEnumNames ? '' : 'ROUND_STATE_INIT_DEAL_BET');
  static const RoundState ROUND_STATE_FLOP_DEAL =
      RoundState._(4, _omitEnumNames ? '' : 'ROUND_STATE_FLOP_DEAL');
  static const RoundState ROUND_STATE_FLOP_DEAL_BET =
      RoundState._(5, _omitEnumNames ? '' : 'ROUND_STATE_FLOP_DEAL_BET');
  static const RoundState ROUND_STATE_TURN_DEAL =
      RoundState._(6, _omitEnumNames ? '' : 'ROUND_STATE_TURN_DEAL');
  static const RoundState ROUND_STATE_TURN_DEAL_BET =
      RoundState._(7, _omitEnumNames ? '' : 'ROUND_STATE_TURN_DEAL_BET');
  static const RoundState ROUND_STATE_RIVER_DEAL =
      RoundState._(8, _omitEnumNames ? '' : 'ROUND_STATE_RIVER_DEAL');
  static const RoundState ROUND_STATE_RIVER_DEAL_BET =
      RoundState._(9, _omitEnumNames ? '' : 'ROUND_STATE_RIVER_DEAL_BET');
  static const RoundState ROUND_STATE_EVAL =
      RoundState._(10, _omitEnumNames ? '' : 'ROUND_STATE_EVAL');
  static const RoundState ROUND_STATE_FINISHED =
      RoundState._(11, _omitEnumNames ? '' : 'ROUND_STATE_FINISHED');
  static const RoundState ROUND_STATE_FAILED =
      RoundState._(12, _omitEnumNames ? '' : 'ROUND_STATE_FAILED');

  static const $core.List<RoundState> values = <RoundState>[
    ROUND_STATE_UNSPECIFIED,
    ROUND_STATE_WAITING_FOR_PLAYERS,
    ROUND_STATE_INIT_DEAL,
    ROUND_STATE_INIT_DEAL_BET,
    ROUND_STATE_FLOP_DEAL,
    ROUND_STATE_FLOP_DEAL_BET,
    ROUND_STATE_TURN_DEAL,
    ROUND_STATE_TURN_DEAL_BET,
    ROUND_STATE_RIVER_DEAL,
    ROUND_STATE_RIVER_DEAL_BET,
    ROUND_STATE_EVAL,
    ROUND_STATE_FINISHED,
    ROUND_STATE_FAILED,
  ];

  static final $core.List<RoundState?> _byValue =
      $pb.ProtobufEnum.$_initByValueList(values, 12);
  static RoundState? valueOf($core.int value) =>
      value < 0 || value >= _byValue.length ? null : _byValue[value];

  const RoundState._(super.value, super.name);
}

class ActionType extends $pb.ProtobufEnum {
  static const ActionType ACTION_TYPE_UNSPECIFIED =
      ActionType._(0, _omitEnumNames ? '' : 'ACTION_TYPE_UNSPECIFIED');
  static const ActionType ACTION_TYPE_CHECK =
      ActionType._(1, _omitEnumNames ? '' : 'ACTION_TYPE_CHECK');
  static const ActionType ACTION_TYPE_BET =
      ActionType._(2, _omitEnumNames ? '' : 'ACTION_TYPE_BET');
  static const ActionType ACTION_TYPE_CALL =
      ActionType._(3, _omitEnumNames ? '' : 'ACTION_TYPE_CALL');
  static const ActionType ACTION_TYPE_RAISE =
      ActionType._(4, _omitEnumNames ? '' : 'ACTION_TYPE_RAISE');
  static const ActionType ACTION_TYPE_FOLD =
      ActionType._(5, _omitEnumNames ? '' : 'ACTION_TYPE_FOLD');
  static const ActionType ACTION_TYPE_ALL_IN =
      ActionType._(6, _omitEnumNames ? '' : 'ACTION_TYPE_ALL_IN');

  static const $core.List<ActionType> values = <ActionType>[
    ACTION_TYPE_UNSPECIFIED,
    ACTION_TYPE_CHECK,
    ACTION_TYPE_BET,
    ACTION_TYPE_CALL,
    ACTION_TYPE_RAISE,
    ACTION_TYPE_FOLD,
    ACTION_TYPE_ALL_IN,
  ];

  static final $core.List<ActionType?> _byValue =
      $pb.ProtobufEnum.$_initByValueList(values, 6);
  static ActionType? valueOf($core.int value) =>
      value < 0 || value >= _byValue.length ? null : _byValue[value];

  const ActionType._(super.value, super.name);
}

const $core.bool _omitEnumNames =
    $core.bool.fromEnvironment('protobuf.omit_enum_names');
