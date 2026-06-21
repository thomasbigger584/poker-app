// This is a generated file - do not edit.
//
// Generated from poker/rest.proto.

// @dart = 3.3

// ignore_for_file: annotate_overrides, camel_case_types, comment_references
// ignore_for_file: constant_identifier_names
// ignore_for_file: curly_braces_in_flow_control_structures
// ignore_for_file: deprecated_member_use_from_same_package, library_prefixes
// ignore_for_file: non_constant_identifier_names, prefer_relative_imports

import 'dart:core' as $core;

import 'package:fixnum/fixnum.dart' as $fixnum;
import 'package:protobuf/protobuf.dart' as $pb;

import 'domain.pb.dart' as $0;
import 'enums.pbenum.dart' as $1;

export 'package:protobuf/protobuf.dart' show GeneratedMessageGenericExtensions;

/// POST /api/poker-table
class CreateTableDTO extends $pb.GeneratedMessage {
  factory CreateTableDTO({
    $core.String? name,
    $1.GameType? gameType,
    $core.double? speedMultiplier,
    $core.int? totalRounds,
    $core.int? minPlayers,
    $core.int? maxPlayers,
    $core.String? minBuyin,
    $core.String? maxBuyin,
  }) {
    final result = create();
    if (name != null) result.name = name;
    if (gameType != null) result.gameType = gameType;
    if (speedMultiplier != null) result.speedMultiplier = speedMultiplier;
    if (totalRounds != null) result.totalRounds = totalRounds;
    if (minPlayers != null) result.minPlayers = minPlayers;
    if (maxPlayers != null) result.maxPlayers = maxPlayers;
    if (minBuyin != null) result.minBuyin = minBuyin;
    if (maxBuyin != null) result.maxBuyin = maxBuyin;
    return result;
  }

  CreateTableDTO._();

  factory CreateTableDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory CreateTableDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'CreateTableDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'name')
    ..aE<$1.GameType>(2, _omitFieldNames ? '' : 'gameType',
        enumValues: $1.GameType.values)
    ..aD(3, _omitFieldNames ? '' : 'speedMultiplier')
    ..aI(4, _omitFieldNames ? '' : 'totalRounds')
    ..aI(5, _omitFieldNames ? '' : 'minPlayers')
    ..aI(6, _omitFieldNames ? '' : 'maxPlayers')
    ..aOS(7, _omitFieldNames ? '' : 'minBuyin')
    ..aOS(8, _omitFieldNames ? '' : 'maxBuyin')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  CreateTableDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  CreateTableDTO copyWith(void Function(CreateTableDTO) updates) =>
      super.copyWith((message) => updates(message as CreateTableDTO))
          as CreateTableDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static CreateTableDTO create() => CreateTableDTO._();
  @$core.override
  CreateTableDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static CreateTableDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<CreateTableDTO>(create);
  static CreateTableDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get name => $_getSZ(0);
  @$pb.TagNumber(1)
  set name($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasName() => $_has(0);
  @$pb.TagNumber(1)
  void clearName() => $_clearField(1);

  @$pb.TagNumber(2)
  $1.GameType get gameType => $_getN(1);
  @$pb.TagNumber(2)
  set gameType($1.GameType value) => $_setField(2, value);
  @$pb.TagNumber(2)
  $core.bool hasGameType() => $_has(1);
  @$pb.TagNumber(2)
  void clearGameType() => $_clearField(2);

  @$pb.TagNumber(3)
  $core.double get speedMultiplier => $_getN(2);
  @$pb.TagNumber(3)
  set speedMultiplier($core.double value) => $_setDouble(2, value);
  @$pb.TagNumber(3)
  $core.bool hasSpeedMultiplier() => $_has(2);
  @$pb.TagNumber(3)
  void clearSpeedMultiplier() => $_clearField(3);

  @$pb.TagNumber(4)
  $core.int get totalRounds => $_getIZ(3);
  @$pb.TagNumber(4)
  set totalRounds($core.int value) => $_setSignedInt32(3, value);
  @$pb.TagNumber(4)
  $core.bool hasTotalRounds() => $_has(3);
  @$pb.TagNumber(4)
  void clearTotalRounds() => $_clearField(4);

  @$pb.TagNumber(5)
  $core.int get minPlayers => $_getIZ(4);
  @$pb.TagNumber(5)
  set minPlayers($core.int value) => $_setSignedInt32(4, value);
  @$pb.TagNumber(5)
  $core.bool hasMinPlayers() => $_has(4);
  @$pb.TagNumber(5)
  void clearMinPlayers() => $_clearField(5);

  @$pb.TagNumber(6)
  $core.int get maxPlayers => $_getIZ(5);
  @$pb.TagNumber(6)
  set maxPlayers($core.int value) => $_setSignedInt32(5, value);
  @$pb.TagNumber(6)
  $core.bool hasMaxPlayers() => $_has(5);
  @$pb.TagNumber(6)
  void clearMaxPlayers() => $_clearField(6);

  @$pb.TagNumber(7)
  $core.String get minBuyin => $_getSZ(6);
  @$pb.TagNumber(7)
  set minBuyin($core.String value) => $_setString(6, value);
  @$pb.TagNumber(7)
  $core.bool hasMinBuyin() => $_has(6);
  @$pb.TagNumber(7)
  void clearMinBuyin() => $_clearField(7);

  @$pb.TagNumber(8)
  $core.String get maxBuyin => $_getSZ(7);
  @$pb.TagNumber(8)
  set maxBuyin($core.String value) => $_setString(7, value);
  @$pb.TagNumber(8)
  $core.bool hasMaxBuyin() => $_has(7);
  @$pb.TagNumber(8)
  void clearMaxBuyin() => $_clearField(8);
}

/// POST /api/app-user/deposit, POST /api/app-user/withdraw
class UserAmountDTO extends $pb.GeneratedMessage {
  factory UserAmountDTO({
    $core.String? amount,
  }) {
    final result = create();
    if (amount != null) result.amount = amount;
    return result;
  }

