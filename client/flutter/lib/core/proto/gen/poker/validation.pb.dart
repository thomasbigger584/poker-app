// This is a generated file - do not edit.
//
// Generated from poker/validation.proto.

// @dart = 3.3

// ignore_for_file: annotate_overrides, camel_case_types, comment_references
// ignore_for_file: constant_identifier_names
// ignore_for_file: curly_braces_in_flow_control_structures
// ignore_for_file: deprecated_member_use_from_same_package, library_prefixes
// ignore_for_file: non_constant_identifier_names, prefer_relative_imports

import 'dart:core' as $core;

import 'package:protobuf/protobuf.dart' as $pb;

export 'package:protobuf/protobuf.dart' show GeneratedMessageGenericExtensions;

class ValidationFieldDTO extends $pb.GeneratedMessage {
  factory ValidationFieldDTO({
    $core.String? field_1,
    $core.String? message,
  }) {
    final result = create();
    if (field_1 != null) result.field_1 = field_1;
    if (message != null) result.message = message;
    return result;
  }

  ValidationFieldDTO._();

  factory ValidationFieldDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory ValidationFieldDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'ValidationFieldDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..aOS(1, _omitFieldNames ? '' : 'field')
    ..aOS(2, _omitFieldNames ? '' : 'message')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  ValidationFieldDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  ValidationFieldDTO copyWith(void Function(ValidationFieldDTO) updates) =>
      super.copyWith((message) => updates(message as ValidationFieldDTO))
          as ValidationFieldDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static ValidationFieldDTO create() => ValidationFieldDTO._();
  @$core.override
  ValidationFieldDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static ValidationFieldDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<ValidationFieldDTO>(create);
  static ValidationFieldDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get field_1 => $_getSZ(0);
  @$pb.TagNumber(1)
  set field_1($core.String value) => $_setString(0, value);
  @$pb.TagNumber(1)
  $core.bool hasField_1() => $_has(0);
  @$pb.TagNumber(1)
  void clearField_1() => $_clearField(1);

  @$pb.TagNumber(2)
  $core.String get message => $_getSZ(1);
  @$pb.TagNumber(2)
  set message($core.String value) => $_setString(1, value);
  @$pb.TagNumber(2)
  $core.bool hasMessage() => $_has(1);
  @$pb.TagNumber(2)
  void clearMessage() => $_clearField(2);
}

class ValidationDTO extends $pb.GeneratedMessage {
  factory ValidationDTO({
    $core.Iterable<ValidationFieldDTO>? fields,
  }) {
    final result = create();
    if (fields != null) result.fields.addAll(fields);
    return result;
  }

  ValidationDTO._();

  factory ValidationDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory ValidationDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'ValidationDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'poker'),
      createEmptyInstance: create)
    ..pPM<ValidationFieldDTO>(1, _omitFieldNames ? '' : 'fields',
        subBuilder: ValidationFieldDTO.create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  ValidationDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  ValidationDTO copyWith(void Function(ValidationDTO) updates) =>
      super.copyWith((message) => updates(message as ValidationDTO))
          as ValidationDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static ValidationDTO create() => ValidationDTO._();
  @$core.override
  ValidationDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static ValidationDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<ValidationDTO>(create);
  static ValidationDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $pb.PbList<ValidationFieldDTO> get fields => $_getList(0);
}

const $core.bool _omitFieldNames =
    $core.bool.fromEnvironment('protobuf.omit_field_names');
const $core.bool _omitMessageNames =
    $core.bool.fromEnvironment('protobuf.omit_message_names');
