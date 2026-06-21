/// A failure returned by the REST API, decoded from the server's protobuf error
/// bodies (`ApiErrorDTO` for 404/500, `ValidationDTO` for 400 — see the backend
/// `GlobalExceptionHandler`).
class ApiException implements Exception {
  ApiException({
    required this.statusCode,
    required this.message,
    this.fieldErrors = const {},
  });

  /// HTTP status code (0 when the request never reached the server).
  final int statusCode;

  /// Human-readable message suitable for surfacing to the user.
  final String message;

  /// Field → message map for 400 validation failures (empty otherwise).
  final Map<String, String> fieldErrors;

  /// True when this came from a bean-validation failure (HTTP 400).
  bool get isValidation => fieldErrors.isNotEmpty;

  /// A transport-level failure with no HTTP response (offline, DNS, timeout).
  bool get isNetwork => statusCode == 0;

  @override
  String toString() => 'ApiException($statusCode): $message';
}
