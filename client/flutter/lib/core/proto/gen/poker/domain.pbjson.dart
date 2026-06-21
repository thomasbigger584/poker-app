// This is a generated file - do not edit.
//
// Generated from poker/domain.proto.

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

@$core.Deprecated('Use appUserDTODescriptor instead')
const AppUserDTO$json = {
  '1': 'AppUserDTO',
  '2': [
    {'1': 'id', '3': 1, '4': 1, '5': 9, '10': 'id'},
    {'1': 'username', '3': 2, '4': 1, '5': 9, '10': 'username'},
    {'1': 'first_name', '3': 3, '4': 1, '5': 9, '10': 'firstName'},
    {'1': 'last_name', '3': 4, '4': 1, '5': 9, '10': 'lastName'},
    {'1': 'email', '3': 5, '4': 1, '5': 9, '10': 'email'},
    {'1': 'email_verified', '3': 6, '4': 1, '5': 8, '10': 'emailVerified'},
    {'1': 'enabled', '3': 7, '4': 1, '5': 8, '10': 'enabled'},
    {'1': 'total_funds', '3': 8, '4': 1, '5': 9, '10': 'totalFunds'},
    {'1': 'persona', '3': 9, '4': 1, '5': 9, '10': 'persona'},
  ],
};

/// Descriptor for `AppUserDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List appUserDTODescriptor = $convert.base64Decode(
    'CgpBcHBVc2VyRFRPEg4KAmlkGAEgASgJUgJpZBIaCgh1c2VybmFtZRgCIAEoCVIIdXNlcm5hbW'
    'USHQoKZmlyc3RfbmFtZRgDIAEoCVIJZmlyc3ROYW1lEhsKCWxhc3RfbmFtZRgEIAEoCVIIbGFz'
    'dE5hbWUSFAoFZW1haWwYBSABKAlSBWVtYWlsEiUKDmVtYWlsX3ZlcmlmaWVkGAYgASgIUg1lbW'
    'FpbFZlcmlmaWVkEhgKB2VuYWJsZWQYByABKAhSB2VuYWJsZWQSHwoLdG90YWxfZnVuZHMYCCAB'
    'KAlSCnRvdGFsRnVuZHMSGAoHcGVyc29uYRgJIAEoCVIHcGVyc29uYQ==');

@$core.Deprecated('Use tableDTODescriptor instead')
const TableDTO$json = {
  '1': 'TableDTO',
  '2': [
    {'1': 'id', '3': 1, '4': 1, '5': 9, '10': 'id'},
    {'1': 'name', '3': 2, '4': 1, '5': 9, '10': 'name'},
    {
      '1': 'game_type',
      '3': 3,
      '4': 1,
      '5': 14,
      '6': '.poker.GameType',
      '10': 'gameType'
    },
    {
      '1': 'speed_multiplier',
      '3': 4,
      '4': 1,
      '5': 1,
      '9': 0,
      '10': 'speedMultiplier',
      '17': true
    },
    {
      '1': 'total_rounds',
      '3': 5,
      '4': 1,
      '5': 5,
      '9': 1,
      '10': 'totalRounds',
      '17': true
    },
    {'1': 'min_players', '3': 6, '4': 1, '5': 5, '10': 'minPlayers'},
    {'1': 'max_players', '3': 7, '4': 1, '5': 5, '10': 'maxPlayers'},
    {'1': 'min_buyin', '3': 8, '4': 1, '5': 9, '10': 'minBuyin'},
    {'1': 'max_buyin', '3': 9, '4': 1, '5': 9, '10': 'maxBuyin'},
  ],
  '8': [
    {'1': '_speed_multiplier'},
    {'1': '_total_rounds'},
  ],
};

