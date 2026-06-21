// This is a generated file - do not edit.
//
// Generated from poker/websocket.proto.

// @dart = 3.3

// ignore_for_file: annotate_overrides, camel_case_types, comment_references
// ignore_for_file: constant_identifier_names
// ignore_for_file: curly_braces_in_flow_control_structures
// ignore_for_file: deprecated_member_use_from_same_package, library_prefixes
// ignore_for_file: non_constant_identifier_names, prefer_relative_imports

import 'dart:core' as $core;

import 'package:fixnum/fixnum.dart' as $fixnum;
import 'package:protobuf/protobuf.dart' as $pb;

import 'domain.pb.dart' as $1;
import 'enums.pbenum.dart' as $2;
import 'validation.pb.dart' as $0;

export 'package:protobuf/protobuf.dart' show GeneratedMessageGenericExtensions;

enum ServerMessageDTO_Payload {
  playerSubscribed,
  playerConnected,
  dealerDetermined,
  dealInit,
  dealCommunity,
  playerTurn,
  playerActioned,
  bettingRoundUpdated,
  roundFinished,
  gameFinished,
  chat,
  log,
  error,
  validation,
  playerDisconnected,
  notSet
}

class ServerMessageDTO extends $pb.GeneratedMessage {
  factory ServerMessageDTO({
    $fixnum.Int64? timestamp,
    PlayerSubscribedDTO? playerSubscribed,
    PlayerConnectedDTO? playerConnected,
    DealerDeterminedDTO? dealerDetermined,
    DealPlayerCardDTO? dealInit,
    DealCommunityCardDTO? dealCommunity,
    PlayerTurnDTO? playerTurn,
    PlayerActionedDTO? playerActioned,
    BettingRoundUpdatedDTO? bettingRoundUpdated,
    RoundFinishedDTO? roundFinished,
    GameFinishedDTO? gameFinished,
    ChatMessageDTO? chat,
    LogMessageDTO? log,
    ErrorMessageDTO? error,
    $0.ValidationDTO? validation,
    PlayerDisconnectedDTO? playerDisconnected,
  }) {
    final result = create();
    if (timestamp != null) result.timestamp = timestamp;
    if (playerSubscribed != null) result.playerSubscribed = playerSubscribed;
    if (playerConnected != null) result.playerConnected = playerConnected;
    if (dealerDetermined != null) result.dealerDetermined = dealerDetermined;
    if (dealInit != null) result.dealInit = dealInit;
    if (dealCommunity != null) result.dealCommunity = dealCommunity;
    if (playerTurn != null) result.playerTurn = playerTurn;
    if (playerActioned != null) result.playerActioned = playerActioned;
    if (bettingRoundUpdated != null)
      result.bettingRoundUpdated = bettingRoundUpdated;
    if (roundFinished != null) result.roundFinished = roundFinished;
    if (gameFinished != null) result.gameFinished = gameFinished;
    if (chat != null) result.chat = chat;
    if (log != null) result.log = log;
    if (error != null) result.error = error;
    if (validation != null) result.validation = validation;
    if (playerDisconnected != null)
      result.playerDisconnected = playerDisconnected;
    return result;
  }

  ServerMessageDTO._();

