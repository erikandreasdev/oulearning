package com.example.oulearning.organization.domain.hierarchy;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public final class Organization {

    private final Set<OuId> ouIds = new HashSet<>();

    public Organization() {}

    public Organization(final Set<OuId> ouIds) {
        if (ouIds != null) {
            ouIds.forEach(id -> HierarchyGuard.requireNonNull(id, "Ou id"));
            this.ouIds.addAll(ouIds);
        }
    }

    public void addOu(final OuId ouId) {
        HierarchyGuard.requireNonNull(ouId, "Ou id");
        this.ouIds.add(ouId);
    }

    public void removeOu(final OuId ouId) {
        HierarchyGuard.requireNonNull(ouId, "Ou id");
        this.ouIds.remove(ouId);
    }

    public Set<OuId> ouIds() {
        return Collections.unmodifiableSet(ouIds);
    }

    @Override
    public String toString() {
        return "Organization[ouIds=%s]".formatted(ouIds);
    }
}
