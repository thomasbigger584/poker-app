// This is a generated file - do not edit.
//
// Generated from poker/websocket.proto.

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

@$core.Deprecated('Use serverMessageDTODescriptor instead')
const ServerMessageDTO$json = {
  '1': 'ServerMessageDTO',
  '2': [
    {'1': 'timestamp', '3': 1, '4': 1, '5': 3, '10': 'timestamp'},
    {
      '1': 'player_subscribed',
      '3': 2,
      '4': 1,
      '5': 11,
      '6': '.poker.PlayerSubscribedDTO',
      '9': 0,
      '10': 'playerSubscribed'
    },
    {
      '1': 'player_connected',
      '3': 3,
      '4': 1,
      '5': 11,
      '6': '.poker.PlayerConnectedDTO',
      '9': 0,
      '10': 'playerConnected'
    },
    {
      '1': 'dealer_determined',
      '3': 4,
      '4': 1,
      '5': 11,
      '6': '.poker.DealerDeterminedDTO',
      '9': 0,
      '10': 'dealerDetermined'
    },
    {
      '1': 'deal_init',
      '3': 5,
      '4': 1,
      '5': 11,
      '6': '.poker.DealPlayerCardDTO',
      '9': 0,
      '10': 'dealInit'
    },
    {
      '1': 'deal_community',
      '3': 6,
      '4': 1,
      '5': 11,
      '6': '.poker.DealCommunityCardDTO',
      '9': 0,
      '10': 'dealCommunity'
    },
    {
      '1': 'player_turn',
      '3': 7,
      '4': 1,
      '5': 11,
      '6': '.poker.PlayerTurnDTO',
      '9': 0,
      '10': 'playerTurn'
    },
    {
      '1': 'player_actioned',
      '3': 8,
      '4': 1,
      '5': 11,
      '6': '.poker.PlayerActionedDTO',
      '9': 0,
      '10': 'playerActioned'
    },
    {
      '1': 'betting_round_updated',
      '3': 9,
      '4': 1,
      '5': 11,
      '6': '.poker.BettingRoundUpdatedDTO',
      '9': 0,
      '10': 'bettingRoundUpdated'
    },
    {
      '1': 'round_finished',
      '3': 10,
      '4': 1,
      '5': 11,
      '6': '.poker.RoundFinishedDTO',
      '9': 0,
      '10': 'roundFinished'
    },
    {
      '1': 'game_finished',
      '3': 11,
      '4': 1,
      '5': 11,
      '6': '.poker.GameFinishedDTO',
      '9': 0,
      '10': 'gameFinished'
    },
    {
      '1': 'chat',
      '3': 12,
      '4': 1,
      '5': 11,
      '6': '.poker.ChatMessageDTO',
      '9': 0,
      '10': 'chat'
    },
    {
      '1': 'log',
      '3': 13,
      '4': 1,
      '5': 11,
      '6': '.poker.LogMessageDTO',
      '9': 0,
      '10': 'log'
    },
    {
      '1': 'error',
      '3': 14,
      '4': 1,
      '5': 11,
      '6': '.poker.ErrorMessageDTO',
      '9': 0,
      '10': 'error'
    },
    {
      '1': 'validation',
      '3': 15,
      '4': 1,
      '5': 11,
      '6': '.poker.ValidationDTO',
      '9': 0,
      '10': 'validation'
    },
    {
      '1': 'player_disconnected',
      '3': 16,
      '4': 1,
      '5': 11,
      '6': '.poker.PlayerDisconnectedDTO',
      '9': 0,
      '10': 'playerDisconnected'
    },
  ],
  '8': [
    {'1': 'payload'},
  ],
};

