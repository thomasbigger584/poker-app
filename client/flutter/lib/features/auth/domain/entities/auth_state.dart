import 'package:flutter/foundation.dart';

import '../../../../core/error/failure.dart';
import 'user_profile.dart';

/// Global authentication state that drives top-level routing.
@immutable
sealed class AuthState {
  const AuthState();
}

/// Session restore is still in flight — show the splash screen.
class AuthInitializing extends AuthState {
  const AuthInitializing();
}

/// A valid session exists.
class Authenticated extends AuthState {
  const Authenticated(this.user);
  final UserProfile user;
}

/// No session — show login. Carries the last error, if any, for feedback.
class Unauthenticated extends AuthState {
  const Unauthenticated([this.error]);
  final Failure? error;
}