/// Descriptor for `TableDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List tableDTODescriptor = $convert.base64Decode(
    'CghUYWJsZURUTxIOCgJpZBgBIAEoCVICaWQSEgoEbmFtZRgCIAEoCVIEbmFtZRIsCglnYW1lX3'
    'R5cGUYAyABKA4yDy5wb2tlci5HYW1lVHlwZVIIZ2FtZVR5cGUSLgoQc3BlZWRfbXVsdGlwbGll'
    'chgEIAEoAUgAUg9zcGVlZE11bHRpcGxpZXKIAQESJgoMdG90YWxfcm91bmRzGAUgASgFSAFSC3'
    'RvdGFsUm91bmRziAEBEh8KC21pbl9wbGF5ZXJzGAYgASgFUgptaW5QbGF5ZXJzEh8KC21heF9w'
    'bGF5ZXJzGAcgASgFUgptYXhQbGF5ZXJzEhsKCW1pbl9idXlpbhgIIAEoCVIIbWluQnV5aW4SGw'
    'oJbWF4X2J1eWluGAkgASgJUghtYXhCdXlpbkITChFfc3BlZWRfbXVsdGlwbGllckIPCg1fdG90'
    'YWxfcm91bmRz');

@$core.Deprecated('Use cardDTODescriptor instead')
const CardDTO$json = {
  '1': 'CardDTO',
  '2': [
    {'1': 'id', '3': 1, '4': 1, '5': 9, '10': 'id'},
    {
      '1': 'rank_type',
      '3': 2,
      '4': 1,
      '5': 14,
      '6': '.poker.RankType',
      '10': 'rankType'
    },
    {'1': 'rank_char', '3': 3, '4': 1, '5': 9, '10': 'rankChar'},
    {'1': 'rank_value', '3': 4, '4': 1, '5': 5, '10': 'rankValue'},
    {
      '1': 'suit_type',
      '3': 5,
      '4': 1,
      '5': 14,
      '6': '.poker.SuitType',
      '10': 'suitType'
    },
    {'1': 'suit_char', '3': 6, '4': 1, '5': 9, '10': 'suitChar'},
    {
      '1': 'card_type',
      '3': 7,
      '4': 1,
      '5': 14,
      '6': '.poker.CardType',
      '10': 'cardType'
    },
  ],
};

/// Descriptor for `CardDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List cardDTODescriptor = $convert.base64Decode(
    'CgdDYXJkRFRPEg4KAmlkGAEgASgJUgJpZBIsCglyYW5rX3R5cGUYAiABKA4yDy5wb2tlci5SYW'
    '5rVHlwZVIIcmFua1R5cGUSGwoJcmFua19jaGFyGAMgASgJUghyYW5rQ2hhchIdCgpyYW5rX3Zh'
    'bHVlGAQgASgFUglyYW5rVmFsdWUSLAoJc3VpdF90eXBlGAUgASgOMg8ucG9rZXIuU3VpdFR5cG'
    'VSCHN1aXRUeXBlEhsKCXN1aXRfY2hhchgGIAEoCVIIc3VpdENoYXISLAoJY2FyZF90eXBlGAcg'
    'ASgOMg8ucG9rZXIuQ2FyZFR5cGVSCGNhcmRUeXBl');

@$core.Deprecated('Use handDTODescriptor instead')
const HandDTO$json = {
  '1': 'HandDTO',
  '2': [
    {'1': 'id', '3': 1, '4': 1, '5': 9, '10': 'id'},
    {
      '1': 'hand_type',
      '3': 2,
      '4': 1,
      '5': 14,
      '6': '.poker.HandType',
      '10': 'handType'
    },
    {'1': 'hand_type_str', '3': 3, '4': 1, '5': 9, '10': 'handTypeStr'},
    {
      '1': 'cards',
      '3': 4,
      '4': 3,
      '5': 11,
      '6': '.poker.CardDTO',
      '10': 'cards'
    },
  ],
};

/// Descriptor for `HandDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List handDTODescriptor = $convert.base64Decode(
    'CgdIYW5kRFRPEg4KAmlkGAEgASgJUgJpZBIsCgloYW5kX3R5cGUYAiABKA4yDy5wb2tlci5IYW'
    '5kVHlwZVIIaGFuZFR5cGUSIgoNaGFuZF90eXBlX3N0chgDIAEoCVILaGFuZFR5cGVTdHISJAoF'
    'Y2FyZHMYBCADKAsyDi5wb2tlci5DYXJkRFRPUgVjYXJkcw==');

