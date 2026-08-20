package com.example.oulearning.organization.domain.hierarchy;

import java.util.HashSet;
import java.util.Set;

public record Organization(Set<OuId> ouIds) {

    private static final String OU_ID_FIELD = "Ou id";

    public Organization {
        if (ouIds == null) {
            ouIds = Set.of();
        } else {
            ouIds.forEach(id -> HierarchyGuard.requireNonNull(id, OU_ID_FIELD));
            ouIds = Set.copyOf(ouIds);
        }
    }

    public Organization() {
        this(Set.of());
    }

    public Organization addOu(final OuId ouId) {
        HierarchyGuard.requireNonNull(ouId, OU_ID_FIELD);
        final var updated = new HashSet<>(ouIds);
        updated.add(ouId);
        return new Organization(updated);
    }

    public Organization removeOu(final OuId ouId) {
        HierarchyGuard.requireNonNull(ouId, OU_ID_FIELD);
        final var updated = new HashSet<>(ouIds);
        updated.remove(ouId);
        return new Organization(updated);
    }

    @Override
    public String toString() {
        return "Organization[ouIds=%s]".formatted(ouIds);
    }
}