  factory ServerMessageDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory ServerMessageDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static const $core.Map<$core.int, ServerMessageDTO_Payload>
      _ServerMessageDTO_PayloadByTag = {
    2: ServerMessageDTO_Payload.playerSubscribed,
    3: ServerMessageDTO_Payload.playerConnected,
    4: ServerMessageDTO_Payload.dealerDetermined,
    5: ServerMessageDTO_Payload.dealInit,
    6: ServerMessageDTO_Payload.dealCommunity,
    7: ServerMessageDTO_Payload.playerTurn,
    8: ServerMessageDTO_Payload.playerActioned,
    9: ServerMessageDTO_Payload.bettingRoundUpdated,
    10: ServerMessageDTO_Payload.roundFinished,
    11: ServerMessageDTO_Payload.gameFinished,
    12: ServerMessageDTO_Payload.chat,
    13: ServerMessageDTO_Payload.log,
    14: ServerMessageDTO_Payload.error,
    15: ServerMessageDTO_Payload.validation,
    16: ServerMessageDTO_Payload.playerDisconnected,
    0: ServerMessageDTO_Payload.notSet
  };
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'ServerMessageDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..oo(0, [2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16])
    ..aInt64(1, _omitFieldNames ? '' : 'timestamp')
    ..aOM<PlayerSubscribedDTO>(2, _omitFieldNames ? '' : 'playerSubscribed',
        subBuilder: PlayerSubscribedDTO.create)
    ..aOM<PlayerConnectedDTO>(3, _omitFieldNames ? '' : 'playerConnected',
        subBuilder: PlayerConnectedDTO.create)
    ..aOM<DealerDeterminedDTO>(4, _omitFieldNames ? '' : 'dealerDetermined',
        subBuilder: DealerDeterminedDTO.create)
    ..aOM<DealPlayerCardDTO>(5, _omitFieldNames ? '' : 'dealInit',
        subBuilder: DealPlayerCardDTO.create)
    ..aOM<DealCommunityCardDTO>(6, _omitFieldNames ? '' : 'dealCommunity',
        subBuilder: DealCommunityCardDTO.create)
    ..aOM<PlayerTurnDTO>(7, _omitFieldNames ? '' : 'playerTurn',
        subBuilder: PlayerTurnDTO.create)
    ..aOM<PlayerActionedDTO>(8, _omitFieldNames ? '' : 'playerActioned',
        subBuilder: PlayerActionedDTO.create)
    ..aOM<BettingRoundUpdatedDTO>(
        9, _omitFieldNames ? '' : 'bettingRoundUpdated',
        subBuilder: BettingRoundUpdatedDTO.create)
    ..aOM<RoundFinishedDTO>(10, _omitFieldNames ? '' : 'roundFinished',
        subBuilder: RoundFinishedDTO.create)
    ..aOM<GameFinishedDTO>(11, _omitFieldNames ? '' : 'gameFinished',
        subBuilder: GameFinishedDTO.create)
    ..aOM<ChatMessageDTO>(12, _omitFieldNames ? '' : 'chat',
        subBuilder: ChatMessageDTO.create)
    ..aOM<LogMessageDTO>(13, _omitFieldNames ? '' : 'log',
        subBuilder: LogMessageDTO.create)
    ..aOM<ErrorMessageDTO>(14, _omitFieldNames ? '' : 'error',
        subBuilder: ErrorMessageDTO.create)
    ..aOM<$0.ValidationDTO>(15, _omitFieldNames ? '' : 'validation',
        subBuilder: $0.ValidationDTO.create)
    ..aOM<PlayerDisconnectedDTO>(
        16, _omitFieldNames ? '' : 'playerDisconnected',
        subBuilder: PlayerDisconnectedDTO.create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  ServerMessageDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  ServerMessageDTO copyWith(void Function(ServerMessageDTO) updates) =>
      super.copyWith((message) => updates(message as ServerMessageDTO))
          as ServerMessageDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static ServerMessageDTO create() => ServerMessageDTO._();
  @$core.override
  ServerMessageDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static ServerMessageDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<ServerMessageDTO>(create);
  static ServerMessageDTO? _defaultInstance;

  @$pb.TagNumber(2)
  @$pb.TagNumber(3)
  @$pb.TagNumber(4)
  @$pb.TagNumber(5)
  @$pb.TagNumber(6)
  @$pb.TagNumber(7)
  @$pb.TagNumber(8)
  @$pb.TagNumber(9)
  @$pb.TagNumber(10)
  @$pb.TagNumber(11)
  @$pb.TagNumber(12)
  @$pb.TagNumber(13)
  @$pb.TagNumber(14)
  @$pb.TagNumber(15)
  @$pb.TagNumber(16)
  ServerMessageDTO_Payload whichPayload() =>
      _ServerMessageDTO_PayloadByTag[$_whichOneof(0)]!;
  @$pb.TagNumber(2)
  @$pb.TagNumber(3)
  @$pb.TagNumber(4)
  @$pb.TagNumber(5)
  @$pb.TagNumber(6)
  @$pb.TagNumber(7)
  @$pb.TagNumber(8)
  @$pb.TagNumber(9)
  @$pb.TagNumber(10)
  @$pb.TagNumber(11)
  @$pb.TagNumber(12)
  @$pb.TagNumber(13)
  @$pb.TagNumber(14)
  @$pb.TagNumber(15)
  @$pb.TagNumber(16)
  void clearPayload() => $_clearField($_whichOneof(0));

  @$pb.TagNumber(1)
  $fixnum.Int64 get timestamp => $_getI64(0);
  @$pb.TagNumber(1)
  set timestamp($fixnum.Int64 value) => $_setInt64(0, value);
  @$pb.TagNumber(1)
  $core.bool hasTimestamp() => $_has(0);
  @$pb.TagNumber(1)
  void clearTimestamp() => $_clearField(1);

  @$pb.TagNumber(2)
  PlayerSubscribedDTO get playerSubscribed => $_getN(1);
  @$pb.TagNumber(2)
  set playerSubscribed(PlayerSubscribedDTO value) => $_setField(2, value);
  @$pb.TagNumber(2)
  $core.bool hasPlayerSubscribed() => $_has(1);
  @$pb.TagNumber(2)
  void clearPlayerSubscribed() => $_clearField(2);
  @$pb.TagNumber(2)
  PlayerSubscribedDTO ensurePlayerSubscribed() => $_ensure(1);

  @$pb.TagNumber(3)
  PlayerConnectedDTO get playerConnected => $_getN(2);
  @$pb.TagNumber(3)
  set playerConnected(PlayerConnectedDTO value) => $_setField(3, value);
  @$pb.TagNumber(3)
  $core.bool hasPlayerConnected() => $_has(2);
  @$pb.TagNumber(3)
  void clearPlayerConnected() => $_clearField(3);
  @$pb.TagNumber(3)
  PlayerConnectedDTO ensurePlayerConnected() => $_ensure(2);

  @$pb.TagNumber(4)
  DealerDeterminedDTO get dealerDetermined => $_getN(3);
  @$pb.TagNumber(4)
  set dealerDetermined(DealerDeterminedDTO value) => $_setField(4, value);
  @$pb.TagNumber(4)
  $core.bool hasDealerDetermined() => $_has(3);
  @$pb.TagNumber(4)
  void clearDealerDetermined() => $_clearField(4);
  @$pb.TagNumber(4)
  DealerDeterminedDTO ensureDealerDetermined() => $_ensure(3);

  @$pb.TagNumber(5)
  DealPlayerCardDTO get dealInit => $_getN(4);
  @$pb.TagNumber(5)
  set dealInit(DealPlayerCardDTO value) => $_setField(5, value);
  @$pb.TagNumber(5)
  $core.bool hasDealInit() => $_has(4);
  @$pb.TagNumber(5)
  void clearDealInit() => $_clearField(5);
  @$pb.TagNumber(5)
  DealPlayerCardDTO ensureDealInit() => $_ensure(4);

  @$pb.TagNumber(6)
  DealCommunityCardDTO get dealCommunity => $_getN(5);
  @$pb.TagNumber(6)
  set dealCommunity(DealCommunityCardDTO value) => $_setField(6, value);
  @$pb.TagNumber(6)
  $core.bool hasDealCommunity() => $_has(5);
  @$pb.TagNumber(6)
  void clearDealCommunity() => $_clearField(6);
  @$pb.TagNumber(6)
  DealCommunityCardDTO ensureDealCommunity() => $_ensure(5);

  @$pb.TagNumber(7)
  PlayerTurnDTO get playerTurn => $_getN(6);
  @$pb.TagNumber(7)
  set playerTurn(PlayerTurnDTO value) => $_setField(7, value);
  @$pb.TagNumber(7)
  $core.bool hasPlayerTurn() => $_has(6);
  @$pb.TagNumber(7)
  void clearPlayerTurn() => $_clearField(7);
  @$pb.TagNumber(7)
  PlayerTurnDTO ensurePlayerTurn() => $_ensure(6);

  @$pb.TagNumber(8)
  PlayerActionedDTO get playerActioned => $_getN(7);
  @$pb.TagNumber(8)
  set playerActioned(PlayerActionedDTO value) => $_setField(8, value);
  @$pb.TagNumber(8)
  $core.bool hasPlayerActioned() => $_has(7);
  @$pb.TagNumber(8)
  void clearPlayerActioned() => $_clearField(8);
  @$pb.TagNumber(8)
  PlayerActionedDTO ensurePlayerActioned() => $_ensure(7);

  @$pb.TagNumber(9)
  BettingRoundUpdatedDTO get bettingRoundUpdated => $_getN(8);
  @$pb.TagNumber(9)
  set bettingRoundUpdated(BettingRoundUpdatedDTO value) => $_setField(9, value);
  @$pb.TagNumber(9)
  $core.bool hasBettingRoundUpdated() => $_has(8);
  @$pb.TagNumber(9)
  void clearBettingRoundUpdated() => $_clearField(9);
  @$pb.TagNumber(9)
  BettingRoundUpdatedDTO ensureBettingRoundUpdated() => $_ensure(8);

  @$pb.TagNumber(10)
  RoundFinishedDTO get roundFinished => $_getN(9);
  @$pb.TagNumber(10)
  set roundFinished(RoundFinishedDTO value) => $_setField(10, value);
  @$pb.TagNumber(10)
  $core.bool hasRoundFinished() => $_has(9);
  @$pb.TagNumber(10)
  void clearRoundFinished() => $_clearField(10);
  @$pb.TagNumber(10)
  RoundFinishedDTO ensureRoundFinished() => $_ensure(9);

  @$pb.TagNumber(11)
  GameFinishedDTO get gameFinished => $_getN(10);
  @$pb.TagNumber(11)
  set gameFinished(GameFinishedDTO value) => $_setField(11, value);
  @$pb.TagNumber(11)
  $core.bool hasGameFinished() => $_has(10);
  @$pb.TagNumber(11)
  void clearGameFinished() => $_clearField(11);
  @$pb.TagNumber(11)
  GameFinishedDTO ensureGameFinished() => $_ensure(10);

  @$pb.TagNumber(12)
  ChatMessageDTO get chat => $_getN(11);
  @$pb.TagNumber(12)
  set chat(ChatMessageDTO value) => $_setField(12, value);
  @$pb.TagNumber(12)
  $core.bool hasChat() => $_has(11);
  @$pb.TagNumber(12)
  void clearChat() => $_clearField(12);
  @$pb.TagNumber(12)
  ChatMessageDTO ensureChat() => $_ensure(11);

  @$pb.TagNumber(13)
  LogMessageDTO get log => $_getN(12);
  @$pb.TagNumber(13)
  set log(LogMessageDTO value) => $_setField(13, value);
  @$pb.TagNumber(13)
  $core.bool hasLog() => $_has(12);
  @$pb.TagNumber(13)
  void clearLog() => $_clearField(13);
  @$pb.TagNumber(13)
  LogMessageDTO ensureLog() => $_ensure(12);

  @$pb.TagNumber(14)
  ErrorMessageDTO get error => $_getN(13);
  @$pb.TagNumber(14)
  set error(ErrorMessageDTO value) => $_setField(14, value);
  @$pb.TagNumber(14)
  $core.bool hasError() => $_has(13);
  @$pb.TagNumber(14)
  void clearError() => $_clearField(14);
  @$pb.TagNumber(14)
  ErrorMessageDTO ensureError() => $_ensure(13);

  @$pb.TagNumber(15)
  $0.ValidationDTO get validation => $_getN(14);
  @$pb.TagNumber(15)
  set validation($0.ValidationDTO value) => $_setField(15, value);
  @$pb.TagNumber(15)
  $core.bool hasValidation() => $_has(14);
  @$pb.TagNumber(15)
  void clearValidation() => $_clearField(15);
  @$pb.TagNumber(15)
  $0.ValidationDTO ensureValidation() => $_ensure(14);

  @$pb.TagNumber(16)
  PlayerDisconnectedDTO get playerDisconnected => $_getN(15);
  @$pb.TagNumber(16)
  set playerDisconnected(PlayerDisconnectedDTO value) => $_setField(16, value);
  @$pb.TagNumber(16)
  $core.bool hasPlayerDisconnected() => $_has(15);
  @$pb.TagNumber(16)
  void clearPlayerDisconnected() => $_clearField(16);
  @$pb.TagNumber(16)
  PlayerDisconnectedDTO ensurePlayerDisconnected() => $_ensure(15);
}

class PlayerSubscribedDTO extends $pb.GeneratedMessage {
  factory PlayerSubscribedDTO({
    $core.Iterable<$1.PlayerSessionDTO>? playerSessions,
    RoundStateDTO? roundState,
  }) {
    final result = create();
    if (playerSessions != null) result.playerSessions.addAll(playerSessions);
    if (roundState != null) result.roundState = roundState;
    return result;
  }

