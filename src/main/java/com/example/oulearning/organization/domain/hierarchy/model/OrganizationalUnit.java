package com.example.oulearning.organization.domain.hierarchy.model;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public record OrganizationalUnit(
        OrganizationalUnitId id,
        Name name,
        OrganizationalUnitId rawParentId,
        Set<OrganizationalUnitId> childIds,
        Set<EmployeeId> owners,
        Set<EmployeeId> members,
        boolean active) {

    public OrganizationalUnit {
        HierarchyGuard.requireOrganizationalUnitId(id);
        HierarchyGuard.requireName(name);
        childIds = (childIds != null) ? Set.copyOf(childIds) : Set.of();
        owners = (owners != null) ? Set.copyOf(owners) : Set.of();
        members = (members != null) ? Set.copyOf(members) : Set.of();
    }

    public static OrganizationalUnit create(
            final OrganizationalUnitId id,
            final Name name,
            final OrganizationalUnitId parentId) {
        return new OrganizationalUnit(id, name, parentId, Set.of(), Set.of(), Set.of(), true);
    }

    public static OrganizationalUnit reconstitute(
            final OrganizationalUnitId id,
            final Name name,
            final OrganizationalUnitId parentId,
            final Set<OrganizationalUnitId> childIds,
            final Set<EmployeeId> owners,
            final Set<EmployeeId> members,
            final boolean active) {
        return new OrganizationalUnit(id, name, parentId, childIds, owners, members, active);
    }

    public static OrganizationalUnit of(
            final OrganizationalUnitId id,
            final Name name,
            final OrganizationalUnitId parentId,
            final Set<OrganizationalUnitId> childIds,
            final Set<EmployeeId> owners,
            final Set<EmployeeId> members) {
        return new OrganizationalUnit(id, name, parentId, childIds, owners, members, true);
    }

    public static OrganizationalUnit of(final OrganizationalUnitId id, final Name name) {
        return new OrganizationalUnit(id, name, null, Set.of(), Set.of(), Set.of(), true);
    }

    public OrganizationalUnit rename(final Name newName) {
        HierarchyGuard.requireName(newName);
        return new OrganizationalUnit(id, newName, rawParentId, childIds, owners, members, active);
    }

    public OrganizationalUnit addOwner(final EmployeeId ownerId) {
        HierarchyGuard.requireEmployeeId(ownerId);
        final var updated = new HashSet<>(owners);
        updated.add(ownerId);
        return new OrganizationalUnit(id, name, rawParentId, childIds, updated, members, active);
    }

    public OrganizationalUnit removeOwner(final EmployeeId ownerId) {
        HierarchyGuard.requireEmployeeId(ownerId);
        final var updated = new HashSet<>(owners);
        updated.remove(ownerId);
        return new OrganizationalUnit(id, name, rawParentId, childIds, updated, members, active);
    }

    public OrganizationalUnit addOwners(final Set<EmployeeId> ownerIds) {
        HierarchyGuard.requireEmployeeIds(ownerIds);
        final var updated = new HashSet<>(owners);
        updated.addAll(ownerIds);
        return new OrganizationalUnit(id, name, rawParentId, childIds, updated, members, active);
    }

    public OrganizationalUnit removeOwners(final Set<EmployeeId> ownerIds) {
        HierarchyGuard.requireEmployeeIds(ownerIds);
        final var updated = new HashSet<>(owners);
        updated.removeAll(ownerIds);
        return new OrganizationalUnit(id, name, rawParentId, childIds, updated, members, active);
    }

    public OrganizationalUnit addMember(final EmployeeId memberId) {
        HierarchyGuard.requireEmployeeId(memberId);
        final var updated = new HashSet<>(members);
        updated.add(memberId);
        return new OrganizationalUnit(id, name, rawParentId, childIds, owners, updated, active);
    }

    public OrganizationalUnit removeMember(final EmployeeId memberId) {
        HierarchyGuard.requireEmployeeId(memberId);
        final var updated = new HashSet<>(members);
        updated.remove(memberId);
        return new OrganizationalUnit(id, name, rawParentId, childIds, owners, updated, active);
    }

    public OrganizationalUnit addMembers(final Set<EmployeeId> memberIds) {
        HierarchyGuard.requireEmployeeIds(memberIds);
        final var updated = new HashSet<>(members);
        updated.addAll(memberIds);
        return new OrganizationalUnit(id, name, rawParentId, childIds, owners, updated, active);
    }

    public OrganizationalUnit removeMembers(final Set<EmployeeId> memberIds) {
        HierarchyGuard.requireEmployeeIds(memberIds);
        final var updated = new HashSet<>(members);
        updated.removeAll(memberIds);
        return new OrganizationalUnit(id, name, rawParentId, childIds, owners, updated, active);
    }

    public OrganizationalUnit deactivate() {
        return new OrganizationalUnit(id, name, rawParentId, childIds, owners, members, false);
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
}
