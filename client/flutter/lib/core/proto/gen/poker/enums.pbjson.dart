// This is a generated file - do not edit.
//
// Generated from poker/enums.proto.

// @dart = 3.3

// ignore_for_file: annotate_overrides, camel_case_types, comment_references
// ignore_for_file: constant_identifier_names
// ignore_for_file: curly_braces_in_flow_control_structures
// ignore_for_file: deprecated_member_use_from_same_package, library_prefixes
// ignore_for_file: non_constant_identifier_names, prefer_relative_imports
// ignore_for_file: unused_import

import 'dart:convert' as $convert;
import 'dart:core' as $core;
import 'dart:typed_data' as $typed_data;

@$core.Deprecated('Use gameTypeDescriptor instead')
const GameType$json = {
  '1': 'GameType',
  '2': [
    {'1': 'GAME_TYPE_UNSPECIFIED', '2': 0},
    {'1': 'GAME_TYPE_TEXAS_HOLDEM', '2': 1},
    {'1': 'GAME_TYPE_BLACKJACK', '2': 2},
  ],
};

/// Descriptor for `GameType`. Decode as a `google.protobuf.EnumDescriptorProto`.
final $typed_data.Uint8List gameTypeDescriptor = $convert.base64Decode(
    'CghHYW1lVHlwZRIZChVHQU1FX1RZUEVfVU5TUEVDSUZJRUQQABIaChZHQU1FX1RZUEVfVEVYQV'
    'NfSE9MREVNEAESFwoTR0FNRV9UWVBFX0JMQUNLSkFDSxAC');

@$core.Deprecated('Use connectionTypeDescriptor instead')
const ConnectionType$json = {
  '1': 'ConnectionType',
  '2': [
    {'1': 'CONNECTION_TYPE_UNSPECIFIED', '2': 0},
    {'1': 'CONNECTION_TYPE_PLAYER', '2': 1},
    {'1': 'CONNECTION_TYPE_LISTENER', '2': 2},
  ],
};

/// Descriptor for `ConnectionType`. Decode as a `google.protobuf.EnumDescriptorProto`.
final $typed_data.Uint8List connectionTypeDescriptor = $convert.base64Decode(
    'Cg5Db25uZWN0aW9uVHlwZRIfChtDT05ORUNUSU9OX1RZUEVfVU5TUEVDSUZJRUQQABIaChZDT0'
    '5ORUNUSU9OX1RZUEVfUExBWUVSEAESHAoYQ09OTkVDVElPTl9UWVBFX0xJU1RFTkVSEAI=');

@$core.Deprecated('Use sessionStateDescriptor instead')
const SessionState$json = {
  '1': 'SessionState',
  '2': [
    {'1': 'SESSION_STATE_UNSPECIFIED', '2': 0},
    {'1': 'SESSION_STATE_CONNECTED', '2': 1},
    {'1': 'SESSION_STATE_DISCONNECTED', '2': 2},
  ],
};

/// Descriptor for `SessionState`. Decode as a `google.protobuf.EnumDescriptorProto`.
final $typed_data.Uint8List sessionStateDescriptor = $convert.base64Decode(
    'CgxTZXNzaW9uU3RhdGUSHQoZU0VTU0lPTl9TVEFURV9VTlNQRUNJRklFRBAAEhsKF1NFU1NJT0'
    '5fU1RBVEVfQ09OTkVDVEVEEAESHgoaU0VTU0lPTl9TVEFURV9ESVNDT05ORUNURUQQAg==');

@$core.Deprecated('Use transactionHistoryTypeDescriptor instead')
const TransactionHistoryType$json = {
  '1': 'TransactionHistoryType',
  '2': [
    {'1': 'TRANSACTION_HISTORY_TYPE_UNSPECIFIED', '2': 0},
    {'1': 'TRANSACTION_HISTORY_TYPE_DEPOSIT', '2': 1},
    {'1': 'TRANSACTION_HISTORY_TYPE_WITHDRAW', '2': 2},
    {'1': 'TRANSACTION_HISTORY_TYPE_BUYIN', '2': 3},
    {'1': 'TRANSACTION_HISTORY_TYPE_CASHOUT', '2': 4},
    {'1': 'TRANSACTION_HISTORY_TYPE_RESET', '2': 5},
  ],
};