  PlayerSubscribedDTO._();

  factory PlayerSubscribedDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory PlayerSubscribedDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'PlayerSubscribedDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..pPM<$1.PlayerSessionDTO>(1, _omitFieldNames ? '' : 'playerSessions',
        subBuilder: $1.PlayerSessionDTO.create)
    ..aOM<RoundStateDTO>(2, _omitFieldNames ? '' : 'roundState',
        subBuilder: RoundStateDTO.create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  PlayerSubscribedDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  PlayerSubscribedDTO copyWith(void Function(PlayerSubscribedDTO) updates) =>
      super.copyWith((message) => updates(message as PlayerSubscribedDTO))
          as PlayerSubscribedDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static PlayerSubscribedDTO create() => PlayerSubscribedDTO._();
  @$core.override
  PlayerSubscribedDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static PlayerSubscribedDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<PlayerSubscribedDTO>(create);
  static PlayerSubscribedDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $pb.PbList<$1.PlayerSessionDTO> get playerSessions => $_getList(0);

  /// In-progress hand state so a (re)subscribing client resumes mid-hand.
  /// Absent when no hand is currently being played.
  @$pb.TagNumber(2)
  RoundStateDTO get roundState => $_getN(1);
  @$pb.TagNumber(2)
  set roundState(RoundStateDTO value) => $_setField(2, value);
  @$pb.TagNumber(2)
  $core.bool hasRoundState() => $_has(1);
  @$pb.TagNumber(2)
  void clearRoundState() => $_clearField(2);
  @$pb.TagNumber(2)
  RoundStateDTO ensureRoundState() => $_ensure(1);
}

class PlayerConnectedDTO extends $pb.GeneratedMessage {
  factory PlayerConnectedDTO({
    $1.PlayerSessionDTO? playerSession,
  }) {
    final result = create();
    if (playerSession != null) result.playerSession = playerSession;
    return result;
  }

