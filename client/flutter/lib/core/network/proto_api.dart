import 'dart:typed_data';

import 'package:dio/dio.dart';
import 'package:protobuf/protobuf.dart';

import '../proto/gen/poker/rest.pb.dart';
import '../proto/gen/poker/validation.pb.dart';
import 'api_exception.dart';

/// Thin protobuf-over-HTTP client wrapping [Dio].
///
/// The backend serves every REST endpoint as binary protobuf
/// (`application/x-protobuf`) from the same generated types the server and
/// Android clients use (see `ProtobufWebConfig`). This helper encodes request
/// messages to bytes, decodes responses with the caller-supplied parser, and
/// translates the server's protobuf error bodies into [ApiException].
class ProtoApi {
  ProtoApi(this._dio);

  final Dio _dio;

  static const String _contentType = 'application/x-protobuf';

  /// GETs [path] and decodes the binary protobuf body with [parse].
  Future<T> getMessage<T extends GeneratedMessage>(
    String path, {
    Map<String, dynamic>? query,
    required T Function(List<int> bytes) parse,
  }) {
    return _send(
      () => _dio.get<List<int>>(
        path,
        queryParameters: query,
        options: Options(responseType: ResponseType.bytes),
      ),
      parse,
    );
  }

  /// POSTs an optional protobuf [body] to [path] and decodes the response.
  Future<T> postMessage<T extends GeneratedMessage>(
    String path, {
    GeneratedMessage? body,
    required T Function(List<int> bytes) parse,
  }) {
    return _send(
      () => _dio.post<List<int>>(
        path,
        data: body?.writeToBuffer(),
        options: Options(
          responseType: ResponseType.bytes,
          contentType: _contentType,
        ),
      ),
      parse,
    );
  }

  Future<T> _send<T extends GeneratedMessage>(
    Future<Response<List<int>>> Function() request,
    T Function(List<int> bytes) parse,
  ) async {
    final Response<List<int>> response;
    try {
      response = await request();
    } on DioException catch (e) {
      // 5xx (validateStatus lets <500 through) and transport errors land here.
      throw _fromDioException(e);
    }

    final code = response.statusCode ?? 0;
    final bytes = response.data ?? const <int>[];
    if (code >= 200 && code < 300) {
      return parse(bytes);
    }
    throw _decodeError(code, bytes);
  }

  ApiException _fromDioException(DioException e) {
    final response = e.response;
    if (response != null) {
      final data = response.data;
      final bytes = data is List<int> ? data : const <int>[];
      return _decodeError(response.statusCode ?? 0, bytes);
    }
    return ApiException(
      statusCode: 0,
      message: switch (e.type) {
        DioExceptionType.connectionTimeout ||
        DioExceptionType.sendTimeout ||
        DioExceptionType.receiveTimeout =>
          'The server took too long to respond. Please try again.',
        DioExceptionType.connectionError =>
          'Could not reach the server. Check your connection and try again.',
        _ => 'Something went wrong talking to the server.',
      },
    );
  }

  /// Decodes a non-2xx body: 400 → [ValidationDTO], everything else →
  /// [ApiErrorDTO]. Falls back to a generic message if the body can't be parsed.
  ApiException _decodeError(int code, List<int> bytes) {
    final data = Uint8List.fromList(bytes);
    if (code == 400) {
      try {
        final v = ValidationDTO.fromBuffer(data);
        return ApiException(
          statusCode: code,
          message: v.fields.isEmpty
              ? 'Please check the highlighted fields.'
              : v.fields.first.message,
          // `field` is a reserved name in the Dart generator → `field_1`.
          fieldErrors: {for (final f in v.fields) f.field_1: f.message},
        );
      } catch (_) {
        // fall through to ApiErrorDTO / generic
      }
    }
    try {
      final err = ApiErrorDTO.fromBuffer(data);
      if (err.message.isNotEmpty) {
        return ApiException(statusCode: code, message: err.message);
      }
    } catch (_) {
      // not an ApiErrorDTO body
    }
    return ApiException(
      statusCode: code,
      message: 'Request failed ($code). Please try again.',
    );
  }
}