  UserAmountDTO._();

  factory UserAmountDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory UserAmountDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'UserAmountDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'amount')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  UserAmountDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  UserAmountDTO copyWith(void Function(UserAmountDTO) updates) =>
      super.copyWith((message) => updates(message as UserAmountDTO))
          as UserAmountDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static UserAmountDTO create() => UserAmountDTO._();
  @$core.override
  UserAmountDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static UserAmountDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<UserAmountDTO>(create);
  static UserAmountDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get amount => $_getSZ(0);
  @$pb.TagNumber(1)
  set amount($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasAmount() => $_has(0);
  @$pb.TagNumber(1)
  void clearAmount() => $_clearField(1);
}

/// GET /api/poker-table (element of AvailableTableListResponse)
class AvailableTableDTO extends $pb.GeneratedMessage {
  factory AvailableTableDTO({
    $0.TableDTO? table,
    $core.int? playersConnected,
    $core.bool? currentUserConnected,
    $1.ConnectionType? currentUserConnectionType,
    $fixnum.Int64? reconnectMillisRemaining,
  }) {
    final result = create();
    if (table != null) result.table = table;
    if (playersConnected != null) result.playersConnected = playersConnected;
    if (currentUserConnected != null)
      result.currentUserConnected = currentUserConnected;
    if (currentUserConnectionType != null)
      result.currentUserConnectionType = currentUserConnectionType;
    if (reconnectMillisRemaining != null)
      result.reconnectMillisRemaining = reconnectMillisRemaining;
    return result;
  }

  AvailableTableDTO._();