@$core.Deprecated('Use playerSessionDTODescriptor instead')
const PlayerSessionDTO$json = {
  '1': 'PlayerSessionDTO',
  '2': [
    {'1': 'id', '3': 1, '4': 1, '5': 9, '10': 'id'},
    {
      '1': 'user',
      '3': 2,
      '4': 1,
      '5': 11,
      '6': '.poker.AppUserDTO',
      '10': 'user'
    },
    {
      '1': 'poker_table',
      '3': 3,
      '4': 1,
      '5': 11,
      '6': '.poker.TableDTO',
      '10': 'pokerTable'
    },
    {
      '1': 'position',
      '3': 4,
      '4': 1,
      '5': 5,
      '9': 0,
      '10': 'position',
      '17': true
    },
    {'1': 'dealer', '3': 5, '4': 1, '5': 8, '9': 1, '10': 'dealer', '17': true},
    {'1': 'funds', '3': 6, '4': 1, '5': 9, '10': 'funds'},
    {
      '1': 'session_state',
      '3': 7,
      '4': 1,
      '5': 14,
      '6': '.poker.SessionState',
      '10': 'sessionState'
    },
    {
      '1': 'connection_type',
      '3': 8,
      '4': 1,
      '5': 14,
      '6': '.poker.ConnectionType',
      '10': 'connectionType'
    },
  ],
  '8': [
    {'1': '_position'},
    {'1': '_dealer'},
  ],
};

/// Descriptor for `PlayerSessionDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List playerSessionDTODescriptor = $convert.base64Decode(
    'ChBQbGF5ZXJTZXNzaW9uRFRPEg4KAmlkGAEgASgJUgJpZBIlCgR1c2VyGAIgASgLMhEucG9rZX'
    'IuQXBwVXNlckRUT1IEdXNlchIwCgtwb2tlcl90YWJsZRgDIAEoCzIPLnBva2VyLlRhYmxlRFRP'
    'Ugpwb2tlclRhYmxlEh8KCHBvc2l0aW9uGAQgASgFSABSCHBvc2l0aW9uiAEBEhsKBmRlYWxlch'
    'gFIAEoCEgBUgZkZWFsZXKIAQESFAoFZnVuZHMYBiABKAlSBWZ1bmRzEjgKDXNlc3Npb25fc3Rh'
    'dGUYByABKA4yEy5wb2tlci5TZXNzaW9uU3RhdGVSDHNlc3Npb25TdGF0ZRI+Cg9jb25uZWN0aW'
    '9uX3R5cGUYCCABKA4yFS5wb2tlci5Db25uZWN0aW9uVHlwZVIOY29ubmVjdGlvblR5cGVCCwoJ'
    'X3Bvc2l0aW9uQgkKB19kZWFsZXI=');

@$core.Deprecated('Use roundDTODescriptor instead')
const RoundDTO$json = {
  '1': 'RoundDTO',
  '2': [
    {'1': 'id', '3': 1, '4': 1, '5': 9, '10': 'id'},
    {
      '1': 'round_state',
      '3': 2,
      '4': 1,
      '5': 14,
      '6': '.poker.RoundState',
      '10': 'roundState'
    },
  ],
};

/// Descriptor for `RoundDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List roundDTODescriptor = $convert.base64Decode(
    'CghSb3VuZERUTxIOCgJpZBgBIAEoCVICaWQSMgoLcm91bmRfc3RhdGUYAiABKA4yES5wb2tlci'
    '5Sb3VuZFN0YXRlUgpyb3VuZFN0YXRl');

@$core.Deprecated('Use bettingRoundDTODescriptor instead')
const BettingRoundDTO$json = {
  '1': 'BettingRoundDTO',
  '2': [
    {'1': 'id', '3': 1, '4': 1, '5': 9, '10': 'id'},
    {
      '1': 'type',
      '3': 2,
      '4': 1,
      '5': 14,
      '6': '.poker.BettingRoundType',
      '10': 'type'
    },
    {
      '1': 'state',
      '3': 3,
      '4': 1,
      '5': 14,
      '6': '.poker.BettingRoundState',
      '10': 'state'
    },
    {
      '1': 'betting_round_refunds',
      '3': 4,
      '4': 3,
      '5': 11,
      '6': '.poker.BettingRoundRefundDTO',
      '10': 'bettingRoundRefunds'
    },
  ],
};

