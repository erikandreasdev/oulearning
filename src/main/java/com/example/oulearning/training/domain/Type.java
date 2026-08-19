package com.example.oulearning.training.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * Domain entity representing a training category/type within a training type hierarchy.
 */
public final class Type {

    private final TypeId id;
    private TypeName name;
    private TypeId parentTypeId;

    private Type(TypeId id, TypeName name, TypeId parentTypeId) {
        this.id = Objects.requireNonNull(id, "TypeId cannot be null");
        this.name = Objects.requireNonNull(name, "TypeName cannot be null");
        if (parentTypeId != null && parentTypeId.equals(id)) {
            throw new InvalidTrainingOperationException("A training type cannot be its own parent");
        }
        this.parentTypeId = parentTypeId;
    }

    /**
     * Factory method to create a new {@link Type} with an optional parent.
     */
    public static Type create(TypeId id, TypeName name, TypeId parentTypeId) {
        return new Type(id, name, parentTypeId);
    }

    /**
     * Factory method to create a top-level root {@link Type}.
     */
    public static Type createRoot(TypeId id, TypeName name) {
        return new Type(id, name, null);
    }

    /**
     * Reconstitutes an existing {@link Type} from persistence.
     */
    public static Type reconstitute(TypeId id, TypeName name, TypeId parentTypeId) {
        return new Type(id, name, parentTypeId);
    }

    public void changeName(TypeName newName) {
        this.name = Objects.requireNonNull(newName, "TypeName cannot be null");
    }

    public void changeParent(TypeId newParentTypeId) {
        if (newParentTypeId != null && newParentTypeId.equals(this.id)) {
            throw new InvalidTrainingOperationException("Cannot set training type parent to itself");
        }
        this.parentTypeId = newParentTypeId;
    }

    public void makeRoot() {
        this.parentTypeId = null;
    }

    public TypeId id() {
        return id;
    }

    public TypeName name() {
        return name;
    }

    public Optional<TypeId> parentTypeId() {
        return Optional.ofNullable(parentTypeId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Type type)) return false;
        return Objects.equals(id, type.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Type[id=" + id + ", name=" + name + ", parentTypeId=" + parentTypeId + "]";
    }
}
