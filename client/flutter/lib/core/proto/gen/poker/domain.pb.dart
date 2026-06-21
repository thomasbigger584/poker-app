// This is a generated file - do not edit.
//
// Generated from poker/domain.proto.

// @dart = 3.3

// ignore_for_file: annotate_overrides, camel_case_types, comment_references
// ignore_for_file: constant_identifier_names
// ignore_for_file: curly_braces_in_flow_control_structures
// ignore_for_file: deprecated_member_use_from_same_package, library_prefixes
// ignore_for_file: non_constant_identifier_names, prefer_relative_imports

import 'dart:core' as $core;

import 'package:protobuf/protobuf.dart' as $pb;

import 'enums.pbenum.dart' as $0;

export 'package:protobuf/protobuf.dart' show GeneratedMessageGenericExtensions;

class AppUserDTO extends $pb.GeneratedMessage {
  factory AppUserDTO({
    $core.String? id,
    $core.String? username,
    $core.String? firstName,
    $core.String? lastName,
    $core.String? email,
    $core.bool? emailVerified,
    $core.bool? enabled,
    $core.String? totalFunds,
    $core.String? persona,
  }) {
    final result = create();
    if (id != null) result.id = id;
    if (username != null) result.username = username;
    if (firstName != null) result.firstName = firstName;
    if (lastName != null) result.lastName = lastName;
    if (email != null) result.email = email;
    if (emailVerified != null) result.emailVerified = emailVerified;
    if (enabled != null) result.enabled = enabled;
    if (totalFunds != null) result.totalFunds = totalFunds;
    if (persona != null) result.persona = persona;
    return result;
  }

  AppUserDTO._();