/// Descriptor for `ServerMessageDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List serverMessageDTODescriptor = $convert.base64Decode(
    'ChBTZXJ2ZXJNZXNzYWdlRFRPEhwKCXRpbWVzdGFtcBgBIAEoA1IJdGltZXN0YW1wEkkKEXBsYX'
    'llcl9zdWJzY3JpYmVkGAIgASgLMhoucG9rZXIuUGxheWVyU3Vic2NyaWJlZERUT0gAUhBwbGF5'
    'ZXJTdWJzY3JpYmVkEkYKEHBsYXllcl9jb25uZWN0ZWQYAyABKAsyGS5wb2tlci5QbGF5ZXJDb2'
    '5uZWN0ZWREVE9IAFIPcGxheWVyQ29ubmVjdGVkEkkKEWRlYWxlcl9kZXRlcm1pbmVkGAQgASgL'
    'MhoucG9rZXIuRGVhbGVyRGV0ZXJtaW5lZERUT0gAUhBkZWFsZXJEZXRlcm1pbmVkEjcKCWRlYW'
    'xfaW5pdBgFIAEoCzIYLnBva2VyLkRlYWxQbGF5ZXJDYXJkRFRPSABSCGRlYWxJbml0EkQKDmRl'
    'YWxfY29tbXVuaXR5GAYgASgLMhsucG9rZXIuRGVhbENvbW11bml0eUNhcmREVE9IAFINZGVhbE'
    'NvbW11bml0eRI3CgtwbGF5ZXJfdHVybhgHIAEoCzIULnBva2VyLlBsYXllclR1cm5EVE9IAFIK'
    'cGxheWVyVHVybhJDCg9wbGF5ZXJfYWN0aW9uZWQYCCABKAsyGC5wb2tlci5QbGF5ZXJBY3Rpb2'
    '5lZERUT0gAUg5wbGF5ZXJBY3Rpb25lZBJTChViZXR0aW5nX3JvdW5kX3VwZGF0ZWQYCSABKAsy'
    'HS5wb2tlci5CZXR0aW5nUm91bmRVcGRhdGVkRFRPSABSE2JldHRpbmdSb3VuZFVwZGF0ZWQSQA'
    'oOcm91bmRfZmluaXNoZWQYCiABKAsyFy5wb2tlci5Sb3VuZEZpbmlzaGVkRFRPSABSDXJvdW5k'
    'RmluaXNoZWQSPQoNZ2FtZV9maW5pc2hlZBgLIAEoCzIWLnBva2VyLkdhbWVGaW5pc2hlZERUT0'
    'gAUgxnYW1lRmluaXNoZWQSKwoEY2hhdBgMIAEoCzIVLnBva2VyLkNoYXRNZXNzYWdlRFRPSABS'
    'BGNoYXQSKAoDbG9nGA0gASgLMhQucG9rZXIuTG9nTWVzc2FnZURUT0gAUgNsb2cSLgoFZXJyb3'
    'IYDiABKAsyFi5wb2tlci5FcnJvck1lc3NhZ2VEVE9IAFIFZXJyb3ISNgoKdmFsaWRhdGlvbhgP'
    'IAEoCzIULnBva2VyLlZhbGlkYXRpb25EVE9IAFIKdmFsaWRhdGlvbhJPChNwbGF5ZXJfZGlzY2'
    '9ubmVjdGVkGBAgASgLMhwucG9rZXIuUGxheWVyRGlzY29ubmVjdGVkRFRPSABSEnBsYXllckRp'
    'c2Nvbm5lY3RlZEIJCgdwYXlsb2Fk');

@$core.Deprecated('Use playerSubscribedDTODescriptor instead')
const PlayerSubscribedDTO$json = {
  '1': 'PlayerSubscribedDTO',
  '2': [
    {
      '1': 'player_sessions',
      '3': 1,
      '4': 3,
      '5': 11,
      '6': '.poker.PlayerSessionDTO',
      '10': 'playerSessions'
    },
    {
      '1': 'round_state',
      '3': 2,
      '4': 1,
      '5': 11,
      '6': '.poker.RoundStateDTO',
      '9': 0,
      '10': 'roundState',
      '17': true
    },
  ],
  '8': [
    {'1': '_round_state'},
  ],
};

/// Descriptor for `PlayerSubscribedDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List playerSubscribedDTODescriptor = $convert.base64Decode(
    'ChNQbGF5ZXJTdWJzY3JpYmVkRFRPEkAKD3BsYXllcl9zZXNzaW9ucxgBIAMoCzIXLnBva2VyLl'
    'BsYXllclNlc3Npb25EVE9SDnBsYXllclNlc3Npb25zEjoKC3JvdW5kX3N0YXRlGAIgASgLMhQu'
    'cG9rZXIuUm91bmRTdGF0ZURUT0gAUgpyb3VuZFN0YXRliAEBQg4KDF9yb3VuZF9zdGF0ZQ==');