/// Descriptor for `TransactionHistoryType`. Decode as a `google.protobuf.EnumDescriptorProto`.
final $typed_data.Uint8List transactionHistoryTypeDescriptor = $convert.base64Decode(
    'ChZUcmFuc2FjdGlvbkhpc3RvcnlUeXBlEigKJFRSQU5TQUNUSU9OX0hJU1RPUllfVFlQRV9VTl'
    'NQRUNJRklFRBAAEiQKIFRSQU5TQUNUSU9OX0hJU1RPUllfVFlQRV9ERVBPU0lUEAESJQohVFJB'
    'TlNBQ1RJT05fSElTVE9SWV9UWVBFX1dJVEhEUkFXEAISIgoeVFJBTlNBQ1RJT05fSElTVE9SWV'
    '9UWVBFX0JVWUlOEAMSJAogVFJBTlNBQ1RJT05fSElTVE9SWV9UWVBFX0NBU0hPVVQQBBIiCh5U'
    'UkFOU0FDVElPTl9ISVNUT1JZX1RZUEVfUkVTRVQQBQ==');

@$core.Deprecated('Use cardTypeDescriptor instead')
const CardType$json = {
  '1': 'CardType',
  '2': [
    {'1': 'CARD_TYPE_UNSPECIFIED', '2': 0},
    {'1': 'CARD_TYPE_PLAYER_CARD_1', '2': 1},
    {'1': 'CARD_TYPE_PLAYER_CARD_2', '2': 2},
    {'1': 'CARD_TYPE_FLOP_CARD_1', '2': 3},
    {'1': 'CARD_TYPE_FLOP_CARD_2', '2': 4},
    {'1': 'CARD_TYPE_FLOP_CARD_3', '2': 5},
    {'1': 'CARD_TYPE_TURN_CARD', '2': 6},
    {'1': 'CARD_TYPE_RIVER_CARD', '2': 7},
  ],
};

/// Descriptor for `CardType`. Decode as a `google.protobuf.EnumDescriptorProto`.
final $typed_data.Uint8List cardTypeDescriptor = $convert.base64Decode(
    'CghDYXJkVHlwZRIZChVDQVJEX1RZUEVfVU5TUEVDSUZJRUQQABIbChdDQVJEX1RZUEVfUExBWU'
    'VSX0NBUkRfMRABEhsKF0NBUkRfVFlQRV9QTEFZRVJfQ0FSRF8yEAISGQoVQ0FSRF9UWVBFX0ZM'
    'T1BfQ0FSRF8xEAMSGQoVQ0FSRF9UWVBFX0ZMT1BfQ0FSRF8yEAQSGQoVQ0FSRF9UWVBFX0ZMT1'
    'BfQ0FSRF8zEAUSFwoTQ0FSRF9UWVBFX1RVUk5fQ0FSRBAGEhgKFENBUkRfVFlQRV9SSVZFUl9D'
    'QVJEEAc=');

@$core.Deprecated('Use rankTypeDescriptor instead')
const RankType$json = {
  '1': 'RankType',
  '2': [
    {'1': 'RANK_TYPE_UNSPECIFIED', '2': 0},
    {'1': 'RANK_TYPE_DEUCE', '2': 1},
    {'1': 'RANK_TYPE_TREY', '2': 2},
    {'1': 'RANK_TYPE_FOUR', '2': 3},
    {'1': 'RANK_TYPE_FIVE', '2': 4},
    {'1': 'RANK_TYPE_SIX', '2': 5},
    {'1': 'RANK_TYPE_SEVEN', '2': 6},
    {'1': 'RANK_TYPE_EIGHT', '2': 7},
    {'1': 'RANK_TYPE_NINE', '2': 8},
    {'1': 'RANK_TYPE_TEN', '2': 9},
    {'1': 'RANK_TYPE_JACK', '2': 10},
    {'1': 'RANK_TYPE_QUEEN', '2': 11},
    {'1': 'RANK_TYPE_KING', '2': 12},
    {'1': 'RANK_TYPE_ACE', '2': 13},
  ],
};

