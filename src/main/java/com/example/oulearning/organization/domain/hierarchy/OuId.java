package com.example.oulearning.organization.domain.hierarchy;

import java.util.UUID;

/**
 * Value object representing an organizational unit identifier.
 *
 * @param value the UUID value
 */
public record OuId(UUID value) {

    public OuId {
        HierarchyGuard.requireNonNull(value, "Ou id");
    }

    public static OuId of(final UUID value) {
        return new OuId(value);
    }

    public static OuId fromString(final String value) {
        return new OuId(HierarchyGuard.requireValidUuid(value, "Ou id"));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
