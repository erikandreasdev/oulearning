package com.example.oulearning.organization.domain.hierarchy;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Domain object representing an Organizational Unit (Ou).
 */
public final class Ou {

    private final Id id;
    private final Name name;
    private final Id parentId;
    private final Set<Id> childIds;
    private final Set<com.example.oulearning.organization.domain.employee.Id> owners;
    private final Set<com.example.oulearning.organization.domain.employee.Id> members;

    public Ou(
            Id id,
            Name name,
            Id parentId,
            Set<Id> childIds,
            Set<com.example.oulearning.organization.domain.employee.Id> owners,
            Set<com.example.oulearning.organization.domain.employee.Id> members) {
        this.id = Objects.requireNonNull(id, "Ou id cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.parentId = parentId;
        this.childIds = (childIds != null) ? Set.copyOf(childIds) : Set.of();
        this.owners = (owners != null) ? Set.copyOf(owners) : Set.of();
        this.members = (members != null) ? Set.copyOf(members) : Set.of();
    }

    public static Ou of(
            Id id,
            Name name,
            Id parentId,
            Set<Id> childIds,
            Set<com.example.oulearning.organization.domain.employee.Id> owners,
            Set<com.example.oulearning.organization.domain.employee.Id> members) {
        return new Ou(id, name, parentId, childIds, owners, members);
    }

    public static Ou of(Id id, Name name) {
        return new Ou(id, name, null, Set.of(), Set.of(), Set.of());
    }

    public Id id() {
        return id;
    }

    public Name name() {
        return name;
    }

    public Optional<Id> parentId() {
        return Optional.ofNullable(parentId);
    }

    public Set<Id> childIds() {
        return childIds;
    }

    public Set<com.example.oulearning.organization.domain.employee.Id> owners() {
        return owners;
    }

    public Set<com.example.oulearning.organization.domain.employee.Id> members() {
        return members;
    }

    @Override
    public boolean equals(Object o) {
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
        return "Ou[id=" + id + ", name=" + name + ", parentId=" + parentId + "]";
    }
}