@$core.Deprecated('Use playerConnectedDTODescriptor instead')
const PlayerConnectedDTO$json = {
  '1': 'PlayerConnectedDTO',
  '2': [
    {
      '1': 'player_session',
      '3': 1,
      '4': 1,
      '5': 11,
      '6': '.poker.PlayerSessionDTO',
      '10': 'playerSession'
    },
  ],
};

/// Descriptor for `PlayerConnectedDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List playerConnectedDTODescriptor = $convert.base64Decode(
    'ChJQbGF5ZXJDb25uZWN0ZWREVE8SPgoOcGxheWVyX3Nlc3Npb24YASABKAsyFy5wb2tlci5QbG'
    'F5ZXJTZXNzaW9uRFRPUg1wbGF5ZXJTZXNzaW9u');

@$core.Deprecated('Use dealerDeterminedDTODescriptor instead')
const DealerDeterminedDTO$json = {
  '1': 'DealerDeterminedDTO',
  '2': [
    {
      '1': 'player_session',
      '3': 1,
      '4': 1,
      '5': 11,
      '6': '.poker.PlayerSessionDTO',
      '10': 'playerSession'
    },
  ],
};

/// Descriptor for `DealerDeterminedDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List dealerDeterminedDTODescriptor = $convert.base64Decode(
    'ChNEZWFsZXJEZXRlcm1pbmVkRFRPEj4KDnBsYXllcl9zZXNzaW9uGAEgASgLMhcucG9rZXIuUG'
    'xheWVyU2Vzc2lvbkRUT1INcGxheWVyU2Vzc2lvbg==');

@$core.Deprecated('Use dealPlayerCardDTODescriptor instead')
const DealPlayerCardDTO$json = {
  '1': 'DealPlayerCardDTO',
  '2': [
    {
      '1': 'player_session',
      '3': 1,
      '4': 1,
      '5': 11,
      '6': '.poker.PlayerSessionDTO',
      '10': 'playerSession'
    },
    {'1': 'card', '3': 2, '4': 1, '5': 11, '6': '.poker.CardDTO', '10': 'card'},
  ],
};

/// Descriptor for `DealPlayerCardDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List dealPlayerCardDTODescriptor = $convert.base64Decode(
    'ChFEZWFsUGxheWVyQ2FyZERUTxI+Cg5wbGF5ZXJfc2Vzc2lvbhgBIAEoCzIXLnBva2VyLlBsYX'
    'llclNlc3Npb25EVE9SDXBsYXllclNlc3Npb24SIgoEY2FyZBgCIAEoCzIOLnBva2VyLkNhcmRE'
    'VE9SBGNhcmQ=');

@$core.Deprecated('Use dealCommunityCardDTODescriptor instead')
const DealCommunityCardDTO$json = {
  '1': 'DealCommunityCardDTO',
  '2': [
    {'1': 'card', '3': 1, '4': 1, '5': 11, '6': '.poker.CardDTO', '10': 'card'},
  ],
};

/// Descriptor for `DealCommunityCardDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List dealCommunityCardDTODescriptor = $convert.base64Decode(
    'ChREZWFsQ29tbXVuaXR5Q2FyZERUTxIiCgRjYXJkGAEgASgLMg4ucG9rZXIuQ2FyZERUT1IEY2'
    'FyZA==');

@$core.Deprecated('Use playerTurnDTODescriptor instead')
const PlayerTurnDTO$json = {
  '1': 'PlayerTurnDTO',
  '2': [
    {
      '1': 'player_session',
      '3': 1,
      '4': 1,
      '5': 11,
      '6': '.poker.PlayerSessionDTO',
      '10': 'playerSession'
    },
    {
      '1': 'betting_round',
      '3': 2,
      '4': 1,
      '5': 11,
      '6': '.poker.BettingRoundDTO',
      '10': 'bettingRound'
    },
    {
      '1': 'next_actions',
      '3': 3,
      '4': 3,
      '5': 14,
      '6': '.poker.ActionType',
      '10': 'nextActions'
    },
    {'1': 'amount_to_call', '3': 4, '4': 1, '5': 9, '10': 'amountToCall'},
    {
      '1': 'player_turn_wait_ms',
      '3': 5,
      '4': 1,
      '5': 3,
      '9': 0,
      '10': 'playerTurnWaitMs',
      '17': true
    },
  ],
  '8': [
    {'1': '_player_turn_wait_ms'},
  ],
};

