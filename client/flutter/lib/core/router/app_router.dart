import 'package:flutter/widgets.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../features/auth/domain/entities/auth_state.dart';
import '../../features/auth/presentation/auth_providers.dart';
import '../../features/auth/presentation/pages/login_page.dart';
import '../../features/auth/presentation/pages/splash_page.dart';
import '../../features/tables/presentation/pages/table_list_page.dart';
import 'app_routes.dart';

/// Provides the app's [GoRouter], with redirects driven by [AuthState].
final routerProvider = Provider<GoRouter>((ref) {
  final notifier = _RouterRefresh(ref);
  ref.onDispose(notifier.dispose);

  return GoRouter(
    initialLocation: AppRoutes.splash,
    refreshListenable: notifier,
    redirect: (context, state) => _redirect(ref, state),
    routes: [
      GoRoute(
        path: AppRoutes.splash,
        name: AppRoutes.splashName,
        builder: (_, _) => const SplashPage(),
      ),
      GoRoute(
        path: AppRoutes.login,
        name: AppRoutes.loginName,
        builder: (_, _) => const LoginPage(),
      ),
      GoRoute(
        path: AppRoutes.tables,
        name: AppRoutes.tablesName,
        builder: (_, _) => const TableListPage(),
      ),
    ],
  );
});

String? _redirect(Ref ref, GoRouterState state) {
  final auth = ref.read(authControllerProvider);
  final location = state.matchedLocation;
  final atSplash = location == AppRoutes.splash;
  final atLogin = location == AppRoutes.login;

  return switch (auth) {
    // Session restore still in flight — hold on the splash screen.
    AuthInitializing() => atSplash ? null : AppRoutes.splash,
    // Signed in — keep out of splash/login.
    Authenticated() => (atSplash || atLogin) ? AppRoutes.tables : null,
    // Signed out — force to login.
    Unauthenticated() => atLogin ? null : AppRoutes.login,
  };
}

/// Bridges Riverpod's [AuthState] changes to go_router's [Listenable]-based
/// refresh mechanism.
class _RouterRefresh extends ChangeNotifier {
  _RouterRefresh(Ref ref) {
    _sub = ref.listen(
      authControllerProvider,
      (_, _) => notifyListeners(),
      fireImmediately: false,
    );
  }

  late final ProviderSubscription _sub;

  @override
  void dispose() {
    _sub.close();
    super.dispose();
  }
}
