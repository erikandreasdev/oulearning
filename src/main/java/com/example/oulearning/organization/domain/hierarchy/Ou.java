package com.example.oulearning.organization.domain.hierarchy;

import com.example.oulearning.organization.domain.employee.EmployeeId;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Domain object representing an Organizational Unit (Ou).
 */
public final class Ou {

    private final OuId id;
    private final Name name;
    private final OuId parentId;
    private final Set<OuId> childIds;
    private final Set<EmployeeId> owners;
    private final Set<EmployeeId> members;

    public Ou(
            final OuId id,
            final Name name,
            final OuId parentId,
            final Set<OuId> childIds,
            final Set<EmployeeId> owners,
            final Set<EmployeeId> members) {
        this.id = HierarchyGuard.requireNonNull(id, "Ou id");
        this.name = HierarchyGuard.requireNonNull(name, "Name");
        this.parentId = parentId;
        this.childIds = (childIds != null) ? Set.copyOf(childIds) : Set.of();
        this.owners = (owners != null) ? Set.copyOf(owners) : Set.of();
        this.members = (members != null) ? Set.copyOf(members) : Set.of();
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

    public OuId id() {
        return id;
    }

    public Name name() {
        return name;
    }

    public Optional<OuId> parentId() {
        return Optional.ofNullable(parentId);
    }

    public Set<OuId> childIds() {
        return childIds;
    }

    public Set<EmployeeId> owners() {
        return owners;
    }

    public Set<EmployeeId> members() {
        return members;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Ou ou)) return false;
        return Objects.equals(id, ou.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Ou[id=%s, name=%s, parentId=%s]".formatted(id, name, parentId);
    }
}