/// Descriptor for `PlayerTurnDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List playerTurnDTODescriptor = $convert.base64Decode(
    'Cg1QbGF5ZXJUdXJuRFRPEj4KDnBsYXllcl9zZXNzaW9uGAEgASgLMhcucG9rZXIuUGxheWVyU2'
    'Vzc2lvbkRUT1INcGxheWVyU2Vzc2lvbhI7Cg1iZXR0aW5nX3JvdW5kGAIgASgLMhYucG9rZXIu'
    'QmV0dGluZ1JvdW5kRFRPUgxiZXR0aW5nUm91bmQSNAoMbmV4dF9hY3Rpb25zGAMgAygOMhEucG'
    '9rZXIuQWN0aW9uVHlwZVILbmV4dEFjdGlvbnMSJAoOYW1vdW50X3RvX2NhbGwYBCABKAlSDGFt'
    'b3VudFRvQ2FsbBIyChNwbGF5ZXJfdHVybl93YWl0X21zGAUgASgDSABSEHBsYXllclR1cm5XYW'
    'l0TXOIAQFCFgoUX3BsYXllcl90dXJuX3dhaXRfbXM=');

@$core.Deprecated('Use playerActionedDTODescriptor instead')
const PlayerActionedDTO$json = {
  '1': 'PlayerActionedDTO',
  '2': [
    {
      '1': 'action',
      '3': 1,
      '4': 1,
      '5': 11,
      '6': '.poker.PlayerActionDTO',
      '10': 'action'
    },
  ],
};

/// Descriptor for `PlayerActionedDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List playerActionedDTODescriptor = $convert.base64Decode(
    'ChFQbGF5ZXJBY3Rpb25lZERUTxIuCgZhY3Rpb24YASABKAsyFi5wb2tlci5QbGF5ZXJBY3Rpb2'
    '5EVE9SBmFjdGlvbg==');

@$core.Deprecated('Use bettingRoundUpdatedDTODescriptor instead')
const BettingRoundUpdatedDTO$json = {
  '1': 'BettingRoundUpdatedDTO',
  '2': [
    {
      '1': 'round',
      '3': 1,
      '4': 1,
      '5': 11,
      '6': '.poker.RoundDTO',
      '10': 'round'
    },
    {
      '1': 'betting_round',
      '3': 2,
      '4': 1,
      '5': 11,
      '6': '.poker.BettingRoundDTO',
      '10': 'bettingRound'
    },
    {
      '1': 'round_pots',
      '3': 3,
      '4': 3,
      '5': 11,
      '6': '.poker.RoundPotDTO',
      '10': 'roundPots'
    },
  ],
};

/// Descriptor for `BettingRoundUpdatedDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List bettingRoundUpdatedDTODescriptor = $convert.base64Decode(
    'ChZCZXR0aW5nUm91bmRVcGRhdGVkRFRPEiUKBXJvdW5kGAEgASgLMg8ucG9rZXIuUm91bmREVE'
    '9SBXJvdW5kEjsKDWJldHRpbmdfcm91bmQYAiABKAsyFi5wb2tlci5CZXR0aW5nUm91bmREVE9S'
    'DGJldHRpbmdSb3VuZBIxCgpyb3VuZF9wb3RzGAMgAygLMhIucG9rZXIuUm91bmRQb3REVE9SCX'
    'JvdW5kUG90cw==');

@$core.Deprecated('Use roundFinishedDTODescriptor instead')
const RoundFinishedDTO$json = {
  '1': 'RoundFinishedDTO',
  '2': [
    {
      '1': 'winners',
      '3': 1,
      '4': 3,
      '5': 11,
      '6': '.poker.RoundWinnerDTO',
      '10': 'winners'
    },
  ],
};

/// Descriptor for `RoundFinishedDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List roundFinishedDTODescriptor = $convert.base64Decode(
    'ChBSb3VuZEZpbmlzaGVkRFRPEi8KB3dpbm5lcnMYASADKAsyFS5wb2tlci5Sb3VuZFdpbm5lck'
    'RUT1IHd2lubmVycw==');