/// Descriptor for `BettingRoundDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List bettingRoundDTODescriptor = $convert.base64Decode(
    'Cg9CZXR0aW5nUm91bmREVE8SDgoCaWQYASABKAlSAmlkEisKBHR5cGUYAiABKA4yFy5wb2tlci'
    '5CZXR0aW5nUm91bmRUeXBlUgR0eXBlEi4KBXN0YXRlGAMgASgOMhgucG9rZXIuQmV0dGluZ1Jv'
    'dW5kU3RhdGVSBXN0YXRlElAKFWJldHRpbmdfcm91bmRfcmVmdW5kcxgEIAMoCzIcLnBva2VyLk'
    'JldHRpbmdSb3VuZFJlZnVuZERUT1ITYmV0dGluZ1JvdW5kUmVmdW5kcw==');

@$core.Deprecated('Use bettingRoundRefundDTODescriptor instead')
const BettingRoundRefundDTO$json = {
  '1': 'BettingRoundRefundDTO',
  '2': [
    {'1': 'id', '3': 1, '4': 1, '5': 9, '10': 'id'},
    {
      '1': 'player_session',
      '3': 2,
      '4': 1,
      '5': 11,
      '6': '.poker.PlayerSessionDTO',
      '10': 'playerSession'
    },
    {'1': 'amount', '3': 3, '4': 1, '5': 9, '10': 'amount'},
  ],
};

/// Descriptor for `BettingRoundRefundDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List bettingRoundRefundDTODescriptor = $convert.base64Decode(
    'ChVCZXR0aW5nUm91bmRSZWZ1bmREVE8SDgoCaWQYASABKAlSAmlkEj4KDnBsYXllcl9zZXNzaW'
    '9uGAIgASgLMhcucG9rZXIuUGxheWVyU2Vzc2lvbkRUT1INcGxheWVyU2Vzc2lvbhIWCgZhbW91'
    'bnQYAyABKAlSBmFtb3VudA==');

@$core.Deprecated('Use roundPotDTODescriptor instead')
const RoundPotDTO$json = {
  '1': 'RoundPotDTO',
  '2': [
    {'1': 'id', '3': 1, '4': 1, '5': 9, '10': 'id'},
    {'1': 'pot_amount', '3': 2, '4': 1, '5': 9, '10': 'potAmount'},
    {
      '1': 'pot_index',
      '3': 3,
      '4': 1,
      '5': 5,
      '9': 0,
      '10': 'potIndex',
      '17': true
    },
    {
      '1': 'eligible_players',
      '3': 4,
      '4': 3,
      '5': 11,
      '6': '.poker.PlayerSessionDTO',
      '10': 'eligiblePlayers'
    },
  ],
  '8': [
    {'1': '_pot_index'},
  ],
};

/// Descriptor for `RoundPotDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List roundPotDTODescriptor = $convert.base64Decode(
    'CgtSb3VuZFBvdERUTxIOCgJpZBgBIAEoCVICaWQSHQoKcG90X2Ftb3VudBgCIAEoCVIJcG90QW'
    '1vdW50EiAKCXBvdF9pbmRleBgDIAEoBUgAUghwb3RJbmRleIgBARJCChBlbGlnaWJsZV9wbGF5'
    'ZXJzGAQgAygLMhcucG9rZXIuUGxheWVyU2Vzc2lvbkRUT1IPZWxpZ2libGVQbGF5ZXJzQgwKCl'
    '9wb3RfaW5kZXg=');

@$core.Deprecated('Use playerActionDTODescriptor instead')
const PlayerActionDTO$json = {
  '1': 'PlayerActionDTO',
  '2': [
    {'1': 'id', '3': 1, '4': 1, '5': 9, '10': 'id'},
    {
      '1': 'player_session',
      '3': 2,
      '4': 1,
      '5': 11,
      '6': '.poker.PlayerSessionDTO',
      '10': 'playerSession'
    },
    {
      '1': 'betting_round',
      '3': 3,
      '4': 1,
      '5': 11,
      '6': '.poker.BettingRoundDTO',
      '10': 'bettingRound'
    },
    {
      '1': 'action_type',
      '3': 4,
      '4': 1,
      '5': 14,
      '6': '.poker.ActionType',
      '10': 'actionType'
    },
    {'1': 'amount', '3': 5, '4': 1, '5': 9, '10': 'amount'},
  ],
};

