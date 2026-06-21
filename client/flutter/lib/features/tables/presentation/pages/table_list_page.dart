import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/network/api_exception.dart';
import '../../../../core/proto/gen/poker/enums.pb.dart';
import '../../../../core/proto/gen/poker/rest.pb.dart';
import '../../../../core/router/app_routes.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/widgets/felt_background.dart';
import '../../../game/presentation/game_launch_args.dart';
import '../tables_providers.dart';
import '../widgets/app_drawer.dart';
import '../widgets/table_list_item.dart';

/// Lobby of available tables — the post-login home. Ports the Android
/// `TableListActivity`: live list, pull-to-refresh, create, connect/reconnect,
/// and the navigation drawer.
class TableListPage extends ConsumerWidget {
  const TableListPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final tablesAsync = ref.watch(tableListProvider);

    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: AppBar(
        title: const Text('Tables'),
        actions: [
          IconButton(
            tooltip: 'Create table',
            icon: const Icon(Icons.add_circle_outline_rounded),
            onPressed: () => context.pushNamed(AppRoutes.createTableName),
          ),
        ],
      ),
      drawer: const AppDrawer(),
      body: FeltBackground(
        child: SafeArea(
          child: RefreshIndicator(
            onRefresh: () => ref.read(tableListProvider.notifier).refresh(),
            child: tablesAsync.when(
              skipLoadingOnRefresh: true,
              data: (tables) => _TableList(
                tables: tables,
                onConnect: (entry) => _connect(context, entry),
                onReconnect: (entry) => _reconnect(context, entry),
              ),
              error: (error, _) => _ErrorState(
                message: error is ApiException
                    ? error.message
                    : 'Could not load tables.',
                onRetry: () => ref.read(tableListProvider.notifier).refresh(),
              ),
              loading: () =>
                  const Center(child: CircularProgressIndicator()),
            ),
          ),
        ),
      ),
    );
  }

  void _connect(BuildContext context, AvailableTableDTO entry) {
    context.pushNamed(AppRoutes.connectTableName, extra: entry);
  }

  void _reconnect(BuildContext context, AvailableTableDTO entry) {
    final connectionType = entry.hasCurrentUserConnectionType()
        ? entry.currentUserConnectionType
        : ConnectionType.CONNECTION_TYPE_PLAYER;
    context.pushNamed(
      AppRoutes.gameName,
      extra: GameLaunchArgs(
        table: entry.table,
        connectionType: connectionType,
        buyInAmount: '0',
        reconnect: true,
      ),
    );
  }
}

class _TableList extends StatelessWidget {
  const _TableList({
    required this.tables,
    required this.onConnect,
    required this.onReconnect,
  });

  final List<AvailableTableDTO> tables;
  final void Function(AvailableTableDTO entry) onConnect;
  final void Function(AvailableTableDTO entry) onReconnect;

  @override
  Widget build(BuildContext context) {
    if (tables.isEmpty) return const _EmptyState();

    return ListView.separated(
      // AlwaysScrollable so pull-to-refresh works even with a short list.
      physics: const AlwaysScrollableScrollPhysics(),
      padding: const EdgeInsets.fromLTRB(12, 8, 12, 24),
      itemCount: tables.length,
      separatorBuilder: (_, _) => const SizedBox(height: 10),
      itemBuilder: (context, index) {
        final entry = tables[index];
        return TableListItem(
          entry: entry,
          onConnect: () => onConnect(entry),
          onReconnect: () => onReconnect(entry),
        );
      },
    );
  }
}

class _EmptyState extends StatelessWidget {
  const _EmptyState();

  @override
  Widget build(BuildContext context) {
    return ListView(
      physics: const AlwaysScrollableScrollPhysics(),
      children: [
        SizedBox(height: MediaQuery.of(context).size.height * 0.18),
        const Icon(Icons.table_bar_rounded, size: 64, color: AppColors.gold),
        const SizedBox(height: 16),
        Text(
          'No tables yet',
          textAlign: TextAlign.center,
          style: Theme.of(context).textTheme.titleLarge?.copyWith(
                color: AppColors.textPrimary,
                fontWeight: FontWeight.w800,
              ),
        ),
        const SizedBox(height: 8),
        const Padding(
          padding: EdgeInsets.symmetric(horizontal: 40),
          child: Text(
            'Create the first table with the + button above, '
            'or pull down to refresh.',
            textAlign: TextAlign.center,
            style: TextStyle(color: AppColors.textSecondary, height: 1.5),
          ),
        ),
      ],
    );
  }
}

class _ErrorState extends StatelessWidget {
  const _ErrorState({required this.message, required this.onRetry});

  final String message;
  final VoidCallback onRetry;

  @override
  Widget build(BuildContext context) {
    return ListView(
      physics: const AlwaysScrollableScrollPhysics(),
      children: [
        SizedBox(height: MediaQuery.of(context).size.height * 0.18),
        const Icon(Icons.cloud_off_rounded, size: 64, color: AppColors.error),
        const SizedBox(height: 16),
        Text(
          message,
          textAlign: TextAlign.center,
          style: const TextStyle(color: AppColors.textPrimary, fontSize: 16),
        ),
        const SizedBox(height: 20),
        Center(
          child: OutlinedButton.icon(
            onPressed: onRetry,
            icon: const Icon(Icons.refresh_rounded),
            label: const Text('Try again'),
          ),
        ),
      ],
    );
  }
}
