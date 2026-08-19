package com.example.oulearning.organization.domain.hierarchy;

import com.example.oulearning.organization.domain.hierarchy.event.MemberAdded;
import com.example.oulearning.organization.domain.hierarchy.event.MemberRemoved;
import com.example.oulearning.organization.domain.hierarchy.event.OuCreated;
import com.example.oulearning.organization.domain.hierarchy.event.OuMoved;
import com.example.oulearning.organization.domain.hierarchy.event.OuNameChanged;
import com.example.oulearning.organization.domain.hierarchy.event.OwnerAdded;
import com.example.oulearning.organization.domain.hierarchy.event.OwnerRemoved;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Aggregate root representing an Organizational Unit (Ou) in the organizational hierarchy.
 */
public final class Ou {

    private final Id id;
    private Name name;
    private Id parentId;
    private final Set<Id> childIds = new HashSet<>();
    private final Set<com.example.oulearning.organization.domain.employee.Id> owners = new HashSet<>();
    private final Set<com.example.oulearning.organization.domain.employee.Id> members = new HashSet<>();
    private final List<Object> domainEvents = new ArrayList<>();

    private Ou(
            Id id,
            Name name,
            Id parentId,
            Set<Id> childIds,
            Set<com.example.oulearning.organization.domain.employee.Id> owners,
            Set<com.example.oulearning.organization.domain.employee.Id> members) {
        this.id = Objects.requireNonNull(id, "Ou id cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.parentId = parentId;
        if (parentId != null && parentId.equals(id)) {
            throw new CyclicHierarchyException("An organizational unit cannot be its own parent");
        }
        if (childIds != null) {
            for (Id childId : childIds) {
                if (childId.equals(id)) {
                    throw new CyclicHierarchyException("An organizational unit cannot be its own child");
                }
                this.childIds.add(childId);
            }
        }
        if (owners != null) {
            this.owners.addAll(owners);
        }
        if (members != null) {
            this.members.addAll(members);
        }
    }

    /**
     * Factory method to create a new {@link Ou} with an optional parent.
     */
    public static Ou create(Id id, Name name, Id parentId, Instant createdAt) {
        Ou ou = new Ou(id, name, parentId, Set.of(), Set.of(), Set.of());
        ou.registerEvent(new OuCreated(id, name, parentId, Objects.requireNonNull(createdAt, "createdAt cannot be null")));
        return ou;
    }

    /**
     * Factory method to create a top-level root {@link Ou}.
     */
    public static Ou createRoot(Id id, Name name, Instant createdAt) {
        return create(id, name, null, createdAt);
    }

    /**
     * Reconstitutes an existing {@link Ou} aggregate from persistence.
     */
    public static Ou reconstitute(
            Id id,
            Name name,
            Id parentId,
            Set<Id> childIds,
            Set<com.example.oulearning.organization.domain.employee.Id> owners,
            Set<com.example.oulearning.organization.domain.employee.Id> members) {
        return new Ou(id, name, parentId, childIds, owners, members);
    }

    public void changeName(Name newName, Instant occurredAt) {
        Objects.requireNonNull(newName, "newName cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        if (!this.name.equals(newName)) {
            Name oldName = this.name;
            this.name = newName;
            registerEvent(new OuNameChanged(this.id, oldName, newName, occurredAt));
        }
    }

    public void moveToParent(Id newParentId, Instant occurredAt) {
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        if (newParentId != null && newParentId.equals(this.id)) {
            throw new CyclicHierarchyException("Cannot move organizational unit under itself");
        }
        if (!Objects.equals(this.parentId, newParentId)) {
            Id oldParentId = this.parentId;
            this.parentId = newParentId;
            registerEvent(new OuMoved(this.id, oldParentId, newParentId, occurredAt));
        }
    }

    public void addChild(Id childId) {
        Objects.requireNonNull(childId, "childId cannot be null");
        if (childId.equals(this.id)) {
            throw new CyclicHierarchyException("Cannot add organizational unit as a child of itself");
        }
        this.childIds.add(childId);
    }

    public void removeChild(Id childId) {
        Objects.requireNonNull(childId, "childId cannot be null");
        this.childIds.remove(childId);
    }

    public void addOwner(com.example.oulearning.organization.domain.employee.Id ownerId, Instant occurredAt) {
        Objects.requireNonNull(ownerId, "ownerId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        if (this.owners.add(ownerId)) {
            registerEvent(new OwnerAdded(this.id, ownerId, occurredAt));
        }
    }

    public void removeOwner(com.example.oulearning.organization.domain.employee.Id ownerId, Instant occurredAt) {
        Objects.requireNonNull(ownerId, "ownerId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        if (this.owners.remove(ownerId)) {
            registerEvent(new OwnerRemoved(this.id, ownerId, occurredAt));
        }
    }

    public void addMember(com.example.oulearning.organization.domain.employee.Id memberId, Instant occurredAt) {
        Objects.requireNonNull(memberId, "memberId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        if (this.members.add(memberId)) {
            registerEvent(new MemberAdded(this.id, memberId, occurredAt));
        }
    }

    public void removeMember(com.example.oulearning.organization.domain.employee.Id memberId, Instant occurredAt) {
        Objects.requireNonNull(memberId, "memberId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        if (this.members.remove(memberId)) {
            registerEvent(new MemberRemoved(this.id, memberId, occurredAt));
        }
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
        return Collections.unmodifiableSet(childIds);
    }

    public Set<com.example.oulearning.organization.domain.employee.Id> owners() {
        return Collections.unmodifiableSet(owners);
    }

    public Set<com.example.oulearning.organization.domain.employee.Id> members() {
        return Collections.unmodifiableSet(members);
    }

    private void registerEvent(Object event) {
        this.domainEvents.add(event);
    }

    public List<Object> pullDomainEvents() {
        List<Object> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return Collections.unmodifiableList(events);
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
