package com.example.oulearning.organization.domain.hierarchy;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Domain entity representing an Organization containing organizational unit identifiers.
 */
public final class Organization {

    private final Set<OuId> ouIds = new HashSet<>();

    public Organization() {}

    public Organization(Set<OuId> ouIds) {
        if (ouIds != null) {
            this.ouIds.addAll(ouIds);
        }
    }

    public void addOu(OuId ouId) {
        Objects.requireNonNull(ouId, "ouId cannot be null");
        this.ouIds.add(ouId);
    }

    public void removeOu(OuId ouId) {
        Objects.requireNonNull(ouId, "ouId cannot be null");
        this.ouIds.remove(ouId);
    }

    public Set<OuId> ouIds() {
        return Collections.unmodifiableSet(ouIds);
    }

    @Override
    public String toString() {
        return "Organization[ouIds=" + ouIds + "]";
    }
}