  PlayerConnectedDTO._();

  factory PlayerConnectedDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory PlayerConnectedDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'PlayerConnectedDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOM<$1.PlayerSessionDTO>(1, _omitFieldNames ? '' : 'playerSession',
        subBuilder: $1.PlayerSessionDTO.create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  PlayerConnectedDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  PlayerConnectedDTO copyWith(void Function(PlayerConnectedDTO) updates) =>
      super.copyWith((message) => updates(message as PlayerConnectedDTO))
          as PlayerConnectedDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static PlayerConnectedDTO create() => PlayerConnectedDTO._();
  @$core.override
  PlayerConnectedDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static PlayerConnectedDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<PlayerConnectedDTO>(create);
  static PlayerConnectedDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $1.PlayerSessionDTO get playerSession => $_getN(0);
  @$pb.TagNumber(1)
  set playerSession($1.PlayerSessionDTO value) => $_setField(1, value);
  @$pb.TagNumber(1)
  $core.bool hasPlayerSession() => $_has(0);
  @$pb.TagNumber(1)
  void clearPlayerSession() => $_clearField(1);
  @$pb.TagNumber(1)
  $1.PlayerSessionDTO ensurePlayerSession() => $_ensure(0);
}

class DealerDeterminedDTO extends $pb.GeneratedMessage {
  factory DealerDeterminedDTO({
    $1.PlayerSessionDTO? playerSession,
  }) {
    final result = create();
    if (playerSession != null) result.playerSession = playerSession;
    return result;
  }

  DealerDeterminedDTO._();

  factory DealerDeterminedDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory DealerDeterminedDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'DealerDeterminedDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOM<$1.PlayerSessionDTO>(1, _omitFieldNames ? '' : 'playerSession',
        subBuilder: $1.PlayerSessionDTO.create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  DealerDeterminedDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  DealerDeterminedDTO copyWith(void Function(DealerDeterminedDTO) updates) =>
      super.copyWith((message) => updates(message as DealerDeterminedDTO))
          as DealerDeterminedDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static DealerDeterminedDTO create() => DealerDeterminedDTO._();
  @$core.override
  DealerDeterminedDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static DealerDeterminedDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<DealerDeterminedDTO>(create);
  static DealerDeterminedDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $1.PlayerSessionDTO get playerSession => $_getN(0);
  @$pb.TagNumber(1)
  set playerSession($1.PlayerSessionDTO value) => $_setField(1, value);
  @$pb.TagNumber(1)
  $core.bool hasPlayerSession() => $_has(0);
  @$pb.TagNumber(1)
  void clearPlayerSession() => $_clearField(1);
  @$pb.TagNumber(1)
  $1.PlayerSessionDTO ensurePlayerSession() => $_ensure(0);
}

class DealPlayerCardDTO extends $pb.GeneratedMessage {
  factory DealPlayerCardDTO({
    $1.PlayerSessionDTO? playerSession,
    $1.CardDTO? card,
  }) {
    final result = create();
    if (playerSession != null) result.playerSession = playerSession;
    if (card != null) result.card = card;
    return result;
  }

  DealPlayerCardDTO._();

  factory DealPlayerCardDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory DealPlayerCardDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'DealPlayerCardDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOM<$1.PlayerSessionDTO>(1, _omitFieldNames ? '' : 'playerSession',
        subBuilder: $1.PlayerSessionDTO.create)
    ..aOM<$1.CardDTO>(2, _omitFieldNames ? '' : 'card',
        subBuilder: $1.CardDTO.create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  DealPlayerCardDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  DealPlayerCardDTO copyWith(void Function(DealPlayerCardDTO) updates) =>
      super.copyWith((message) => updates(message as DealPlayerCardDTO))
          as DealPlayerCardDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static DealPlayerCardDTO create() => DealPlayerCardDTO._();
  @$core.override
  DealPlayerCardDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static DealPlayerCardDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<DealPlayerCardDTO>(create);
  static DealPlayerCardDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $1.PlayerSessionDTO get playerSession => $_getN(0);
  @$pb.TagNumber(1)
  set playerSession($1.PlayerSessionDTO value) => $_setField(1, value);
  @$pb.TagNumber(1)
  $core.bool hasPlayerSession() => $_has(0);
  @$pb.TagNumber(1)
  void clearPlayerSession() => $_clearField(1);
  @$pb.TagNumber(1)
  $1.PlayerSessionDTO ensurePlayerSession() => $_ensure(0);

  @$pb.TagNumber(2)
  $1.CardDTO get card => $_getN(1);
  @$pb.TagNumber(2)
  set card($1.CardDTO value) => $_setField(2, value);
  @$pb.TagNumber(2)
  $core.bool hasCard() => $_has(1);
  @$pb.TagNumber(2)
  void clearCard() => $_clearField(2);
  @$pb.TagNumber(2)
  $1.CardDTO ensureCard() => $_ensure(1);
}

class DealCommunityCardDTO extends $pb.GeneratedMessage {
  factory DealCommunityCardDTO({
    $1.CardDTO? card,
  }) {
    final result = create();
    if (card != null) result.card = card;
    return result;
  }

  DealCommunityCardDTO._();