/// Descriptor for `RankType`. Decode as a `google.protobuf.EnumDescriptorProto`.
final $typed_data.Uint8List rankTypeDescriptor = $convert.base64Decode(
    'CghSYW5rVHlwZRIZChVSQU5LX1RZUEVfVU5TUEVDSUZJRUQQABITCg9SQU5LX1RZUEVfREVVQ0'
    'UQARISCg5SQU5LX1RZUEVfVFJFWRACEhIKDlJBTktfVFlQRV9GT1VSEAMSEgoOUkFOS19UWVBF'
    'X0ZJVkUQBBIRCg1SQU5LX1RZUEVfU0lYEAUSEwoPUkFOS19UWVBFX1NFVkVOEAYSEwoPUkFOS1'
    '9UWVBFX0VJR0hUEAcSEgoOUkFOS19UWVBFX05JTkUQCBIRCg1SQU5LX1RZUEVfVEVOEAkSEgoO'
    'UkFOS19UWVBFX0pBQ0sQChITCg9SQU5LX1RZUEVfUVVFRU4QCxISCg5SQU5LX1RZUEVfS0lORx'
    'AMEhEKDVJBTktfVFlQRV9BQ0UQDQ==');

@$core.Deprecated('Use suitTypeDescriptor instead')
const SuitType$json = {
  '1': 'SuitType',
  '2': [
    {'1': 'SUIT_TYPE_UNSPECIFIED', '2': 0},
    {'1': 'SUIT_TYPE_CLUBS', '2': 1},
    {'1': 'SUIT_TYPE_DIAMONDS', '2': 2},
    {'1': 'SUIT_TYPE_HEARTS', '2': 3},
    {'1': 'SUIT_TYPE_SPADES', '2': 4},
  ],
};

/// Descriptor for `SuitType`. Decode as a `google.protobuf.EnumDescriptorProto`.
final $typed_data.Uint8List suitTypeDescriptor = $convert.base64Decode(
    'CghTdWl0VHlwZRIZChVTVUlUX1RZUEVfVU5TUEVDSUZJRUQQABITCg9TVUlUX1RZUEVfQ0xVQl'
    'MQARIWChJTVUlUX1RZUEVfRElBTU9ORFMQAhIUChBTVUlUX1RZUEVfSEVBUlRTEAMSFAoQU1VJ'
    'VF9UWVBFX1NQQURFUxAE');

@$core.Deprecated('Use handTypeDescriptor instead')
const HandType$json = {
  '1': 'HandType',
  '2': [
    {'1': 'HAND_TYPE_UNSPECIFIED', '2': 0},
    {'1': 'HAND_TYPE_ROYAL_FLUSH', '2': 1},
    {'1': 'HAND_TYPE_STRAIGHT_FLUSH', '2': 2},
    {'1': 'HAND_TYPE_FOUR_OF_A_KIND', '2': 3},
    {'1': 'HAND_TYPE_FULL_HOUSE', '2': 4},
    {'1': 'HAND_TYPE_FLUSH', '2': 5},
    {'1': 'HAND_TYPE_STRAIGHT', '2': 6},
    {'1': 'HAND_TYPE_THREE_OF_A_KIND', '2': 7},
    {'1': 'HAND_TYPE_TWO_PAIR', '2': 8},
    {'1': 'HAND_TYPE_PAIR', '2': 9},
    {'1': 'HAND_TYPE_HIGH_CARD', '2': 10},
    {'1': 'HAND_TYPE_EMPTY_HAND', '2': 11},
  ],
};