  factory AvailableTableDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory AvailableTableDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'AvailableTableDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOM<$0.TableDTO>(1, _omitFieldNames ? '' : 'table',
        subBuilder: $0.TableDTO.create)
    ..aI(2, _omitFieldNames ? '' : 'playersConnected')
    ..aOB(3, _omitFieldNames ? '' : 'currentUserConnected')
    ..aE<$1.ConnectionType>(
        4, _omitFieldNames ? '' : 'currentUserConnectionType',
        enumValues: $1.ConnectionType.values)
    ..aInt64(5, _omitFieldNames ? '' : 'reconnectMillisRemaining')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  AvailableTableDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  AvailableTableDTO copyWith(void Function(AvailableTableDTO) updates) =>
      super.copyWith((message) => updates(message as AvailableTableDTO))
          as AvailableTableDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static AvailableTableDTO create() => AvailableTableDTO._();
  @$core.override
  AvailableTableDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static AvailableTableDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<AvailableTableDTO>(create);
  static AvailableTableDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $0.TableDTO get table => $_getN(0);
  @$pb.TagNumber(1)
  set table($0.TableDTO value) => $_setField(1, value);
  @$pb.TagNumber(1)
  $core.bool hasTable() => $_has(0);
  @$pb.TagNumber(1)
  void clearTable() => $_clearField(1);
  @$pb.TagNumber(1)
  $0.TableDTO ensureTable() => $_ensure(0);

  @$pb.TagNumber(2)
  $core.int get playersConnected => $_getIZ(1);
  @$pb.TagNumber(2)
  set playersConnected($core.int value) => $_setSignedInt32(1, value);
  @$pb.TagNumber(2)
  $core.bool hasPlayersConnected() => $_has(1);
  @$pb.TagNumber(2)
  void clearPlayersConnected() => $_clearField(2);

  @$pb.TagNumber(3)
  $core.bool get currentUserConnected => $_getBF(2);
  @$pb.TagNumber(3)
  set currentUserConnected($core.bool value) => $_setBool(2, value);
  @$pb.TagNumber(3)
  $core.bool hasCurrentUserConnected() => $_has(2);
  @$pb.TagNumber(3)
  void clearCurrentUserConnected() => $_clearField(3);

  @$pb.TagNumber(4)
  $1.ConnectionType get currentUserConnectionType => $_getN(3);
  @$pb.TagNumber(4)
  set currentUserConnectionType($1.ConnectionType value) =>
      $_setField(4, value);
  @$pb.TagNumber(4)
  $core.bool hasCurrentUserConnectionType() => $_has(3);
  @$pb.TagNumber(4)
  void clearCurrentUserConnectionType() => $_clearField(4);

  @$pb.TagNumber(5)
  $fixnum.Int64 get reconnectMillisRemaining => $_getI64(4);
  @$pb.TagNumber(5)
  set reconnectMillisRemaining($fixnum.Int64 value) => $_setInt64(4, value);
  @$pb.TagNumber(5)
  $core.bool hasReconnectMillisRemaining() => $_has(4);
  @$pb.TagNumber(5)
  void clearReconnectMillisRemaining() => $_clearField(5);
}

/// GET /api/poker-table
class AvailableTableListResponse extends $pb.GeneratedMessage {
  factory AvailableTableListResponse({
    $core.Iterable<AvailableTableDTO>? tables,
  }) {
    final result = create();
    if (tables != null) result.tables.addAll(tables);
    return result;
  }

  AvailableTableListResponse._();

  factory AvailableTableListResponse.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory AvailableTableListResponse.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'AvailableTableListResponse',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..pPM<AvailableTableDTO>(1, _omitFieldNames ? '' : 'tables',
        subBuilder: AvailableTableDTO.create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  AvailableTableListResponse clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  AvailableTableListResponse copyWith(
          void Function(AvailableTableListResponse) updates) =>
      super.copyWith(
              (message) => updates(message as AvailableTableListResponse))
          as AvailableTableListResponse;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static AvailableTableListResponse create() => AvailableTableListResponse._();
  @$core.override
  AvailableTableListResponse createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static AvailableTableListResponse getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<AvailableTableListResponse>(create);
  static AvailableTableListResponse? _defaultInstance;

  @$pb.TagNumber(1)
  $pb.PbList<AvailableTableDTO> get tables => $_getList(0);
}

/// GET /api/app-user/bots
class AppUserListResponse extends $pb.GeneratedMessage {
  factory AppUserListResponse({
    $core.Iterable<$0.AppUserDTO>? users,
  }) {
    final result = create();
    if (users != null) result.users.addAll(users);
    return result;
  }

