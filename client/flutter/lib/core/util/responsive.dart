import 'package:flutter/widgets.dart';

/// Layout breakpoints shared across the content screens, so the app reads as one
/// product whether it's on a phone, a tablet, or a stretched desktop / web
/// window. Below [medium] everything is a single column; the wider tiers add
/// columns and the screens cap their overall content width so nothing sprawls
/// edge-to-edge on large monitors.
abstract final class Breakpoints {
  /// At/above this width a second column of cards is worthwhile.
  static const double medium = 700;

  /// At/above this width a third column (or a two-pane dashboard) fits.
  static const double expanded = 1100;

  /// Number of card columns appropriate for a content area of [width],
  /// clamped to [max] (some content reads better with fewer columns).
  static int columnsFor(double width, {int max = 3}) {
    final columns = width >= expanded
        ? 3
        : width >= medium
            ? 2
            : 1;
    return columns > max ? max : columns;
  }

  /// True when [width] is roomy enough for a multi-pane layout.
  static bool isExpanded(double width) => width >= expanded;
}

/// Lays children out in a width-responsive grid that still scrolls (so
/// pull-to-refresh keeps working) and centres + caps its content width on large
/// screens. Items in the same row share a fixed width computed from the column
/// count; their heights are natural (a [Wrap], not a fixed-aspect grid), which
/// suits cards whose height varies with content.
class AdaptiveCardGrid extends StatelessWidget {
  const AdaptiveCardGrid({
    super.key,
    required this.itemCount,
    required this.itemBuilder,
    this.maxContentWidth = 1180,
    this.maxColumns = 3,
    this.spacing = 16,
    this.padding = const EdgeInsets.fromLTRB(16, 16, 16, 28),
  });

  final int itemCount;
  final Widget Function(BuildContext context, int index) itemBuilder;

  /// Cap on the laid-out content width — keeps the grid from sprawling on
  /// ultrawide displays.
  final double maxContentWidth;
  final int maxColumns;
  final double spacing;
  final EdgeInsets padding;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: ConstrainedBox(
        constraints: BoxConstraints(
          maxWidth: maxContentWidth + padding.horizontal,
        ),
        child: LayoutBuilder(
          builder: (context, constraints) {
            final innerWidth = constraints.maxWidth - padding.horizontal;
            final columns = Breakpoints.columnsFor(innerWidth, max: maxColumns);
            final itemWidth =
                (innerWidth - spacing * (columns - 1)) / columns;
            return ListView(
              // AlwaysScrollable so pull-to-refresh works with a short list.
              physics: const AlwaysScrollableScrollPhysics(),
              padding: padding,
              children: [
                Wrap(
                  spacing: spacing,
                  runSpacing: spacing,
                  children: [
                    for (var i = 0; i < itemCount; i++)
                      SizedBox(
                        width: itemWidth,
                        child: itemBuilder(context, i),
                      ),
                  ],
                ),
              ],
            );
          },
        ),
      ),
    );
  }
}
