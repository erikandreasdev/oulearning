package com.example.oulearning.organization.domain.hierarchy;

import com.example.oulearning.organization.domain.employee.EmployeeId;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public record OrganizationalUnit(
        OrganizationalUnitId id,
        Name name,
        OrganizationalUnitId rawParentId,
        Set<OrganizationalUnitId> childIds,
        Set<EmployeeId> owners,
        Set<EmployeeId> members) {

    public OrganizationalUnit {
        id = HierarchyGuard.requireOrganizationalUnitId(id);
        name = HierarchyGuard.requireName(name);
        childIds = (childIds != null) ? Set.copyOf(childIds) : Set.of();
        owners = (owners != null) ? Set.copyOf(owners) : Set.of();
        members = (members != null) ? Set.copyOf(members) : Set.of();
    }

    public static OrganizationalUnit of(
            final OrganizationalUnitId id,
            final Name name,
            final OrganizationalUnitId parentId,
            final Set<OrganizationalUnitId> childIds,
            final Set<EmployeeId> owners,
            final Set<EmployeeId> members) {
        return new OrganizationalUnit(id, name, parentId, childIds, owners, members);
    }

    public static OrganizationalUnit of(final OrganizationalUnitId id, final Name name) {
        return new OrganizationalUnit(id, name, null, Set.of(), Set.of(), Set.of());
    }

    public Optional<OrganizationalUnitId> parentId() {
        return Optional.ofNullable(rawParentId);
    }

    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof final OrganizationalUnit organizationalUnit
                && Objects.equals(id, organizationalUnit.id));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OrganizationalUnit[id=%s, name=%s, parentId=%s]".formatted(id, name, rawParentId);
    }
}