  factory DealCommunityCardDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory DealCommunityCardDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'DealCommunityCardDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOM<$1.CardDTO>(1, _omitFieldNames ? '' : 'card',
        subBuilder: $1.CardDTO.create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  DealCommunityCardDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  DealCommunityCardDTO copyWith(void Function(DealCommunityCardDTO) updates) =>
      super.copyWith((message) => updates(message as DealCommunityCardDTO))
          as DealCommunityCardDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static DealCommunityCardDTO create() => DealCommunityCardDTO._();
  @$core.override
  DealCommunityCardDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static DealCommunityCardDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<DealCommunityCardDTO>(create);
  static DealCommunityCardDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $1.CardDTO get card => $_getN(0);
  @$pb.TagNumber(1)
  set card($1.CardDTO value) => $_setField(1, value);
  @$pb.TagNumber(1)
  $core.bool hasCard() => $_has(0);
  @$pb.TagNumber(1)
  void clearCard() => $_clearField(1);
  @$pb.TagNumber(1)
  $1.CardDTO ensureCard() => $_ensure(0);
}

class PlayerTurnDTO extends $pb.GeneratedMessage {
  factory PlayerTurnDTO({
    $1.PlayerSessionDTO? playerSession,
    $1.BettingRoundDTO? bettingRound,
    $core.Iterable<$2.ActionType>? nextActions,
    $core.String? amountToCall,
    $fixnum.Int64? playerTurnWaitMs,
  }) {
    final result = create();
    if (playerSession != null) result.playerSession = playerSession;
    if (bettingRound != null) result.bettingRound = bettingRound;
    if (nextActions != null) result.nextActions.addAll(nextActions);
    if (amountToCall != null) result.amountToCall = amountToCall;
    if (playerTurnWaitMs != null) result.playerTurnWaitMs = playerTurnWaitMs;
    return result;
  }

  PlayerTurnDTO._();

  factory PlayerTurnDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory PlayerTurnDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'PlayerTurnDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOM<$1.PlayerSessionDTO>(1, _omitFieldNames ? '' : 'playerSession',
        subBuilder: $1.PlayerSessionDTO.create)
    ..aOM<$1.BettingRoundDTO>(2, _omitFieldNames ? '' : 'bettingRound',
        subBuilder: $1.BettingRoundDTO.create)
    ..pc<$2.ActionType>(
        3, _omitFieldNames ? '' : 'nextActions', $pb.PbFieldType.KE,
        valueOf: $2.ActionType.valueOf,
        enumValues: $2.ActionType.values,
        defaultEnumValue: $2.ActionType.ACTION_TYPE_UNSPECIFIED)
    ..aOS(4, _omitFieldNames ? '' : 'amountToCall')
    ..aInt64(5, _omitFieldNames ? '' : 'playerTurnWaitMs')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  PlayerTurnDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  PlayerTurnDTO copyWith(void Function(PlayerTurnDTO) updates) =>
      super.copyWith((message) => updates(message as PlayerTurnDTO))
          as PlayerTurnDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static PlayerTurnDTO create() => PlayerTurnDTO._();
  @$core.override
  PlayerTurnDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static PlayerTurnDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<PlayerTurnDTO>(create);
  static PlayerTurnDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $1.PlayerSessionDTO get playerSession => $_getN(0);
  @$pb.TagNumber(1)
  set playerSession($1.PlayerSessionDTO value) => $_setField(1, value);
  @$pb.TagNumber(1)
  $core.bool hasPlayerSession() => $_has(0);
  @$pb.TagNumber(1)
  void clearPlayerSession() => $_clearField(1);
  @$pb.TagNumber(1)
  $1.PlayerSessionDTO ensurePlayerSession() => $_ensure(0);

  @$pb.TagNumber(2)
  $1.BettingRoundDTO get bettingRound => $_getN(1);
  @$pb.TagNumber(2)
  set bettingRound($1.BettingRoundDTO value) => $_setField(2, value);
  @$pb.TagNumber(2)
  $core.bool hasBettingRound() => $_has(1);
  @$pb.TagNumber(2)
  void clearBettingRound() => $_clearField(2);
  @$pb.TagNumber(2)
  $1.BettingRoundDTO ensureBettingRound() => $_ensure(1);

  @$pb.TagNumber(3)
  $pb.PbList<$2.ActionType> get nextActions => $_getList(2);

  @$pb.TagNumber(4)
  $core.String get amountToCall => $_getSZ(3);
  @$pb.TagNumber(4)
  set amountToCall($core.String value) => $_setString(3, value);
  @$pb.TagNumber(4)
  $core.bool hasAmountToCall() => $_has(3);
  @$pb.TagNumber(4)
  void clearAmountToCall() => $_clearField(4);

  @$pb.TagNumber(5)
  $fixnum.Int64 get playerTurnWaitMs => $_getI64(4);
  @$pb.TagNumber(5)
  set playerTurnWaitMs($fixnum.Int64 value) => $_setInt64(4, value);
  @$pb.TagNumber(5)
  $core.bool hasPlayerTurnWaitMs() => $_has(4);
  @$pb.TagNumber(5)
  void clearPlayerTurnWaitMs() => $_clearField(5);
}

class PlayerActionedDTO extends $pb.GeneratedMessage {
  factory PlayerActionedDTO({
    $1.PlayerActionDTO? action,
  }) {
    final result = create();
    if (action != null) result.action = action;
    return result;
  }

  PlayerActionedDTO._();

  factory PlayerActionedDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory PlayerActionedDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'PlayerActionedDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOM<$1.PlayerActionDTO>(1, _omitFieldNames ? '' : 'action',
        subBuilder: $1.PlayerActionDTO.create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  PlayerActionedDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  PlayerActionedDTO copyWith(void Function(PlayerActionedDTO) updates) =>
      super.copyWith((message) => updates(message as PlayerActionedDTO))
          as PlayerActionedDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static PlayerActionedDTO create() => PlayerActionedDTO._();
  @$core.override
  PlayerActionedDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static PlayerActionedDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<PlayerActionedDTO>(create);
  static PlayerActionedDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $1.PlayerActionDTO get action => $_getN(0);
  @$pb.TagNumber(1)
  set action($1.PlayerActionDTO value) => $_setField(1, value);
  @$pb.TagNumber(1)
  $core.bool hasAction() => $_has(0);
  @$pb.TagNumber(1)
  void clearAction() => $_clearField(1);
  @$pb.TagNumber(1)
  $1.PlayerActionDTO ensureAction() => $_ensure(0);
}

class BettingRoundUpdatedDTO extends $pb.GeneratedMessage {
  factory BettingRoundUpdatedDTO({
    $1.RoundDTO? round,
    $1.BettingRoundDTO? bettingRound,
    $core.Iterable<$1.RoundPotDTO>? roundPots,
  }) {
    final result = create();
    if (round != null) result.round = round;
    if (bettingRound != null) result.bettingRound = bettingRound;
    if (roundPots != null) result.roundPots.addAll(roundPots);
    return result;
  }

