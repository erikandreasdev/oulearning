package com.example.oulearning.organization.domain.hierarchy;

import com.example.oulearning.organization.domain.employee.EmployeeId;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public record Ou(
        OuId id,
        Name name,
        OuId rawParentId,
        Set<OuId> childIds,
        Set<EmployeeId> owners,
        Set<EmployeeId> members) {

    public Ou {
        id = HierarchyGuard.requireNonNull(id, "Ou id");
        name = HierarchyGuard.requireNonNull(name, "Name");
        childIds = (childIds != null) ? Set.copyOf(childIds) : Set.of();
        owners = (owners != null) ? Set.copyOf(owners) : Set.of();
        members = (members != null) ? Set.copyOf(members) : Set.of();
    }

    public static Ou of(
            final OuId id,
            final Name name,
            final OuId parentId,
            final Set<OuId> childIds,
            final Set<EmployeeId> owners,
            final Set<EmployeeId> members) {
        return new Ou(id, name, parentId, childIds, owners, members);
    }

    public static Ou of(final OuId id, final Name name) {
        return new Ou(id, name, null, Set.of(), Set.of(), Set.of());
    }

    public Optional<OuId> parentId() {
        return Optional.ofNullable(rawParentId);
    }

    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof final Ou ou && Objects.equals(id, ou.id));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Ou[id=%s, name=%s, parentId=%s]".formatted(id, name, rawParentId);
    }
}
