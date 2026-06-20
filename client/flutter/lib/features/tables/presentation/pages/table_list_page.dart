import 'package:flutter/material.dart';

import '../../../../core/theme/app_colors.dart';
import '../../../../core/widgets/felt_background.dart';
import '../widgets/app_drawer.dart';

/// Lobby / table list. The live list, create-table and connect flows are
/// intentionally stubbed for now — this proves the post-login destination and
/// the navigation drawer.
class TableListPage extends StatelessWidget {
  const TableListPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: AppBar(
        title: const Text('Tables'),
        actions: [
          IconButton(
            tooltip: 'Create table',
            icon: const Icon(Icons.add_circle_outline_rounded),
            onPressed: () {
              ScaffoldMessenger.of(context)
                ..clearSnackBars()
                ..showSnackBar(
                  const SnackBar(content: Text('Create table — coming soon')),
                );
            },
          ),
        ],
      ),
      drawer: const AppDrawer(),
      body: FeltBackground(
        child: SafeArea(
          child: Center(
            child: Padding(
              padding: const EdgeInsets.all(32),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Container(
                    width: 96,
                    height: 96,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      color: Colors.black.withValues(alpha: 0.2),
                      border: Border.all(
                        color: AppColors.feltLight.withValues(alpha: 0.5),
                      ),
                    ),
                    child: const Icon(Icons.table_bar_rounded,
                        size: 44, color: AppColors.gold),
                  ),
                  const SizedBox(height: 24),
                  Text(
                    'You’re signed in',
                    style: Theme.of(context).textTheme.titleLarge?.copyWith(
                          color: AppColors.textPrimary,
                          fontWeight: FontWeight.w800,
                        ),
                  ),
                  const SizedBox(height: 10),
                  const Text(
                    'The lobby of available tables will appear here.\n'
                    'Open the menu to explore your profile.',
                    textAlign: TextAlign.center,
                    style: TextStyle(color: AppColors.textSecondary, height: 1.5),
                  ),
                  const SizedBox(height: 28),
                  Builder(
                    builder: (context) => OutlinedButton.icon(
                      onPressed: () => Scaffold.of(context).openDrawer(),
                      icon: const Icon(Icons.menu_rounded),
                      label: const Text('Open menu'),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