  BettingRoundUpdatedDTO._();

  factory BettingRoundUpdatedDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory BettingRoundUpdatedDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'BettingRoundUpdatedDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOM<$1.RoundDTO>(1, _omitFieldNames ? '' : 'round',
        subBuilder: $1.RoundDTO.create)
    ..aOM<$1.BettingRoundDTO>(2, _omitFieldNames ? '' : 'bettingRound',
        subBuilder: $1.BettingRoundDTO.create)
    ..pPM<$1.RoundPotDTO>(3, _omitFieldNames ? '' : 'roundPots',
        subBuilder: $1.RoundPotDTO.create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  BettingRoundUpdatedDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  BettingRoundUpdatedDTO copyWith(
          void Function(BettingRoundUpdatedDTO) updates) =>
      super.copyWith((message) => updates(message as BettingRoundUpdatedDTO))
          as BettingRoundUpdatedDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static BettingRoundUpdatedDTO create() => BettingRoundUpdatedDTO._();
  @$core.override
  BettingRoundUpdatedDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static BettingRoundUpdatedDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<BettingRoundUpdatedDTO>(create);
  static BettingRoundUpdatedDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $1.RoundDTO get round => $_getN(0);
  @$pb.TagNumber(1)
  set round($1.RoundDTO value) => $_setField(1, value);
  @$pb.TagNumber(1)
  $core.bool hasRound() => $_has(0);
  @$pb.TagNumber(1)
  void clearRound() => $_clearField(1);
  @$pb.TagNumber(1)
  $1.RoundDTO ensureRound() => $_ensure(0);

  @$pb.TagNumber(2)
  $1.BettingRoundDTO get bettingRound => $_getN(1);
  @$pb.TagNumber(2)
  set bettingRound($1.BettingRoundDTO value) => $_setField(2, value);
  @$pb.TagNumber(2)
  $core.bool hasBettingRound() => $_has(1);
  @$pb.TagNumber(2)
  void clearBettingRound() => $_clearField(2);
  @$pb.TagNumber(2)
  $1.BettingRoundDTO ensureBettingRound() => $_ensure(1);

  @$pb.TagNumber(3)
  $pb.PbList<$1.RoundPotDTO> get roundPots => $_getList(2);
}

class RoundFinishedDTO extends $pb.GeneratedMessage {
  factory RoundFinishedDTO({
    $core.Iterable<$1.RoundWinnerDTO>? winners,
  }) {
    final result = create();
    if (winners != null) result.winners.addAll(winners);
    return result;
  }

  RoundFinishedDTO._();

  factory RoundFinishedDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory RoundFinishedDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'RoundFinishedDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..pPM<$1.RoundWinnerDTO>(1, _omitFieldNames ? '' : 'winners',
        subBuilder: $1.RoundWinnerDTO.create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  RoundFinishedDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  RoundFinishedDTO copyWith(void Function(RoundFinishedDTO) updates) =>
      super.copyWith((message) => updates(message as RoundFinishedDTO))
          as RoundFinishedDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static RoundFinishedDTO create() => RoundFinishedDTO._();
  @$core.override
  RoundFinishedDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static RoundFinishedDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<RoundFinishedDTO>(create);
  static RoundFinishedDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $pb.PbList<$1.RoundWinnerDTO> get winners => $_getList(0);
}

class GameFinishedDTO extends $pb.GeneratedMessage {
  factory GameFinishedDTO() => create();

  GameFinishedDTO._();

  factory GameFinishedDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory GameFinishedDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'GameFinishedDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  GameFinishedDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  GameFinishedDTO copyWith(void Function(GameFinishedDTO) updates) =>
      super.copyWith((message) => updates(message as GameFinishedDTO))
          as GameFinishedDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static GameFinishedDTO create() => GameFinishedDTO._();
  @$core.override
  GameFinishedDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static GameFinishedDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<GameFinishedDTO>(create);
  static GameFinishedDTO? _defaultInstance;
}

class ChatMessageDTO extends $pb.GeneratedMessage {
  factory ChatMessageDTO({
    $core.String? username,
    $core.String? message,
  }) {
    final result = create();
    if (username != null) result.username = username;
    if (message != null) result.message = message;
    return result;
  }

  ChatMessageDTO._();

  factory ChatMessageDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory ChatMessageDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'ChatMessageDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'username')
    ..aOS(2, _omitFieldNames ? '' : 'message')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  ChatMessageDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  ChatMessageDTO copyWith(void Function(ChatMessageDTO) updates) =>
      super.copyWith((message) => updates(message as ChatMessageDTO))
          as ChatMessageDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static ChatMessageDTO create() => ChatMessageDTO._();
  @$core.override
  ChatMessageDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static ChatMessageDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<ChatMessageDTO>(create);
  static ChatMessageDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get username => $_getSZ(0);
  @$pb.TagNumber(1)
  set username($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasUsername() => $_has(0);
  @$pb.TagNumber(1)
  void clearUsername() => $_clearField(1);

  @$pb.TagNumber(2)
  $core.String get message => $_getSZ(1);
  @$pb.TagNumber(2)
  set message($core.String value) => $_setString(1, value);
  @$pb.TagNumber(2)
  $core.bool hasMessage() => $_has(1);
  @$pb.TagNumber(2)
  void clearMessage() => $_clearField(2);
}

class LogMessageDTO extends $pb.GeneratedMessage {
  factory LogMessageDTO({
    $core.String? message,
  }) {
    final result = create();
    if (message != null) result.message = message;
    return result;
  }

  LogMessageDTO._();

  factory LogMessageDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory LogMessageDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'LogMessageDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'message')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  LogMessageDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  LogMessageDTO copyWith(void Function(LogMessageDTO) updates) =>
      super.copyWith((message) => updates(message as LogMessageDTO))
          as LogMessageDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static LogMessageDTO create() => LogMessageDTO._();
  @$core.override
  LogMessageDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static LogMessageDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<LogMessageDTO>(create);
  static LogMessageDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get message => $_getSZ(0);
  @$pb.TagNumber(1)
  set message($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasMessage() => $_has(0);
  @$pb.TagNumber(1)
  void clearMessage() => $_clearField(1);
}

class ErrorMessageDTO extends $pb.GeneratedMessage {
  factory ErrorMessageDTO({
    $core.String? message,
  }) {
    final result = create();
    if (message != null) result.message = message;
    return result;
  }

