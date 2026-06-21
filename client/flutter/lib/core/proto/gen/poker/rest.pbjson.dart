// This is a generated file - do not edit.
//
// Generated from poker/rest.proto.

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

@$core.Deprecated('Use createTableDTODescriptor instead')
const CreateTableDTO$json = {
  '1': 'CreateTableDTO',
  '2': [
    {'1': 'name', '3': 1, '4': 1, '5': 9, '10': 'name'},
    {
      '1': 'game_type',
      '3': 2,
      '4': 1,
      '5': 14,
      '6': '.poker.GameType',
      '10': 'gameType'
    },
    {
      '1': 'speed_multiplier',
      '3': 3,
      '4': 1,
      '5': 1,
      '9': 0,
      '10': 'speedMultiplier',
      '17': true
    },
    {
      '1': 'total_rounds',
      '3': 4,
      '4': 1,
      '5': 5,
      '9': 1,
      '10': 'totalRounds',
      '17': true
    },
    {'1': 'min_players', '3': 5, '4': 1, '5': 5, '10': 'minPlayers'},
    {'1': 'max_players', '3': 6, '4': 1, '5': 5, '10': 'maxPlayers'},
    {'1': 'min_buyin', '3': 7, '4': 1, '5': 9, '10': 'minBuyin'},
    {'1': 'max_buyin', '3': 8, '4': 1, '5': 9, '10': 'maxBuyin'},
  ],
  '8': [
    {'1': '_speed_multiplier'},
    {'1': '_total_rounds'},
  ],
};

/// Descriptor for `CreateTableDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List createTableDTODescriptor = $convert.base64Decode(
    'Cg5DcmVhdGVUYWJsZURUTxISCgRuYW1lGAEgASgJUgRuYW1lEiwKCWdhbWVfdHlwZRgCIAEoDj'
    'IPLnBva2VyLkdhbWVUeXBlUghnYW1lVHlwZRIuChBzcGVlZF9tdWx0aXBsaWVyGAMgASgBSABS'
    'D3NwZWVkTXVsdGlwbGllcogBARImCgx0b3RhbF9yb3VuZHMYBCABKAVIAVILdG90YWxSb3VuZH'
    'OIAQESHwoLbWluX3BsYXllcnMYBSABKAVSCm1pblBsYXllcnMSHwoLbWF4X3BsYXllcnMYBiAB'
    'KAVSCm1heFBsYXllcnMSGwoJbWluX2J1eWluGAcgASgJUghtaW5CdXlpbhIbCgltYXhfYnV5aW'
    '4YCCABKAlSCG1heEJ1eWluQhMKEV9zcGVlZF9tdWx0aXBsaWVyQg8KDV90b3RhbF9yb3VuZHM=');

@$core.Deprecated('Use userAmountDTODescriptor instead')
const UserAmountDTO$json = {
  '1': 'UserAmountDTO',
  '2': [
    {'1': 'amount', '3': 1, '4': 1, '5': 9, '10': 'amount'},
  ],
};

/// Descriptor for `UserAmountDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List userAmountDTODescriptor = $convert
    .base64Decode('Cg1Vc2VyQW1vdW50RFRPEhYKBmFtb3VudBgBIAEoCVIGYW1vdW50');

@$core.Deprecated('Use availableTableDTODescriptor instead')
const AvailableTableDTO$json = {
  '1': 'AvailableTableDTO',
  '2': [
    {
      '1': 'table',
      '3': 1,
      '4': 1,
      '5': 11,
      '6': '.poker.TableDTO',
      '10': 'table'
    },
    {
      '1': 'players_connected',
      '3': 2,
      '4': 1,
      '5': 5,
      '10': 'playersConnected'
    },
    {
      '1': 'current_user_connected',
      '3': 3,
      '4': 1,
      '5': 8,
      '10': 'currentUserConnected'
    },
    {
      '1': 'current_user_connection_type',
      '3': 4,
      '4': 1,
      '5': 14,
      '6': '.poker.ConnectionType',
      '9': 0,
      '10': 'currentUserConnectionType',
      '17': true
    },
    {
      '1': 'reconnect_millis_remaining',
      '3': 5,
      '4': 1,
      '5': 3,
      '9': 1,
      '10': 'reconnectMillisRemaining',
      '17': true
    },
  ],
  '8': [
    {'1': '_current_user_connection_type'},
    {'1': '_reconnect_millis_remaining'},
  ],
};