/// Descriptor for `HandType`. Decode as a `google.protobuf.EnumDescriptorProto`.
final $typed_data.Uint8List handTypeDescriptor = $convert.base64Decode(
    'CghIYW5kVHlwZRIZChVIQU5EX1RZUEVfVU5TUEVDSUZJRUQQABIZChVIQU5EX1RZUEVfUk9ZQU'
    'xfRkxVU0gQARIcChhIQU5EX1RZUEVfU1RSQUlHSFRfRkxVU0gQAhIcChhIQU5EX1RZUEVfRk9V'
    'Ul9PRl9BX0tJTkQQAxIYChRIQU5EX1RZUEVfRlVMTF9IT1VTRRAEEhMKD0hBTkRfVFlQRV9GTF'
    'VTSBAFEhYKEkhBTkRfVFlQRV9TVFJBSUdIVBAGEh0KGUhBTkRfVFlQRV9USFJFRV9PRl9BX0tJ'
    'TkQQBxIWChJIQU5EX1RZUEVfVFdPX1BBSVIQCBISCg5IQU5EX1RZUEVfUEFJUhAJEhcKE0hBTk'
    'RfVFlQRV9ISUdIX0NBUkQQChIYChRIQU5EX1RZUEVfRU1QVFlfSEFORBAL');

@$core.Deprecated('Use bettingRoundTypeDescriptor instead')
const BettingRoundType$json = {
  '1': 'BettingRoundType',
  '2': [
    {'1': 'BETTING_ROUND_TYPE_UNSPECIFIED', '2': 0},
    {'1': 'BETTING_ROUND_TYPE_DEAL', '2': 1},
    {'1': 'BETTING_ROUND_TYPE_FLOP', '2': 2},
    {'1': 'BETTING_ROUND_TYPE_TURN', '2': 3},
    {'1': 'BETTING_ROUND_TYPE_RIVER', '2': 4},
  ],
};

/// Descriptor for `BettingRoundType`. Decode as a `google.protobuf.EnumDescriptorProto`.
final $typed_data.Uint8List bettingRoundTypeDescriptor = $convert.base64Decode(
    'ChBCZXR0aW5nUm91bmRUeXBlEiIKHkJFVFRJTkdfUk9VTkRfVFlQRV9VTlNQRUNJRklFRBAAEh'
    'sKF0JFVFRJTkdfUk9VTkRfVFlQRV9ERUFMEAESGwoXQkVUVElOR19ST1VORF9UWVBFX0ZMT1AQ'
    'AhIbChdCRVRUSU5HX1JPVU5EX1RZUEVfVFVSThADEhwKGEJFVFRJTkdfUk9VTkRfVFlQRV9SSV'
    'ZFUhAE');

@$core.Deprecated('Use bettingRoundStateDescriptor instead')
const BettingRoundState$json = {
  '1': 'BettingRoundState',
  '2': [
    {'1': 'BETTING_ROUND_STATE_UNSPECIFIED', '2': 0},
    {'1': 'BETTING_ROUND_STATE_IN_PROGRESS', '2': 1},
    {'1': 'BETTING_ROUND_STATE_FINISHED', '2': 2},
    {'1': 'BETTING_ROUND_STATE_FAILED', '2': 3},
  ],
};

/// Descriptor for `BettingRoundState`. Decode as a `google.protobuf.EnumDescriptorProto`.
final $typed_data.Uint8List bettingRoundStateDescriptor = $convert.base64Decode(
    'ChFCZXR0aW5nUm91bmRTdGF0ZRIjCh9CRVRUSU5HX1JPVU5EX1NUQVRFX1VOU1BFQ0lGSUVEEA'
    'ASIwofQkVUVElOR19ST1VORF9TVEFURV9JTl9QUk9HUkVTUxABEiAKHEJFVFRJTkdfUk9VTkRf'
    'U1RBVEVfRklOSVNIRUQQAhIeChpCRVRUSU5HX1JPVU5EX1NUQVRFX0ZBSUxFRBAD');