  ErrorMessageDTO._();

  factory ErrorMessageDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory ErrorMessageDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'ErrorMessageDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'message')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  ErrorMessageDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  ErrorMessageDTO copyWith(void Function(ErrorMessageDTO) updates) =>
      super.copyWith((message) => updates(message as ErrorMessageDTO))
          as ErrorMessageDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static ErrorMessageDTO create() => ErrorMessageDTO._();
  @$core.override
  ErrorMessageDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static ErrorMessageDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<ErrorMessageDTO>(create);
  static ErrorMessageDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get message => $_getSZ(0);
  @$pb.TagNumber(1)
  set message($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasMessage() => $_has(0);
  @$pb.TagNumber(1)
  void clearMessage() => $_clearField(1);
}

class PlayerDisconnectedDTO extends $pb.GeneratedMessage {
  factory PlayerDisconnectedDTO({
    $core.String? username,
  }) {
    final result = create();
    if (username != null) result.username = username;
    return result;
  }

  PlayerDisconnectedDTO._();

  factory PlayerDisconnectedDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory PlayerDisconnectedDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'PlayerDisconnectedDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'username')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  PlayerDisconnectedDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  PlayerDisconnectedDTO copyWith(
          void Function(PlayerDisconnectedDTO) updates) =>
      super.copyWith((message) => updates(message as PlayerDisconnectedDTO))
          as PlayerDisconnectedDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static PlayerDisconnectedDTO create() => PlayerDisconnectedDTO._();
  @$core.override
  PlayerDisconnectedDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static PlayerDisconnectedDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<PlayerDisconnectedDTO>(create);
  static PlayerDisconnectedDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get username => $_getSZ(0);
  @$pb.TagNumber(1)
  set username($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasUsername() => $_has(0);
  @$pb.TagNumber(1)
  void clearUsername() => $_clearField(1);
}

/// Snapshot of the in-progress hand, sent with PlayerSubscribedDTO when a
/// client (re)subscribes mid-round so it can re-render exactly where it
/// left off (cards on the table, pot, whose turn).
class RoundStateDTO extends $pb.GeneratedMessage {
  factory RoundStateDTO({
    $1.RoundDTO? round,
    $1.PlayerSessionDTO? dealer,
    $core.Iterable<DealPlayerCardDTO>? playerCards,
    $core.Iterable<$1.CardDTO>? communityCards,
    $1.BettingRoundDTO? bettingRound,
    $core.Iterable<$1.RoundPotDTO>? roundPots,
    $core.Iterable<$1.PlayerSessionDTO>? foldedPlayers,
    PlayerTurnDTO? currentTurn,
  }) {
    final result = create();
    if (round != null) result.round = round;
    if (dealer != null) result.dealer = dealer;
    if (playerCards != null) result.playerCards.addAll(playerCards);
    if (communityCards != null) result.communityCards.addAll(communityCards);
    if (bettingRound != null) result.bettingRound = bettingRound;
    if (roundPots != null) result.roundPots.addAll(roundPots);
    if (foldedPlayers != null) result.foldedPlayers.addAll(foldedPlayers);
    if (currentTurn != null) result.currentTurn = currentTurn;
    return result;
  }

  RoundStateDTO._();

  factory RoundStateDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory RoundStateDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'RoundStateDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOM<$1.RoundDTO>(1, _omitFieldNames ? '' : 'round',
        subBuilder: $1.RoundDTO.create)
    ..aOM<$1.PlayerSessionDTO>(2, _omitFieldNames ? '' : 'dealer',
        subBuilder: $1.PlayerSessionDTO.create)
    ..pPM<DealPlayerCardDTO>(3, _omitFieldNames ? '' : 'playerCards',
        subBuilder: DealPlayerCardDTO.create)
    ..pPM<$1.CardDTO>(4, _omitFieldNames ? '' : 'communityCards',
        subBuilder: $1.CardDTO.create)
    ..aOM<$1.BettingRoundDTO>(5, _omitFieldNames ? '' : 'bettingRound',
        subBuilder: $1.BettingRoundDTO.create)
    ..pPM<$1.RoundPotDTO>(6, _omitFieldNames ? '' : 'roundPots',
        subBuilder: $1.RoundPotDTO.create)
    ..pPM<$1.PlayerSessionDTO>(7, _omitFieldNames ? '' : 'foldedPlayers',
        subBuilder: $1.PlayerSessionDTO.create)
    ..aOM<PlayerTurnDTO>(8, _omitFieldNames ? '' : 'currentTurn',
        subBuilder: PlayerTurnDTO.create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  RoundStateDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  RoundStateDTO copyWith(void Function(RoundStateDTO) updates) =>
      super.copyWith((message) => updates(message as RoundStateDTO))
          as RoundStateDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static RoundStateDTO create() => RoundStateDTO._();
  @$core.override
  RoundStateDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static RoundStateDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<RoundStateDTO>(create);
  static RoundStateDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $1.RoundDTO get round => $_getN(0);
  @$pb.TagNumber(1)
  set round($1.RoundDTO value) => $_setField(1, value);
  @$pb.TagNumber(1)
  $core.bool hasRound() => $_has(0);
  @$pb.TagNumber(1)
  void clearRound() => $_clearField(1);
  @$pb.TagNumber(1)
  $1.RoundDTO ensureRound() => $_ensure(0);

  @$pb.TagNumber(2)
  $1.PlayerSessionDTO get dealer => $_getN(1);
  @$pb.TagNumber(2)
  set dealer($1.PlayerSessionDTO value) => $_setField(2, value);
  @$pb.TagNumber(2)
  $core.bool hasDealer() => $_has(1);
  @$pb.TagNumber(2)
  void clearDealer() => $_clearField(2);
  @$pb.TagNumber(2)
  $1.PlayerSessionDTO ensureDealer() => $_ensure(1);

  @$pb.TagNumber(3)
  $pb.PbList<DealPlayerCardDTO> get playerCards => $_getList(2);

  @$pb.TagNumber(4)
  $pb.PbList<$1.CardDTO> get communityCards => $_getList(3);

  @$pb.TagNumber(5)
  $1.BettingRoundDTO get bettingRound => $_getN(4);
  @$pb.TagNumber(5)
  set bettingRound($1.BettingRoundDTO value) => $_setField(5, value);
  @$pb.TagNumber(5)
  $core.bool hasBettingRound() => $_has(4);
  @$pb.TagNumber(5)
  void clearBettingRound() => $_clearField(5);
  @$pb.TagNumber(5)
  $1.BettingRoundDTO ensureBettingRound() => $_ensure(4);

  @$pb.TagNumber(6)
  $pb.PbList<$1.RoundPotDTO> get roundPots => $_getList(5);

  /// Players dealt into this hand but no longer active (folded / dropped).
  @$pb.TagNumber(7)
  $pb.PbList<$1.PlayerSessionDTO> get foldedPlayers => $_getList(6);

  @$pb.TagNumber(8)
  PlayerTurnDTO get currentTurn => $_getN(7);
  @$pb.TagNumber(8)
  set currentTurn(PlayerTurnDTO value) => $_setField(8, value);
  @$pb.TagNumber(8)
  $core.bool hasCurrentTurn() => $_has(7);
  @$pb.TagNumber(8)
  void clearCurrentTurn() => $_clearField(8);
  @$pb.TagNumber(8)
  PlayerTurnDTO ensureCurrentTurn() => $_ensure(7);
}

/// /app/pokerTable.{tableId}.sendPlayerAction
class CreatePlayerActionDTO extends $pb.GeneratedMessage {
  factory CreatePlayerActionDTO({
    $2.ActionType? action,
    $core.String? amount,
  }) {
    final result = create();
    if (action != null) result.action = action;
    if (amount != null) result.amount = amount;
    return result;
  }

