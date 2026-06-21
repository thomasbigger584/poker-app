// This is a generated file - do not edit.
//
// Generated from poker/validation.proto.

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

@$core.Deprecated('Use validationFieldDTODescriptor instead')
const ValidationFieldDTO$json = {
  '1': 'ValidationFieldDTO',
  '2': [
    {'1': 'field', '3': 1, '4': 1, '5': 9, '10': 'field'},
    {'1': 'message', '3': 2, '4': 1, '5': 9, '10': 'message'},
  ],
};

/// Descriptor for `ValidationFieldDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List validationFieldDTODescriptor = $convert.base64Decode(
    'ChJWYWxpZGF0aW9uRmllbGREVE8SFAoFZmllbGQYASABKAlSBWZpZWxkEhgKB21lc3NhZ2UYAi'
    'ABKAlSB21lc3NhZ2U=');

@$core.Deprecated('Use validationDTODescriptor instead')
const ValidationDTO$json = {
  '1': 'ValidationDTO',
  '2': [
    {
      '1': 'fields',
      '3': 1,
      '4': 3,
      '5': 11,
      '6': '.poker.ValidationFieldDTO',
      '10': 'fields'
    },
  ],
};

/// Descriptor for `ValidationDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List validationDTODescriptor = $convert.base64Decode(
    'Cg1WYWxpZGF0aW9uRFRPEjEKBmZpZWxkcxgBIAMoCzIZLnBva2VyLlZhbGlkYXRpb25GaWVsZE'
    'RUT1IGZmllbGRz');