@$core.Deprecated('Use gameFinishedDTODescriptor instead')
const GameFinishedDTO$json = {
  '1': 'GameFinishedDTO',
};

/// Descriptor for `GameFinishedDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List gameFinishedDTODescriptor =
    $convert.base64Decode('Cg9HYW1lRmluaXNoZWREVE8=');

@$core.Deprecated('Use chatMessageDTODescriptor instead')
const ChatMessageDTO$json = {
  '1': 'ChatMessageDTO',
  '2': [
    {'1': 'username', '3': 1, '4': 1, '5': 9, '10': 'username'},
    {'1': 'message', '3': 2, '4': 1, '5': 9, '10': 'message'},
  ],
};

/// Descriptor for `ChatMessageDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List chatMessageDTODescriptor = $convert.base64Decode(
    'Cg5DaGF0TWVzc2FnZURUTxIaCgh1c2VybmFtZRgBIAEoCVIIdXNlcm5hbWUSGAoHbWVzc2FnZR'
    'gCIAEoCVIHbWVzc2FnZQ==');

@$core.Deprecated('Use logMessageDTODescriptor instead')
const LogMessageDTO$json = {
  '1': 'LogMessageDTO',
  '2': [
    {'1': 'message', '3': 1, '4': 1, '5': 9, '10': 'message'},
  ],
};

/// Descriptor for `LogMessageDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List logMessageDTODescriptor = $convert
    .base64Decode('Cg1Mb2dNZXNzYWdlRFRPEhgKB21lc3NhZ2UYASABKAlSB21lc3NhZ2U=');

@$core.Deprecated('Use errorMessageDTODescriptor instead')
const ErrorMessageDTO$json = {
  '1': 'ErrorMessageDTO',
  '2': [
    {'1': 'message', '3': 1, '4': 1, '5': 9, '10': 'message'},
  ],
};

/// Descriptor for `ErrorMessageDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List errorMessageDTODescriptor = $convert.base64Decode(
    'Cg9FcnJvck1lc3NhZ2VEVE8SGAoHbWVzc2FnZRgBIAEoCVIHbWVzc2FnZQ==');

@$core.Deprecated('Use playerDisconnectedDTODescriptor instead')
const PlayerDisconnectedDTO$json = {
  '1': 'PlayerDisconnectedDTO',
  '2': [
    {'1': 'username', '3': 1, '4': 1, '5': 9, '10': 'username'},
  ],
};

/// Descriptor for `PlayerDisconnectedDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List playerDisconnectedDTODescriptor =
    $convert.base64Decode(
        'ChVQbGF5ZXJEaXNjb25uZWN0ZWREVE8SGgoIdXNlcm5hbWUYASABKAlSCHVzZXJuYW1l');

@$core.Deprecated('Use roundStateDTODescriptor instead')
const RoundStateDTO$json = {
  '1': 'RoundStateDTO',
  '2': [
    {
      '1': 'round',
      '3': 1,
      '4': 1,
      '5': 11,
      '6': '.poker.RoundDTO',
      '10': 'round'
    },
    {
      '1': 'dealer',
      '3': 2,
      '4': 1,
      '5': 11,
      '6': '.poker.PlayerSessionDTO',
      '10': 'dealer'
    },
    {
      '1': 'player_cards',
      '3': 3,
      '4': 3,
      '5': 11,
      '6': '.poker.DealPlayerCardDTO',
      '10': 'playerCards'
    },
    {
      '1': 'community_cards',
      '3': 4,
      '4': 3,
      '5': 11,
      '6': '.poker.CardDTO',
      '10': 'communityCards'
    },
    {
      '1': 'betting_round',
      '3': 5,
      '4': 1,
      '5': 11,
      '6': '.poker.BettingRoundDTO',
      '10': 'bettingRound'
    },
    {
      '1': 'round_pots',
      '3': 6,
      '4': 3,
      '5': 11,
      '6': '.poker.RoundPotDTO',
      '10': 'roundPots'
    },
    {
      '1': 'folded_players',
      '3': 7,
      '4': 3,
      '5': 11,
      '6': '.poker.PlayerSessionDTO',
      '10': 'foldedPlayers'
    },
    {
      '1': 'current_turn',
      '3': 8,
      '4': 1,
      '5': 11,
      '6': '.poker.PlayerTurnDTO',
      '9': 0,
      '10': 'currentTurn',
      '17': true
    },
  ],
  '8': [
    {'1': '_current_turn'},
  ],
};