@$core.Deprecated('Use roundStateDescriptor instead')
const RoundState$json = {
  '1': 'RoundState',
  '2': [
    {'1': 'ROUND_STATE_UNSPECIFIED', '2': 0},
    {'1': 'ROUND_STATE_WAITING_FOR_PLAYERS', '2': 1},
    {'1': 'ROUND_STATE_INIT_DEAL', '2': 2},
    {'1': 'ROUND_STATE_INIT_DEAL_BET', '2': 3},
    {'1': 'ROUND_STATE_FLOP_DEAL', '2': 4},
    {'1': 'ROUND_STATE_FLOP_DEAL_BET', '2': 5},
    {'1': 'ROUND_STATE_TURN_DEAL', '2': 6},
    {'1': 'ROUND_STATE_TURN_DEAL_BET', '2': 7},
    {'1': 'ROUND_STATE_RIVER_DEAL', '2': 8},
    {'1': 'ROUND_STATE_RIVER_DEAL_BET', '2': 9},
    {'1': 'ROUND_STATE_EVAL', '2': 10},
    {'1': 'ROUND_STATE_FINISHED', '2': 11},
    {'1': 'ROUND_STATE_FAILED', '2': 12},
  ],
};

/// Descriptor for `RoundState`. Decode as a `google.protobuf.EnumDescriptorProto`.
final $typed_data.Uint8List roundStateDescriptor = $convert.base64Decode(
    'CgpSb3VuZFN0YXRlEhsKF1JPVU5EX1NUQVRFX1VOU1BFQ0lGSUVEEAASIwofUk9VTkRfU1RBVE'
    'VfV0FJVElOR19GT1JfUExBWUVSUxABEhkKFVJPVU5EX1NUQVRFX0lOSVRfREVBTBACEh0KGVJP'
    'VU5EX1NUQVRFX0lOSVRfREVBTF9CRVQQAxIZChVST1VORF9TVEFURV9GTE9QX0RFQUwQBBIdCh'
    'lST1VORF9TVEFURV9GTE9QX0RFQUxfQkVUEAUSGQoVUk9VTkRfU1RBVEVfVFVSTl9ERUFMEAYS'
    'HQoZUk9VTkRfU1RBVEVfVFVSTl9ERUFMX0JFVBAHEhoKFlJPVU5EX1NUQVRFX1JJVkVSX0RFQU'
    'wQCBIeChpST1VORF9TVEFURV9SSVZFUl9ERUFMX0JFVBAJEhQKEFJPVU5EX1NUQVRFX0VWQUwQ'
    'ChIYChRST1VORF9TVEFURV9GSU5JU0hFRBALEhYKElJPVU5EX1NUQVRFX0ZBSUxFRBAM');

@$core.Deprecated('Use actionTypeDescriptor instead')
const ActionType$json = {
  '1': 'ActionType',
  '2': [
    {'1': 'ACTION_TYPE_UNSPECIFIED', '2': 0},
    {'1': 'ACTION_TYPE_CHECK', '2': 1},
    {'1': 'ACTION_TYPE_BET', '2': 2},
    {'1': 'ACTION_TYPE_CALL', '2': 3},
    {'1': 'ACTION_TYPE_RAISE', '2': 4},
    {'1': 'ACTION_TYPE_FOLD', '2': 5},
    {'1': 'ACTION_TYPE_ALL_IN', '2': 6},
  ],
};

/// Descriptor for `ActionType`. Decode as a `google.protobuf.EnumDescriptorProto`.
final $typed_data.Uint8List actionTypeDescriptor = $convert.base64Decode(
    'CgpBY3Rpb25UeXBlEhsKF0FDVElPTl9UWVBFX1VOU1BFQ0lGSUVEEAASFQoRQUNUSU9OX1RZUE'
    'VfQ0hFQ0sQARITCg9BQ1RJT05fVFlQRV9CRVQQAhIUChBBQ1RJT05fVFlQRV9DQUxMEAMSFQoR'
    'QUNUSU9OX1RZUEVfUkFJU0UQBBIUChBBQ1RJT05fVFlQRV9GT0xEEAUSFgoSQUNUSU9OX1RZUE'
    'VfQUxMX0lOEAY=');
