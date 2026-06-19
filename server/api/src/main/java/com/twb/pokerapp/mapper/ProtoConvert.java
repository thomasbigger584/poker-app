package com.twb.pokerapp.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Central conversion between Java scalar types and their protobuf wire encodings. The poker domain
 * enums are now the generated protobuf enums themselves (single source of truth), so no enum
 * conversion is needed here any more — only scalar encodings remain.
 *
 * <p>Scalar conventions on the wire: UUID/BigDecimal/char/LocalDateTime are all encoded as strings;
 * an absent value is the empty string (proto3 string default).
 */
public final class ProtoConvert {

    private ProtoConvert() {
    }

    public static String uuidStr(UUID value) {
        return value == null ? "" : value.toString();
    }

    public static UUID uuid(String value) {
        return (value == null || value.isEmpty()) ? null : UUID.fromString(value);
    }

    public static String money(BigDecimal value) {
        return value == null ? "" : value.toPlainString();
    }

    public static BigDecimal bigDecimal(String value) {
        return (value == null || value.isEmpty()) ? null : new BigDecimal(value);
    }

    public static String ch(char value) {
        return String.valueOf(value);
    }

    /** Null-safe text: protobuf string setters reject null, so coalesce to the proto3 default "". */
    public static String text(String value) {
        return value == null ? "" : value;
    }

    public static String dateTime(LocalDateTime value) {
        return value == null ? "" : value.toString();
    }

    public static LocalDateTime dateTime(String value) {
        return (value == null || value.isEmpty()) ? null : LocalDateTime.parse(value);
    }
}