/// Descriptor for `AvailableTableDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List availableTableDTODescriptor = $convert.base64Decode(
    'ChFBdmFpbGFibGVUYWJsZURUTxIlCgV0YWJsZRgBIAEoCzIPLnBva2VyLlRhYmxlRFRPUgV0YW'
    'JsZRIrChFwbGF5ZXJzX2Nvbm5lY3RlZBgCIAEoBVIQcGxheWVyc0Nvbm5lY3RlZBI0ChZjdXJy'
    'ZW50X3VzZXJfY29ubmVjdGVkGAMgASgIUhRjdXJyZW50VXNlckNvbm5lY3RlZBJbChxjdXJyZW'
    '50X3VzZXJfY29ubmVjdGlvbl90eXBlGAQgASgOMhUucG9rZXIuQ29ubmVjdGlvblR5cGVIAFIZ'
    'Y3VycmVudFVzZXJDb25uZWN0aW9uVHlwZYgBARJBChpyZWNvbm5lY3RfbWlsbGlzX3JlbWFpbm'
    'luZxgFIAEoA0gBUhhyZWNvbm5lY3RNaWxsaXNSZW1haW5pbmeIAQFCHwodX2N1cnJlbnRfdXNl'
    'cl9jb25uZWN0aW9uX3R5cGVCHQobX3JlY29ubmVjdF9taWxsaXNfcmVtYWluaW5n');

@$core.Deprecated('Use availableTableListResponseDescriptor instead')
const AvailableTableListResponse$json = {
  '1': 'AvailableTableListResponse',
  '2': [
    {
      '1': 'tables',
      '3': 1,
      '4': 3,
      '5': 11,
      '6': '.poker.AvailableTableDTO',
      '10': 'tables'
    },
  ],
};

/// Descriptor for `AvailableTableListResponse`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List availableTableListResponseDescriptor =
    $convert.base64Decode(
        'ChpBdmFpbGFibGVUYWJsZUxpc3RSZXNwb25zZRIwCgZ0YWJsZXMYASADKAsyGC5wb2tlci5Bdm'
        'FpbGFibGVUYWJsZURUT1IGdGFibGVz');

@$core.Deprecated('Use appUserListResponseDescriptor instead')
const AppUserListResponse$json = {
  '1': 'AppUserListResponse',
  '2': [
    {
      '1': 'users',
      '3': 1,
      '4': 3,
      '5': 11,
      '6': '.poker.AppUserDTO',
      '10': 'users'
    },
  ],
};

/// Descriptor for `AppUserListResponse`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List appUserListResponseDescriptor = $convert.base64Decode(
    'ChNBcHBVc2VyTGlzdFJlc3BvbnNlEicKBXVzZXJzGAEgAygLMhEucG9rZXIuQXBwVXNlckRUT1'
    'IFdXNlcnM=');

@$core.Deprecated('Use transactionHistoryListResponseDescriptor instead')
const TransactionHistoryListResponse$json = {
  '1': 'TransactionHistoryListResponse',
  '2': [
    {
      '1': 'transactions',
      '3': 1,
      '4': 3,
      '5': 11,
      '6': '.poker.TransactionHistoryDTO',
      '10': 'transactions'
    },
  ],
};

/// Descriptor for `TransactionHistoryListResponse`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List transactionHistoryListResponseDescriptor =
    $convert.base64Decode(
        'Ch5UcmFuc2FjdGlvbkhpc3RvcnlMaXN0UmVzcG9uc2USQAoMdHJhbnNhY3Rpb25zGAEgAygLMh'
        'wucG9rZXIuVHJhbnNhY3Rpb25IaXN0b3J5RFRPUgx0cmFuc2FjdGlvbnM=');

@$core.Deprecated('Use apiErrorDTODescriptor instead')
const ApiErrorDTO$json = {
  '1': 'ApiErrorDTO',
  '2': [
    {'1': 'status', '3': 1, '4': 1, '5': 5, '10': 'status'},
    {'1': 'message', '3': 2, '4': 1, '5': 9, '10': 'message'},
  ],
};

/// Descriptor for `ApiErrorDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List apiErrorDTODescriptor = $convert.base64Decode(
    'CgtBcGlFcnJvckRUTxIWCgZzdGF0dXMYASABKAVSBnN0YXR1cxIYCgdtZXNzYWdlGAIgASgJUg'
    'dtZXNzYWdl');