/// Descriptor for `RoundStateDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List roundStateDTODescriptor = $convert.base64Decode(
    'Cg1Sb3VuZFN0YXRlRFRPEiUKBXJvdW5kGAEgASgLMg8ucG9rZXIuUm91bmREVE9SBXJvdW5kEi'
    '8KBmRlYWxlchgCIAEoCzIXLnBva2VyLlBsYXllclNlc3Npb25EVE9SBmRlYWxlchI7CgxwbGF5'
    'ZXJfY2FyZHMYAyADKAsyGC5wb2tlci5EZWFsUGxheWVyQ2FyZERUT1ILcGxheWVyQ2FyZHMSNw'
    'oPY29tbXVuaXR5X2NhcmRzGAQgAygLMg4ucG9rZXIuQ2FyZERUT1IOY29tbXVuaXR5Q2FyZHMS'
    'OwoNYmV0dGluZ19yb3VuZBgFIAEoCzIWLnBva2VyLkJldHRpbmdSb3VuZERUT1IMYmV0dGluZ1'
    'JvdW5kEjEKCnJvdW5kX3BvdHMYBiADKAsyEi5wb2tlci5Sb3VuZFBvdERUT1IJcm91bmRQb3Rz'
    'Ej4KDmZvbGRlZF9wbGF5ZXJzGAcgAygLMhcucG9rZXIuUGxheWVyU2Vzc2lvbkRUT1INZm9sZG'
    'VkUGxheWVycxI8CgxjdXJyZW50X3R1cm4YCCABKAsyFC5wb2tlci5QbGF5ZXJUdXJuRFRPSABS'
    'C2N1cnJlbnRUdXJuiAEBQg8KDV9jdXJyZW50X3R1cm4=');

@$core.Deprecated('Use createPlayerActionDTODescriptor instead')
const CreatePlayerActionDTO$json = {
  '1': 'CreatePlayerActionDTO',
  '2': [
    {
      '1': 'action',
      '3': 1,
      '4': 1,
      '5': 14,
      '6': '.poker.ActionType',
      '10': 'action'
    },
    {'1': 'amount', '3': 2, '4': 1, '5': 9, '10': 'amount'},
  ],
};

/// Descriptor for `CreatePlayerActionDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List createPlayerActionDTODescriptor = $convert.base64Decode(
    'ChVDcmVhdGVQbGF5ZXJBY3Rpb25EVE8SKQoGYWN0aW9uGAEgASgOMhEucG9rZXIuQWN0aW9uVH'
    'lwZVIGYWN0aW9uEhYKBmFtb3VudBgCIAEoCVIGYW1vdW50');

@$core.Deprecated('Use createChatMessageDTODescriptor instead')
const CreateChatMessageDTO$json = {
  '1': 'CreateChatMessageDTO',
  '2': [
    {'1': 'message', '3': 1, '4': 1, '5': 9, '10': 'message'},
  ],
};

/// Descriptor for `CreateChatMessageDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List createChatMessageDTODescriptor =
    $convert.base64Decode(
        'ChRDcmVhdGVDaGF0TWVzc2FnZURUTxIYCgdtZXNzYWdlGAEgASgJUgdtZXNzYWdl');

@$core.Deprecated('Use createBotConnectionDTODescriptor instead')
const CreateBotConnectionDTO$json = {
  '1': 'CreateBotConnectionDTO',
  '2': [
    {'1': 'bot_user_id', '3': 1, '4': 1, '5': 9, '10': 'botUserId'},
    {'1': 'buy_in_amount', '3': 2, '4': 1, '5': 9, '10': 'buyInAmount'},
  ],
};

/// Descriptor for `CreateBotConnectionDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List createBotConnectionDTODescriptor =
    $convert.base64Decode(
        'ChZDcmVhdGVCb3RDb25uZWN0aW9uRFRPEh4KC2JvdF91c2VyX2lkGAEgASgJUglib3RVc2VySW'
        'QSIgoNYnV5X2luX2Ftb3VudBgCIAEoCVILYnV5SW5BbW91bnQ=');
