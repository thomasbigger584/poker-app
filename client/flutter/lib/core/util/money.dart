/// Helpers for the BigDecimal-as-string money values used across the proto
/// contract (`total_funds`, `min_buyin`, amounts, …). Kept dependency-free
/// (no `intl`) — a simple, locale-agnostic chip/grouped formatter.
abstract final class Money {
  /// Parses a backend decimal string (e.g. "50000.00") to a double, or 0.
  static double parse(String? raw) {
    if (raw == null || raw.isEmpty) return 0;
    return double.tryParse(raw) ?? 0;
  }

  /// Groups the integer part with commas, dropping any fractional part:
  /// "50000.00" → "50,000". Used for chips/funds where cents add noise.
  static String compact(String? raw) => _grouped(parse(raw).round());

  /// Like [compact] but with a leading sign, for transaction deltas:
  /// (+) deposits/cashouts vs (−) withdrawals/buy-ins.
  static String signed(String? raw, {required bool positive}) {
    final value = parse(raw).abs().round();
    return '${positive ? '+' : '−'}${_grouped(value)}';
  }

  /// Two-decimal display for forms/summaries: "100" → "100.00".
  static String exact(String? raw) => parse(raw).toStringAsFixed(2);

  static String _grouped(int value) {
    final digits = value.abs().toString();
    final buffer = StringBuffer();
    for (var i = 0; i < digits.length; i++) {
      if (i > 0 && (digits.length - i) % 3 == 0) buffer.write(',');
      buffer.write(digits[i]);
    }
    return '${value < 0 ? '-' : ''}$buffer';
  }
}
