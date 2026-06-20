package com.twb.pokerapp.domain.converter;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ProtocolMessageEnum;
import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;

import java.util.function.IntFunction;

/**
 * Persists a generated protobuf enum to the database using the same short value that the original
 * hand-written domain enum stored, e.g. {@code ACTION_TYPE_CHECK} <-> {@code "CHECK"}. This keeps the
 * existing {@code text} columns (and their baked-in defaults such as {@code WAITING_FOR_PLAYERS})
 * valid with no Liquibase migration, while letting the proto enum be the single source of truth.
 *
 * <p>The proto value names are generated as {@code SCREAMING(EnumName) + "_" + legacyName} (verified
 * across every enum), so the legacy short name is recovered by stripping that prefix.
 *
 * <p>The two non-domain proto values map to {@code null}: {@code *_UNSPECIFIED} (number 0, the proto3
 * default) and {@code UNRECOGNIZED} (an unknown wire value, whose {@code getNumber()} /
 * {@code getValueDescriptor()} throw). A genuine {@code null} attribute passes through silently, but
 * mapping one of those two to {@code null} is logged at WARN — reaching the persistence layer with a
 * non-domain enum almost always signals a mapping bug upstream.
 */
@Slf4j
public abstract class ProtoEnumStringConverter<E extends Enum<E> & ProtocolMessageEnum>
        implements AttributeConverter<E, String> {

    private final Descriptors.EnumDescriptor descriptor;
    private final IntFunction<E> byNumber;
    private final String prefix;

    protected ProtoEnumStringConverter(Descriptors.EnumDescriptor descriptor, IntFunction<E> byNumber) {
        this.descriptor = descriptor;
        this.byNumber = byNumber;
        this.prefix = screamingSnake(descriptor.getName()) + "_";
    }

    @Override
    public String convertToDatabaseColumn(E value) {
        if (value == null) {
            return null;
        }
        // UNRECOGNIZED has no value descriptor and its getNumber() throws, so detect it by name first
        // (short-circuiting before getNumber()); *_UNSPECIFIED is number 0.
        if ("UNRECOGNIZED".equals(value.name()) || value.getNumber() == 0) {
            log.warn("Mapping {} value '{}' to null for persistence (non-domain enum value)",
                    descriptor.getName(), value.name());
            return null;
        }
        return value.getValueDescriptor().getName().substring(prefix.length());
    }

    @Override
    public E convertToEntityAttribute(String dbValue) {
        if (dbValue == null || dbValue.isEmpty()) {
            return null;
        }
        var valueDescriptor = descriptor.findValueByName(prefix + dbValue);
        if (valueDescriptor == null) {
            throw new IllegalArgumentException(
                    "Unknown " + descriptor.getName() + " database value: " + dbValue);
        }
        return byNumber.apply(valueDescriptor.getNumber());
    }

    private static String screamingSnake(String camelCase) {
        var builder = new StringBuilder();
        for (var index = 0; index < camelCase.length(); index++) {
            var character = camelCase.charAt(index);
            if (index > 0 && Character.isUpperCase(character)) {
                builder.append('_');
            }
            builder.append(Character.toUpperCase(character));
        }
        return builder.toString();
    }
}
