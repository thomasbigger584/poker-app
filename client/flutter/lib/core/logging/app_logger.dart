import 'package:flutter/foundation.dart';
import 'package:logger/logger.dart';

/// App-wide logger. Verbose in debug, warnings+ in release.
final Logger log = Logger(
  level: kDebugMode ? Level.debug : Level.warning,
  printer: PrettyPrinter(
    methodCount: 0,
    errorMethodCount: 6,
    lineLength: 100,
    colors: false,
    printEmojis: true,
    dateTimeFormat: DateTimeFormat.onlyTimeAndSinceStart,
  ),
);
