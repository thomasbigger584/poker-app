import 'package:flutter/foundation.dart';

/// Domain-level error type. Presentation maps these to user-facing copy.
@immutable
sealed class Failure {
  const Failure(this.message, [this.cause]);

  /// Human-readable, already-localised-enough message.
  final String message;

  /// Underlying exception/error, kept for logging — never shown to users.
  final Object? cause;

  @override
  String toString() => '$runtimeType($message)';
}

/// No connectivity / host unreachable / timeout.
class NetworkFailure extends Failure {
  const NetworkFailure([super.message = 'Network error', super.cause]);
}

/// OAuth / token / session problems.
class AuthFailure extends Failure {
  const AuthFailure([super.message = 'Authentication failed', super.cause]);
}

/// User intentionally aborted (e.g. closed the login browser tab).
class CancelledFailure extends Failure {
  const CancelledFailure([super.message = 'Cancelled', super.cause]);
}

/// Backend returned an error response.
class ServerFailure extends Failure {
  const ServerFailure([super.message = 'Server error', super.cause]);
}

/// Anything we did not anticipate.
class UnexpectedFailure extends Failure {
  const UnexpectedFailure([super.message = 'Something went wrong', super.cause]);
}