/// Descriptor for `PlayerActionDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List playerActionDTODescriptor = $convert.base64Decode(
    'Cg9QbGF5ZXJBY3Rpb25EVE8SDgoCaWQYASABKAlSAmlkEj4KDnBsYXllcl9zZXNzaW9uGAIgAS'
    'gLMhcucG9rZXIuUGxheWVyU2Vzc2lvbkRUT1INcGxheWVyU2Vzc2lvbhI7Cg1iZXR0aW5nX3Jv'
    'dW5kGAMgASgLMhYucG9rZXIuQmV0dGluZ1JvdW5kRFRPUgxiZXR0aW5nUm91bmQSMgoLYWN0aW'
    '9uX3R5cGUYBCABKA4yES5wb2tlci5BY3Rpb25UeXBlUgphY3Rpb25UeXBlEhYKBmFtb3VudBgF'
    'IAEoCVIGYW1vdW50');

@$core.Deprecated('Use roundWinnerDTODescriptor instead')
const RoundWinnerDTO$json = {
  '1': 'RoundWinnerDTO',
  '2': [
    {'1': 'id', '3': 1, '4': 1, '5': 9, '10': 'id'},
    {
      '1': 'player_session',
      '3': 2,
      '4': 1,
      '5': 11,
      '6': '.poker.PlayerSessionDTO',
      '10': 'playerSession'
    },
    {
      '1': 'round',
      '3': 3,
      '4': 1,
      '5': 11,
      '6': '.poker.RoundDTO',
      '10': 'round'
    },
    {'1': 'hand', '3': 4, '4': 1, '5': 11, '6': '.poker.HandDTO', '10': 'hand'},
    {'1': 'amount', '3': 5, '4': 1, '5': 9, '10': 'amount'},
  ],
};

/// Descriptor for `RoundWinnerDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List roundWinnerDTODescriptor = $convert.base64Decode(
    'Cg5Sb3VuZFdpbm5lckRUTxIOCgJpZBgBIAEoCVICaWQSPgoOcGxheWVyX3Nlc3Npb24YAiABKA'
    'syFy5wb2tlci5QbGF5ZXJTZXNzaW9uRFRPUg1wbGF5ZXJTZXNzaW9uEiUKBXJvdW5kGAMgASgL'
    'Mg8ucG9rZXIuUm91bmREVE9SBXJvdW5kEiIKBGhhbmQYBCABKAsyDi5wb2tlci5IYW5kRFRPUg'
    'RoYW5kEhYKBmFtb3VudBgFIAEoCVIGYW1vdW50');

@$core.Deprecated('Use transactionHistoryDTODescriptor instead')
const TransactionHistoryDTO$json = {
  '1': 'TransactionHistoryDTO',
  '2': [
    {'1': 'id', '3': 1, '4': 1, '5': 9, '10': 'id'},
    {'1': 'amount', '3': 2, '4': 1, '5': 9, '10': 'amount'},
    {
      '1': 'type',
      '3': 3,
      '4': 1,
      '5': 14,
      '6': '.poker.TransactionHistoryType',
      '10': 'type'
    },
    {'1': 'created_date_time', '3': 4, '4': 1, '5': 9, '10': 'createdDateTime'},
  ],
};

/// Descriptor for `TransactionHistoryDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List transactionHistoryDTODescriptor = $convert.base64Decode(
    'ChVUcmFuc2FjdGlvbkhpc3RvcnlEVE8SDgoCaWQYASABKAlSAmlkEhYKBmFtb3VudBgCIAEoCV'
    'IGYW1vdW50EjEKBHR5cGUYAyABKA4yHS5wb2tlci5UcmFuc2FjdGlvbkhpc3RvcnlUeXBlUgR0'
    'eXBlEioKEWNyZWF0ZWRfZGF0ZV90aW1lGAQgASgJUg9jcmVhdGVkRGF0ZVRpbWU=');