  factory AppUserDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory AppUserDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'AppUserDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'id')
    ..aOS(2, _omitFieldNames ? '' : 'username')
    ..aOS(3, _omitFieldNames ? '' : 'firstName')
    ..aOS(4, _omitFieldNames ? '' : 'lastName')
    ..aOS(5, _omitFieldNames ? '' : 'email')
    ..aOB(6, _omitFieldNames ? '' : 'emailVerified')
    ..aOB(7, _omitFieldNames ? '' : 'enabled')
    ..aOS(8, _omitFieldNames ? '' : 'totalFunds')
    ..aOS(9, _omitFieldNames ? '' : 'persona')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  AppUserDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  AppUserDTO copyWith(void Function(AppUserDTO) updates) =>
      super.copyWith((message) => updates(message as AppUserDTO)) as AppUserDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static AppUserDTO create() => AppUserDTO._();
  @$core.override
  AppUserDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static AppUserDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<AppUserDTO>(create);
  static AppUserDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get id => $_getSZ(0);
  @$pb.TagNumber(1)
  set id($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasId() => $_has(0);
  @$pb.TagNumber(1)
  void clearId() => $_clearField(1);

  @$pb.TagNumber(2)
  $core.String get username => $_getSZ(1);
  @$pb.TagNumber(2)
  set username($core.String value) => $_setString(1, value);
  @$pb.TagNumber(2)
  $core.bool hasUsername() => $_has(1);
  @$pb.TagNumber(2)
  void clearUsername() => $_clearField(2);

  @$pb.TagNumber(3)
  $core.String get firstName => $_getSZ(2);
  @$pb.TagNumber(3)
  set firstName($core.String value) => $_setString(2, value);
  @$pb.TagNumber(3)
  $core.bool hasFirstName() => $_has(2);
  @$pb.TagNumber(3)
  void clearFirstName() => $_clearField(3);

  @$pb.TagNumber(4)
  $core.String get lastName => $_getSZ(3);
  @$pb.TagNumber(4)
  set lastName($core.String value) => $_setString(3, value);
  @$pb.TagNumber(4)
  $core.bool hasLastName() => $_has(3);
  @$pb.TagNumber(4)
  void clearLastName() => $_clearField(4);

  @$pb.TagNumber(5)
  $core.String get email => $_getSZ(4);
  @$pb.TagNumber(5)
  set email($core.String value) => $_setString(4, value);
  @$pb.TagNumber(5)
  $core.bool hasEmail() => $_has(4);
  @$pb.TagNumber(5)
  void clearEmail() => $_clearField(5);

  @$pb.TagNumber(6)
  $core.bool get emailVerified => $_getBF(5);
  @$pb.TagNumber(6)
  set emailVerified($core.bool value) => $_setBool(5, value);
  @$pb.TagNumber(6)
  $core.bool hasEmailVerified() => $_has(5);
  @$pb.TagNumber(6)
  void clearEmailVerified() => $_clearField(6);

  @$pb.TagNumber(7)
  $core.bool get enabled => $_getBF(6);
  @$pb.TagNumber(7)
  set enabled($core.bool value) => $_setBool(6, value);
  @$pb.TagNumber(7)
  $core.bool hasEnabled() => $_has(6);
  @$pb.TagNumber(7)
  void clearEnabled() => $_clearField(7);

  @$pb.TagNumber(8)
  $core.String get totalFunds => $_getSZ(7);
  @$pb.TagNumber(8)
  set totalFunds($core.String value) => $_setString(7, value);
  @$pb.TagNumber(8)
  $core.bool hasTotalFunds() => $_has(7);
  @$pb.TagNumber(8)
  void clearTotalFunds() => $_clearField(8);

  @$pb.TagNumber(9)
  $core.String get persona => $_getSZ(8);
  @$pb.TagNumber(9)
  set persona($core.String value) => $_setString(8, value);
  @$pb.TagNumber(9)
  $core.bool hasPersona() => $_has(8);
  @$pb.TagNumber(9)
  void clearPersona() => $_clearField(9);
}

class TableDTO extends $pb.GeneratedMessage {
  factory TableDTO({
    $core.String? id,
    $core.String? name,
    $0.GameType? gameType,
    $core.double? speedMultiplier,
    $core.int? totalRounds,
    $core.int? minPlayers,
    $core.int? maxPlayers,
    $core.String? minBuyin,
    $core.String? maxBuyin,
  }) {
    final result = create();
    if (id != null) result.id = id;
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

  TableDTO._();

  factory TableDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory TableDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'TableDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'id')
    ..aOS(2, _omitFieldNames ? '' : 'name')
    ..aE<$0.GameType>(3, _omitFieldNames ? '' : 'gameType',
        enumValues: $0.GameType.values)
    ..aD(4, _omitFieldNames ? '' : 'speedMultiplier')
    ..aI(5, _omitFieldNames ? '' : 'totalRounds')
    ..aI(6, _omitFieldNames ? '' : 'minPlayers')
    ..aI(7, _omitFieldNames ? '' : 'maxPlayers')
    ..aOS(8, _omitFieldNames ? '' : 'minBuyin')
    ..aOS(9, _omitFieldNames ? '' : 'maxBuyin')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  TableDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  TableDTO copyWith(void Function(TableDTO) updates) =>
      super.copyWith((message) => updates(message as TableDTO)) as TableDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static TableDTO create() => TableDTO._();
  @$core.override
  TableDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static TableDTO getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<TableDTO>(create);
  static TableDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get id => $_getSZ(0);
  @$pb.TagNumber(1)
  set id($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasId() => $_has(0);
  @$pb.TagNumber(1)
  void clearId() => $_clearField(1);

  @$pb.TagNumber(2)
  $core.String get name => $_getSZ(1);
  @$pb.TagNumber(2)
  set name($core.String value) => $_setString(1, value);
  @$pb.TagNumber(2)
  $core.bool hasName() => $_has(1);
  @$pb.TagNumber(2)
  void clearName() => $_clearField(2);

  @$pb.TagNumber(3)
  $0.GameType get gameType => $_getN(2);
  @$pb.TagNumber(3)
  set gameType($0.GameType value) => $_setField(3, value);
  @$pb.TagNumber(3)
  $core.bool hasGameType() => $_has(2);
  @$pb.TagNumber(3)
  void clearGameType() => $_clearField(3);

  @$pb.TagNumber(4)
  $core.double get speedMultiplier => $_getN(3);
  @$pb.TagNumber(4)
  set speedMultiplier($core.double value) => $_setDouble(3, value);
  @$pb.TagNumber(4)
  $core.bool hasSpeedMultiplier() => $_has(3);
  @$pb.TagNumber(4)
  void clearSpeedMultiplier() => $_clearField(4);

  @$pb.TagNumber(5)
  $core.int get totalRounds => $_getIZ(4);
  @$pb.TagNumber(5)
  set totalRounds($core.int value) => $_setSignedInt32(4, value);
  @$pb.TagNumber(5)
  $core.bool hasTotalRounds() => $_has(4);
  @$pb.TagNumber(5)
  void clearTotalRounds() => $_clearField(5);

  @$pb.TagNumber(6)
  $core.int get minPlayers => $_getIZ(5);
  @$pb.TagNumber(6)
  set minPlayers($core.int value) => $_setSignedInt32(5, value);
  @$pb.TagNumber(6)
  $core.bool hasMinPlayers() => $_has(5);
  @$pb.TagNumber(6)
  void clearMinPlayers() => $_clearField(6);

  @$pb.TagNumber(7)
  $core.int get maxPlayers => $_getIZ(6);
  @$pb.TagNumber(7)
  set maxPlayers($core.int value) => $_setSignedInt32(6, value);
  @$pb.TagNumber(7)
  $core.bool hasMaxPlayers() => $_has(6);
  @$pb.TagNumber(7)
  void clearMaxPlayers() => $_clearField(7);

  @$pb.TagNumber(8)
  $core.String get minBuyin => $_getSZ(7);
  @$pb.TagNumber(8)
  set minBuyin($core.String value) => $_setString(7, value);
  @$pb.TagNumber(8)
  $core.bool hasMinBuyin() => $_has(7);
  @$pb.TagNumber(8)
  void clearMinBuyin() => $_clearField(8);

  @$pb.TagNumber(9)
  $core.String get maxBuyin => $_getSZ(8);
  @$pb.TagNumber(9)
  set maxBuyin($core.String value) => $_setString(8, value);
  @$pb.TagNumber(9)
  $core.bool hasMaxBuyin() => $_has(8);
  @$pb.TagNumber(9)
  void clearMaxBuyin() => $_clearField(9);
}

class CardDTO extends $pb.GeneratedMessage {
  factory CardDTO({
    $core.String? id,
    $0.RankType? rankType,
    $core.String? rankChar,
    $core.int? rankValue,
    $0.SuitType? suitType,
    $core.String? suitChar,
    $0.CardType? cardType,
  }) {
    final result = create();
    if (id != null) result.id = id;
    if (rankType != null) result.rankType = rankType;
    if (rankChar != null) result.rankChar = rankChar;
    if (rankValue != null) result.rankValue = rankValue;
    if (suitType != null) result.suitType = suitType;
    if (suitChar != null) result.suitChar = suitChar;
    if (cardType != null) result.cardType = cardType;
    return result;
  }

  CardDTO._();

  factory CardDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory CardDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'CardDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'id')
    ..aE<$0.RankType>(2, _omitFieldNames ? '' : 'rankType',
        enumValues: $0.RankType.values)
    ..aOS(3, _omitFieldNames ? '' : 'rankChar')
    ..aI(4, _omitFieldNames ? '' : 'rankValue')
    ..aE<$0.SuitType>(5, _omitFieldNames ? '' : 'suitType',
        enumValues: $0.SuitType.values)
    ..aOS(6, _omitFieldNames ? '' : 'suitChar')
    ..aE<$0.CardType>(7, _omitFieldNames ? '' : 'cardType',
        enumValues: $0.CardType.values)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  CardDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  CardDTO copyWith(void Function(CardDTO) updates) =>
      super.copyWith((message) => updates(message as CardDTO)) as CardDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static CardDTO create() => CardDTO._();
  @$core.override
  CardDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static CardDTO getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<CardDTO>(create);
  static CardDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get id => $_getSZ(0);
  @$pb.TagNumber(1)
  set id($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasId() => $_has(0);
  @$pb.TagNumber(1)
  void clearId() => $_clearField(1);

  @$pb.TagNumber(2)
  $0.RankType get rankType => $_getN(1);
  @$pb.TagNumber(2)
  set rankType($0.RankType value) => $_setField(2, value);
  @$pb.TagNumber(2)
  $core.bool hasRankType() => $_has(1);
  @$pb.TagNumber(2)
  void clearRankType() => $_clearField(2);

  @$pb.TagNumber(3)
  $core.String get rankChar => $_getSZ(2);
  @$pb.TagNumber(3)
  set rankChar($core.String value) => $_setString(2, value);
  @$pb.TagNumber(3)
  $core.bool hasRankChar() => $_has(2);
  @$pb.TagNumber(3)
  void clearRankChar() => $_clearField(3);

  @$pb.TagNumber(4)
  $core.int get rankValue => $_getIZ(3);
  @$pb.TagNumber(4)
  set rankValue($core.int value) => $_setSignedInt32(3, value);
  @$pb.TagNumber(4)
  $core.bool hasRankValue() => $_has(3);
  @$pb.TagNumber(4)
  void clearRankValue() => $_clearField(4);

  @$pb.TagNumber(5)
  $0.SuitType get suitType => $_getN(4);
  @$pb.TagNumber(5)
  set suitType($0.SuitType value) => $_setField(5, value);
  @$pb.TagNumber(5)
  $core.bool hasSuitType() => $_has(4);
  @$pb.TagNumber(5)
  void clearSuitType() => $_clearField(5);

  @$pb.TagNumber(6)
  $core.String get suitChar => $_getSZ(5);
  @$pb.TagNumber(6)
  set suitChar($core.String value) => $_setString(5, value);
  @$pb.TagNumber(6)
  $core.bool hasSuitChar() => $_has(5);
  @$pb.TagNumber(6)
  void clearSuitChar() => $_clearField(6);

  @$pb.TagNumber(7)
  $0.CardType get cardType => $_getN(6);
  @$pb.TagNumber(7)
  set cardType($0.CardType value) => $_setField(7, value);
  @$pb.TagNumber(7)
  $core.bool hasCardType() => $_has(6);
  @$pb.TagNumber(7)
  void clearCardType() => $_clearField(7);
}

class HandDTO extends $pb.GeneratedMessage {
  factory HandDTO({
    $core.String? id,
    $0.HandType? handType,
    $core.String? handTypeStr,
    $core.Iterable<CardDTO>? cards,
  }) {
    final result = create();
    if (id != null) result.id = id;
    if (handType != null) result.handType = handType;
    if (handTypeStr != null) result.handTypeStr = handTypeStr;
    if (cards != null) result.cards.addAll(cards);
    return result;
  }

  HandDTO._();

  factory HandDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory HandDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'HandDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'id')
    ..aE<$0.HandType>(2, _omitFieldNames ? '' : 'handType',
        enumValues: $0.HandType.values)
    ..aOS(3, _omitFieldNames ? '' : 'handTypeStr')
    ..pPM<CardDTO>(4, _omitFieldNames ? '' : 'cards',
        subBuilder: CardDTO.create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  HandDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  HandDTO copyWith(void Function(HandDTO) updates) =>
      super.copyWith((message) => updates(message as HandDTO)) as HandDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static HandDTO create() => HandDTO._();
  @$core.override
  HandDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static HandDTO getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<HandDTO>(create);
  static HandDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get id => $_getSZ(0);
  @$pb.TagNumber(1)
  set id($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasId() => $_has(0);
  @$pb.TagNumber(1)
  void clearId() => $_clearField(1);

  @$pb.TagNumber(2)
  $0.HandType get handType => $_getN(1);
  @$pb.TagNumber(2)
  set handType($0.HandType value) => $_setField(2, value);
  @$pb.TagNumber(2)
  $core.bool hasHandType() => $_has(1);
  @$pb.TagNumber(2)
  void clearHandType() => $_clearField(2);

  @$pb.TagNumber(3)
  $core.String get handTypeStr => $_getSZ(2);
  @$pb.TagNumber(3)
  set handTypeStr($core.String value) => $_setString(2, value);
  @$pb.TagNumber(3)
  $core.bool hasHandTypeStr() => $_has(2);
  @$pb.TagNumber(3)
  void clearHandTypeStr() => $_clearField(3);

  @$pb.TagNumber(4)
  $pb.PbList<CardDTO> get cards => $_getList(3);
}

class PlayerSessionDTO extends $pb.GeneratedMessage {
  factory PlayerSessionDTO({
    $core.String? id,
    AppUserDTO? user,
    TableDTO? pokerTable,
    $core.int? position,
    $core.bool? dealer,
    $core.String? funds,
    $0.SessionState? sessionState,
    $0.ConnectionType? connectionType,
  }) {
    final result = create();
    if (id != null) result.id = id;
    if (user != null) result.user = user;
    if (pokerTable != null) result.pokerTable = pokerTable;
    if (position != null) result.position = position;
    if (dealer != null) result.dealer = dealer;
    if (funds != null) result.funds = funds;
    if (sessionState != null) result.sessionState = sessionState;
    if (connectionType != null) result.connectionType = connectionType;
    return result;
  }

  PlayerSessionDTO._();

  factory PlayerSessionDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory PlayerSessionDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'PlayerSessionDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'id')
    ..aOM<AppUserDTO>(2, _omitFieldNames ? '' : 'user',
        subBuilder: AppUserDTO.create)
    ..aOM<TableDTO>(3, _omitFieldNames ? '' : 'pokerTable',
        subBuilder: TableDTO.create)
    ..aI(4, _omitFieldNames ? '' : 'position')
    ..aOB(5, _omitFieldNames ? '' : 'dealer')
    ..aOS(6, _omitFieldNames ? '' : 'funds')
    ..aE<$0.SessionState>(7, _omitFieldNames ? '' : 'sessionState',
        enumValues: $0.SessionState.values)
    ..aE<$0.ConnectionType>(8, _omitFieldNames ? '' : 'connectionType',
        enumValues: $0.ConnectionType.values)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  PlayerSessionDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  PlayerSessionDTO copyWith(void Function(PlayerSessionDTO) updates) =>
      super.copyWith((message) => updates(message as PlayerSessionDTO))
          as PlayerSessionDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static PlayerSessionDTO create() => PlayerSessionDTO._();
  @$core.override
  PlayerSessionDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static PlayerSessionDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<PlayerSessionDTO>(create);
  static PlayerSessionDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get id => $_getSZ(0);
  @$pb.TagNumber(1)
  set id($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasId() => $_has(0);
  @$pb.TagNumber(1)
  void clearId() => $_clearField(1);

  @$pb.TagNumber(2)
  AppUserDTO get user => $_getN(1);
  @$pb.TagNumber(2)
  set user(AppUserDTO value) => $_setField(2, value);
  @$pb.TagNumber(2)
  $core.bool hasUser() => $_has(1);
  @$pb.TagNumber(2)
  void clearUser() => $_clearField(2);
  @$pb.TagNumber(2)
  AppUserDTO ensureUser() => $_ensure(1);

  @$pb.TagNumber(3)
  TableDTO get pokerTable => $_getN(2);
  @$pb.TagNumber(3)
  set pokerTable(TableDTO value) => $_setField(3, value);
  @$pb.TagNumber(3)
  $core.bool hasPokerTable() => $_has(2);
  @$pb.TagNumber(3)
  void clearPokerTable() => $_clearField(3);
  @$pb.TagNumber(3)
  TableDTO ensurePokerTable() => $_ensure(2);

  @$pb.TagNumber(4)
  $core.int get position => $_getIZ(3);
  @$pb.TagNumber(4)
  set position($core.int value) => $_setSignedInt32(3, value);
  @$pb.TagNumber(4)
  $core.bool hasPosition() => $_has(3);
  @$pb.TagNumber(4)
  void clearPosition() => $_clearField(4);

  @$pb.TagNumber(5)
  $core.bool get dealer => $_getBF(4);
  @$pb.TagNumber(5)
  set dealer($core.bool value) => $_setBool(4, value);
  @$pb.TagNumber(5)
  $core.bool hasDealer() => $_has(4);
  @$pb.TagNumber(5)
  void clearDealer() => $_clearField(5);

  @$pb.TagNumber(6)
  $core.String get funds => $_getSZ(5);
  @$pb.TagNumber(6)
  set funds($core.String value) => $_setString(5, value);
  @$pb.TagNumber(6)
  $core.bool hasFunds() => $_has(5);
  @$pb.TagNumber(6)
  void clearFunds() => $_clearField(6);

  @$pb.TagNumber(7)
  $0.SessionState get sessionState => $_getN(6);
  @$pb.TagNumber(7)
  set sessionState($0.SessionState value) => $_setField(7, value);
  @$pb.TagNumber(7)
  $core.bool hasSessionState() => $_has(6);
  @$pb.TagNumber(7)
  void clearSessionState() => $_clearField(7);

  @$pb.TagNumber(8)
  $0.ConnectionType get connectionType => $_getN(7);
  @$pb.TagNumber(8)
  set connectionType($0.ConnectionType value) => $_setField(8, value);
  @$pb.TagNumber(8)
  $core.bool hasConnectionType() => $_has(7);
  @$pb.TagNumber(8)
  void clearConnectionType() => $_clearField(8);
}

class RoundDTO extends $pb.GeneratedMessage {
  factory RoundDTO({
    $core.String? id,
    $0.RoundState? roundState,
  }) {
    final result = create();
    if (id != null) result.id = id;
    if (roundState != null) result.roundState = roundState;
    return result;
  }

  RoundDTO._();

  factory RoundDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory RoundDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'RoundDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'id')
    ..aE<$0.RoundState>(2, _omitFieldNames ? '' : 'roundState',
        enumValues: $0.RoundState.values)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  RoundDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  RoundDTO copyWith(void Function(RoundDTO) updates) =>
      super.copyWith((message) => updates(message as RoundDTO)) as RoundDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static RoundDTO create() => RoundDTO._();
  @$core.override
  RoundDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static RoundDTO getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<RoundDTO>(create);
  static RoundDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get id => $_getSZ(0);
  @$pb.TagNumber(1)
  set id($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasId() => $_has(0);
  @$pb.TagNumber(1)
  void clearId() => $_clearField(1);

  @$pb.TagNumber(2)
  $0.RoundState get roundState => $_getN(1);
  @$pb.TagNumber(2)
  set roundState($0.RoundState value) => $_setField(2, value);
  @$pb.TagNumber(2)
  $core.bool hasRoundState() => $_has(1);
  @$pb.TagNumber(2)
  void clearRoundState() => $_clearField(2);
}

class BettingRoundDTO extends $pb.GeneratedMessage {
  factory BettingRoundDTO({
    $core.String? id,
    $0.BettingRoundType? type,
    $0.BettingRoundState? state,
    $core.Iterable<BettingRoundRefundDTO>? bettingRoundRefunds,
  }) {
    final result = create();
    if (id != null) result.id = id;
    if (type != null) result.type = type;
    if (state != null) result.state = state;
    if (bettingRoundRefunds != null)
      result.bettingRoundRefunds.addAll(bettingRoundRefunds);
    return result;
  }

  BettingRoundDTO._();

  factory BettingRoundDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory BettingRoundDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'BettingRoundDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'id')
    ..aE<$0.BettingRoundType>(2, _omitFieldNames ? '' : 'type',
        enumValues: $0.BettingRoundType.values)
    ..aE<$0.BettingRoundState>(3, _omitFieldNames ? '' : 'state',
        enumValues: $0.BettingRoundState.values)
    ..pPM<BettingRoundRefundDTO>(
        4, _omitFieldNames ? '' : 'bettingRoundRefunds',
        subBuilder: BettingRoundRefundDTO.create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  BettingRoundDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  BettingRoundDTO copyWith(void Function(BettingRoundDTO) updates) =>
      super.copyWith((message) => updates(message as BettingRoundDTO))
          as BettingRoundDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static BettingRoundDTO create() => BettingRoundDTO._();
  @$core.override
  BettingRoundDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static BettingRoundDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<BettingRoundDTO>(create);
  static BettingRoundDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get id => $_getSZ(0);
  @$pb.TagNumber(1)
  set id($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasId() => $_has(0);
  @$pb.TagNumber(1)
  void clearId() => $_clearField(1);

  @$pb.TagNumber(2)
  $0.BettingRoundType get type => $_getN(1);
  @$pb.TagNumber(2)
  set type($0.BettingRoundType value) => $_setField(2, value);
  @$pb.TagNumber(2)
  $core.bool hasType() => $_has(1);
  @$pb.TagNumber(2)
  void clearType() => $_clearField(2);

  @$pb.TagNumber(3)
  $0.BettingRoundState get state => $_getN(2);
  @$pb.TagNumber(3)
  set state($0.BettingRoundState value) => $_setField(3, value);
  @$pb.TagNumber(3)
  $core.bool hasState() => $_has(2);
  @$pb.TagNumber(3)
  void clearState() => $_clearField(3);

  @$pb.TagNumber(4)
  $pb.PbList<BettingRoundRefundDTO> get bettingRoundRefunds => $_getList(3);
}

class BettingRoundRefundDTO extends $pb.GeneratedMessage {
  factory BettingRoundRefundDTO({
    $core.String? id,
    PlayerSessionDTO? playerSession,
    $core.String? amount,
  }) {
    final result = create();
    if (id != null) result.id = id;
    if (playerSession != null) result.playerSession = playerSession;
    if (amount != null) result.amount = amount;
    return result;
  }

  BettingRoundRefundDTO._();

  factory BettingRoundRefundDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory BettingRoundRefundDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'BettingRoundRefundDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'id')
    ..aOM<PlayerSessionDTO>(2, _omitFieldNames ? '' : 'playerSession',
        subBuilder: PlayerSessionDTO.create)
    ..aOS(3, _omitFieldNames ? '' : 'amount')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  BettingRoundRefundDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  BettingRoundRefundDTO copyWith(
          void Function(BettingRoundRefundDTO) updates) =>
      super.copyWith((message) => updates(message as BettingRoundRefundDTO))
          as BettingRoundRefundDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static BettingRoundRefundDTO create() => BettingRoundRefundDTO._();
  @$core.override
  BettingRoundRefundDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static BettingRoundRefundDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<BettingRoundRefundDTO>(create);
  static BettingRoundRefundDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get id => $_getSZ(0);
  @$pb.TagNumber(1)
  set id($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasId() => $_has(0);
  @$pb.TagNumber(1)
  void clearId() => $_clearField(1);

  @$pb.TagNumber(2)
  PlayerSessionDTO get playerSession => $_getN(1);
  @$pb.TagNumber(2)
  set playerSession(PlayerSessionDTO value) => $_setField(2, value);
  @$pb.TagNumber(2)
  $core.bool hasPlayerSession() => $_has(1);
  @$pb.TagNumber(2)
  void clearPlayerSession() => $_clearField(2);
  @$pb.TagNumber(2)
  PlayerSessionDTO ensurePlayerSession() => $_ensure(1);

  @$pb.TagNumber(3)
  $core.String get amount => $_getSZ(2);
  @$pb.TagNumber(3)
  set amount($core.String value) => $_setString(2, value);
  @$pb.TagNumber(3)
  $core.bool hasAmount() => $_has(2);
  @$pb.TagNumber(3)
  void clearAmount() => $_clearField(3);
}

class RoundPotDTO extends $pb.GeneratedMessage {
  factory RoundPotDTO({
    $core.String? id,
    $core.String? potAmount,
    $core.int? potIndex,
    $core.Iterable<PlayerSessionDTO>? eligiblePlayers,
  }) {
    final result = create();
    if (id != null) result.id = id;
    if (potAmount != null) result.potAmount = potAmount;
    if (potIndex != null) result.potIndex = potIndex;
    if (eligiblePlayers != null) result.eligiblePlayers.addAll(eligiblePlayers);
    return result;
  }

  RoundPotDTO._();

  factory RoundPotDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory RoundPotDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'RoundPotDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'id')
    ..aOS(2, _omitFieldNames ? '' : 'potAmount')
    ..aI(3, _omitFieldNames ? '' : 'potIndex')
    ..pPM<PlayerSessionDTO>(4, _omitFieldNames ? '' : 'eligiblePlayers',
        subBuilder: PlayerSessionDTO.create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  RoundPotDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  RoundPotDTO copyWith(void Function(RoundPotDTO) updates) =>
      super.copyWith((message) => updates(message as RoundPotDTO))
          as RoundPotDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static RoundPotDTO create() => RoundPotDTO._();
  @$core.override
  RoundPotDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static RoundPotDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<RoundPotDTO>(create);
  static RoundPotDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get id => $_getSZ(0);
  @$pb.TagNumber(1)
  set id($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasId() => $_has(0);
  @$pb.TagNumber(1)
  void clearId() => $_clearField(1);

  @$pb.TagNumber(2)
  $core.String get potAmount => $_getSZ(1);
  @$pb.TagNumber(2)
  set potAmount($core.String value) => $_setString(1, value);
  @$pb.TagNumber(2)
  $core.bool hasPotAmount() => $_has(1);
  @$pb.TagNumber(2)
  void clearPotAmount() => $_clearField(2);

  @$pb.TagNumber(3)
  $core.int get potIndex => $_getIZ(2);
  @$pb.TagNumber(3)
  set potIndex($core.int value) => $_setSignedInt32(2, value);
  @$pb.TagNumber(3)
  $core.bool hasPotIndex() => $_has(2);
  @$pb.TagNumber(3)
  void clearPotIndex() => $_clearField(3);

  @$pb.TagNumber(4)
  $pb.PbList<PlayerSessionDTO> get eligiblePlayers => $_getList(3);
}

class PlayerActionDTO extends $pb.GeneratedMessage {
  factory PlayerActionDTO({
    $core.String? id,
    PlayerSessionDTO? playerSession,
    BettingRoundDTO? bettingRound,
    $0.ActionType? actionType,
    $core.String? amount,
  }) {
    final result = create();
    if (id != null) result.id = id;
    if (playerSession != null) result.playerSession = playerSession;
    if (bettingRound != null) result.bettingRound = bettingRound;
    if (actionType != null) result.actionType = actionType;
    if (amount != null) result.amount = amount;
    return result;
  }

  PlayerActionDTO._();

  factory PlayerActionDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory PlayerActionDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'PlayerActionDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'id')
    ..aOM<PlayerSessionDTO>(2, _omitFieldNames ? '' : 'playerSession',
        subBuilder: PlayerSessionDTO.create)
    ..aOM<BettingRoundDTO>(3, _omitFieldNames ? '' : 'bettingRound',
        subBuilder: BettingRoundDTO.create)
    ..aE<$0.ActionType>(4, _omitFieldNames ? '' : 'actionType',
        enumValues: $0.ActionType.values)
    ..aOS(5, _omitFieldNames ? '' : 'amount')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  PlayerActionDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  PlayerActionDTO copyWith(void Function(PlayerActionDTO) updates) =>
      super.copyWith((message) => updates(message as PlayerActionDTO))
          as PlayerActionDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static PlayerActionDTO create() => PlayerActionDTO._();
  @$core.override
  PlayerActionDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static PlayerActionDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<PlayerActionDTO>(create);
  static PlayerActionDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get id => $_getSZ(0);
  @$pb.TagNumber(1)
  set id($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasId() => $_has(0);
  @$pb.TagNumber(1)
  void clearId() => $_clearField(1);

  @$pb.TagNumber(2)
  PlayerSessionDTO get playerSession => $_getN(1);
  @$pb.TagNumber(2)
  set playerSession(PlayerSessionDTO value) => $_setField(2, value);
  @$pb.TagNumber(2)
  $core.bool hasPlayerSession() => $_has(1);
  @$pb.TagNumber(2)
  void clearPlayerSession() => $_clearField(2);
  @$pb.TagNumber(2)
  PlayerSessionDTO ensurePlayerSession() => $_ensure(1);

  @$pb.TagNumber(3)
  BettingRoundDTO get bettingRound => $_getN(2);
  @$pb.TagNumber(3)
  set bettingRound(BettingRoundDTO value) => $_setField(3, value);
  @$pb.TagNumber(3)
  $core.bool hasBettingRound() => $_has(2);
  @$pb.TagNumber(3)
  void clearBettingRound() => $_clearField(3);
  @$pb.TagNumber(3)
  BettingRoundDTO ensureBettingRound() => $_ensure(2);

  @$pb.TagNumber(4)
  $0.ActionType get actionType => $_getN(3);
  @$pb.TagNumber(4)
  set actionType($0.ActionType value) => $_setField(4, value);
  @$pb.TagNumber(4)
  $core.bool hasActionType() => $_has(3);
  @$pb.TagNumber(4)
  void clearActionType() => $_clearField(4);

  @$pb.TagNumber(5)
  $core.String get amount => $_getSZ(4);
  @$pb.TagNumber(5)
  set amount($core.String value) => $_setString(4, value);
  @$pb.TagNumber(5)
  $core.bool hasAmount() => $_has(4);
  @$pb.TagNumber(5)
  void clearAmount() => $_clearField(5);
}

class RoundWinnerDTO extends $pb.GeneratedMessage {
  factory RoundWinnerDTO({
    $core.String? id,
    PlayerSessionDTO? playerSession,
    RoundDTO? round,
    HandDTO? hand,
    $core.String? amount,
  }) {
    final result = create();
    if (id != null) result.id = id;
    if (playerSession != null) result.playerSession = playerSession;
    if (round != null) result.round = round;
    if (hand != null) result.hand = hand;
    if (amount != null) result.amount = amount;
    return result;
  }

  RoundWinnerDTO._();

  factory RoundWinnerDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory RoundWinnerDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'RoundWinnerDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'id')
    ..aOM<PlayerSessionDTO>(2, _omitFieldNames ? '' : 'playerSession',
        subBuilder: PlayerSessionDTO.create)
    ..aOM<RoundDTO>(3, _omitFieldNames ? '' : 'round',
        subBuilder: RoundDTO.create)
    ..aOM<HandDTO>(4, _omitFieldNames ? '' : 'hand', subBuilder: HandDTO.create)
    ..aOS(5, _omitFieldNames ? '' : 'amount')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  RoundWinnerDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  RoundWinnerDTO copyWith(void Function(RoundWinnerDTO) updates) =>
      super.copyWith((message) => updates(message as RoundWinnerDTO))
          as RoundWinnerDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static RoundWinnerDTO create() => RoundWinnerDTO._();
  @$core.override
  RoundWinnerDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static RoundWinnerDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<RoundWinnerDTO>(create);
  static RoundWinnerDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get id => $_getSZ(0);
  @$pb.TagNumber(1)
  set id($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasId() => $_has(0);
  @$pb.TagNumber(1)
  void clearId() => $_clearField(1);

  @$pb.TagNumber(2)
  PlayerSessionDTO get playerSession => $_getN(1);
  @$pb.TagNumber(2)
  set playerSession(PlayerSessionDTO value) => $_setField(2, value);
  @$pb.TagNumber(2)
  $core.bool hasPlayerSession() => $_has(1);
  @$pb.TagNumber(2)
  void clearPlayerSession() => $_clearField(2);
  @$pb.TagNumber(2)
  PlayerSessionDTO ensurePlayerSession() => $_ensure(1);

  @$pb.TagNumber(3)
  RoundDTO get round => $_getN(2);
  @$pb.TagNumber(3)
  set round(RoundDTO value) => $_setField(3, value);
  @$pb.TagNumber(3)
  $core.bool hasRound() => $_has(2);
  @$pb.TagNumber(3)
  void clearRound() => $_clearField(3);
  @$pb.TagNumber(3)
  RoundDTO ensureRound() => $_ensure(2);

  @$pb.TagNumber(4)
  HandDTO get hand => $_getN(3);
  @$pb.TagNumber(4)
  set hand(HandDTO value) => $_setField(4, value);
  @$pb.TagNumber(4)
  $core.bool hasHand() => $_has(3);
  @$pb.TagNumber(4)
  void clearHand() => $_clearField(4);
  @$pb.TagNumber(4)
  HandDTO ensureHand() => $_ensure(3);

  @$pb.TagNumber(5)
  $core.String get amount => $_getSZ(4);
  @$pb.TagNumber(5)
  set amount($core.String value) => $_setString(4, value);
  @$pb.TagNumber(5)
  $core.bool hasAmount() => $_has(4);
  @$pb.TagNumber(5)
  void clearAmount() => $_clearField(5);
}

class TransactionHistoryDTO extends $pb.GeneratedMessage {
  factory TransactionHistoryDTO({
    $core.String? id,
    $core.String? amount,
    $0.TransactionHistoryType? type,
    $core.String? createdDateTime,
  }) {
    final result = create();
    if (id != null) result.id = id;
    if (amount != null) result.amount = amount;
    if (type != null) result.type = type;
    if (createdDateTime != null) result.createdDateTime = createdDateTime;
    return result;
  }

  TransactionHistoryDTO._();

  factory TransactionHistoryDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory TransactionHistoryDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'TransactionHistoryDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'id')
    ..aOS(2, _omitFieldNames ? '' : 'amount')
    ..aE<$0.TransactionHistoryType>(3, _omitFieldNames ? '' : 'type',
        enumValues: $0.TransactionHistoryType.values)
    ..aOS(4, _omitFieldNames ? '' : 'createdDateTime')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  TransactionHistoryDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  TransactionHistoryDTO copyWith(
          void Function(TransactionHistoryDTO) updates) =>
      super.copyWith((message) => updates(message as TransactionHistoryDTO))
          as TransactionHistoryDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static TransactionHistoryDTO create() => TransactionHistoryDTO._();
  @$core.override
  TransactionHistoryDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static TransactionHistoryDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<TransactionHistoryDTO>(create);
  static TransactionHistoryDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get id => $_getSZ(0);
  @$pb.TagNumber(1)
  set id($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasId() => $_has(0);
  @$pb.TagNumber(1)
  void clearId() => $_clearField(1);

  @$pb.TagNumber(2)
  $core.String get amount => $_getSZ(1);
  @$pb.TagNumber(2)
  set amount($core.String value) => $_setString(1, value);
  @$pb.TagNumber(2)
  $core.bool hasAmount() => $_has(1);
  @$pb.TagNumber(2)
  void clearAmount() => $_clearField(2);

  @$pb.TagNumber(3)
  $0.TransactionHistoryType get type => $_getN(2);
  @$pb.TagNumber(3)
  set type($0.TransactionHistoryType value) => $_setField(3, value);
  @$pb.TagNumber(3)
  $core.bool hasType() => $_has(2);
  @$pb.TagNumber(3)
  void clearType() => $_clearField(3);

  @$pb.TagNumber(4)
  $core.String get createdDateTime => $_getSZ(3);
  @$pb.TagNumber(4)
  set createdDateTime($core.String value) => $_setString(3, value);
  @$pb.TagNumber(4)
  $core.bool hasCreatedDateTime() => $_has(3);
  @$pb.TagNumber(4)
  void clearCreatedDateTime() => $_clearField(4);
}

const $core.bool _omitFieldNames =
    $core.bool.fromEnvironment('protobuf.omit_field_names');
const $core.bool _omitMessageNames =
    $core.bool.fromEnvironment('protobuf.omit_message_names');