  AppUserListResponse._();

  factory AppUserListResponse.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory AppUserListResponse.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'AppUserListResponse',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..pPM<$0.AppUserDTO>(1, _omitFieldNames ? '' : 'users',
        subBuilder: $0.AppUserDTO.create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  AppUserListResponse clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  AppUserListResponse copyWith(void Function(AppUserListResponse) updates) =>
      super.copyWith((message) => updates(message as AppUserListResponse))
          as AppUserListResponse;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static AppUserListResponse create() => AppUserListResponse._();
  @$core.override
  AppUserListResponse createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static AppUserListResponse getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<AppUserListResponse>(create);
  static AppUserListResponse? _defaultInstance;

  @$pb.TagNumber(1)
  $pb.PbList<$0.AppUserDTO> get users => $_getList(0);
}

/// GET /api/transaction-history/current
class TransactionHistoryListResponse extends $pb.GeneratedMessage {
  factory TransactionHistoryListResponse({
    $core.Iterable<$0.TransactionHistoryDTO>? transactions,
  }) {
    final result = create();
    if (transactions != null) result.transactions.addAll(transactions);
    return result;
  }

  TransactionHistoryListResponse._();

  factory TransactionHistoryListResponse.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory TransactionHistoryListResponse.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'TransactionHistoryListResponse',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..pPM<$0.TransactionHistoryDTO>(1, _omitFieldNames ? '' : 'transactions',
        subBuilder: $0.TransactionHistoryDTO.create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  TransactionHistoryListResponse clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  TransactionHistoryListResponse copyWith(
          void Function(TransactionHistoryListResponse) updates) =>
      super.copyWith(
              (message) => updates(message as TransactionHistoryListResponse))
          as TransactionHistoryListResponse;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static TransactionHistoryListResponse create() =>
      TransactionHistoryListResponse._();
  @$core.override
  TransactionHistoryListResponse createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static TransactionHistoryListResponse getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<TransactionHistoryListResponse>(create);
  static TransactionHistoryListResponse? _defaultInstance;

  @$pb.TagNumber(1)
  $pb.PbList<$0.TransactionHistoryDTO> get transactions => $_getList(0);
}

/// Body for non-validation error responses (404, 500, ...).
class ApiErrorDTO extends $pb.GeneratedMessage {
  factory ApiErrorDTO({
    $core.int? status,
    $core.String? message,
  }) {
    final result = create();
    if (status != null) result.status = status;
    if (message != null) result.message = message;
    return result;
  }

  ApiErrorDTO._();

  factory ApiErrorDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory ApiErrorDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'ApiErrorDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aI(1, _omitFieldNames ? '' : 'status')
    ..aOS(2, _omitFieldNames ? '' : 'message')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  ApiErrorDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  ApiErrorDTO copyWith(void Function(ApiErrorDTO) updates) =>
      super.copyWith((message) => updates(message as ApiErrorDTO))
          as ApiErrorDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static ApiErrorDTO create() => ApiErrorDTO._();
  @$core.override
  ApiErrorDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static ApiErrorDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<ApiErrorDTO>(create);
  static ApiErrorDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.int get status => $_getIZ(0);
  @$pb.TagNumber(1)
  set status($core.int value) => $_setSignedInt32(0, value);
  @$pb.TagNumber(1)
  $core.bool hasStatus() => $_has(0);
  @$pb.TagNumber(1)
  void clearStatus() => $_clearField(1);

  @$pb.TagNumber(2)
  $core.String get message => $_getSZ(1);
  @$pb.TagNumber(2)
  set message($core.String value) => $_setString(1, value);
  @$pb.TagNumber(2)
  $core.bool hasMessage() => $_has(1);
  @$pb.TagNumber(2)
  void clearMessage() => $_clearField(2);
}

const $core.bool _omitFieldNames =
    $core.bool.fromEnvironment('protobuf.omit_field_names');
const $core.bool _omitMessageNames =
    $core.bool.fromEnvironment('protobuf.omit_message_names');