  CreatePlayerActionDTO._();

  factory CreatePlayerActionDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory CreatePlayerActionDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'CreatePlayerActionDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aE<$2.ActionType>(1, _omitFieldNames ? '' : 'action',
        enumValues: $2.ActionType.values)
    ..aOS(2, _omitFieldNames ? '' : 'amount')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  CreatePlayerActionDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  CreatePlayerActionDTO copyWith(
          void Function(CreatePlayerActionDTO) updates) =>
      super.copyWith((message) => updates(message as CreatePlayerActionDTO))
          as CreatePlayerActionDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static CreatePlayerActionDTO create() => CreatePlayerActionDTO._();
  @$core.override
  CreatePlayerActionDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static CreatePlayerActionDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<CreatePlayerActionDTO>(create);
  static CreatePlayerActionDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $2.ActionType get action => $_getN(0);
  @$pb.TagNumber(1)
  set action($2.ActionType value) => $_setField(1, value);
  @$pb.TagNumber(1)
  $core.bool hasAction() => $_has(0);
  @$pb.TagNumber(1)
  void clearAction() => $_clearField(1);

  @$pb.TagNumber(2)
  $core.String get amount => $_getSZ(1);
  @$pb.TagNumber(2)
  set amount($core.String value) => $_setString(1, value);
  @$pb.TagNumber(2)
  $core.bool hasAmount() => $_has(1);
  @$pb.TagNumber(2)
  void clearAmount() => $_clearField(2);
}

/// /app/pokerTable.{tableId}.sendChatMessage
class CreateChatMessageDTO extends $pb.GeneratedMessage {
  factory CreateChatMessageDTO({
    $core.String? message,
  }) {
    final result = create();
    if (message != null) result.message = message;
    return result;
  }

  CreateChatMessageDTO._();

  factory CreateChatMessageDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory CreateChatMessageDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'CreateChatMessageDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'message')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  CreateChatMessageDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  CreateChatMessageDTO copyWith(void Function(CreateChatMessageDTO) updates) =>
      super.copyWith((message) => updates(message as CreateChatMessageDTO))
          as CreateChatMessageDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static CreateChatMessageDTO create() => CreateChatMessageDTO._();
  @$core.override
  CreateChatMessageDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static CreateChatMessageDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<CreateChatMessageDTO>(create);
  static CreateChatMessageDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get message => $_getSZ(0);
  @$pb.TagNumber(1)
  set message($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasMessage() => $_has(0);
  @$pb.TagNumber(1)
  void clearMessage() => $_clearField(1);
}

/// /app/pokerTable.{tableId}.sendBotConnected
class CreateBotConnectionDTO extends $pb.GeneratedMessage {
  factory CreateBotConnectionDTO({
    $core.String? botUserId,
    $core.String? buyInAmount,
  }) {
    final result = create();
    if (botUserId != null) result.botUserId = botUserId;
    if (buyInAmount != null) result.buyInAmount = buyInAmount;
    return result;
  }

  CreateBotConnectionDTO._();

  factory CreateBotConnectionDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory CreateBotConnectionDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'CreateBotConnectionDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'botUserId')
    ..aOS(2, _omitFieldNames ? '' : 'buyInAmount')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  CreateBotConnectionDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  CreateBotConnectionDTO copyWith(
          void Function(CreateBotConnectionDTO) updates) =>
      super.copyWith((message) => updates(message as CreateBotConnectionDTO))
          as CreateBotConnectionDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static CreateBotConnectionDTO create() => CreateBotConnectionDTO._();
  @$core.override
  CreateBotConnectionDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static CreateBotConnectionDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<CreateBotConnectionDTO>(create);
  static CreateBotConnectionDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get botUserId => $_getSZ(0);
  @$pb.TagNumber(1)
  set botUserId($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasBotUserId() => $_has(0);
  @$pb.TagNumber(1)
  void clearBotUserId() => $_clearField(1);

  @$pb.TagNumber(2)
  $core.String get buyInAmount => $_getSZ(1);
  @$pb.TagNumber(2)
  set buyInAmount($core.String value) => $_setString(1, value);
  @$pb.TagNumber(2)
  $core.bool hasBuyInAmount() => $_has(1);
  @$pb.TagNumber(2)
  void clearBuyInAmount() => $_clearField(2);
}

const $core.bool _omitFieldNames =
    $core.bool.fromEnvironment('protobuf.omit_field_names');
const $core.bool _omitMessageNames =
    $core.bool.fromEnvironment('protobuf.omit_message_names');
