package com.example.oulearning.training.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * Domain object representing a training Type.
 */
public final class Type {

    private final TypeId id;
    private final TypeName name;
    private final TypeId parentTypeId;

    public Type(TypeId id, TypeName name, TypeId parentTypeId) {
        this.id = Objects.requireNonNull(id, "TypeId cannot be null");
        this.name = Objects.requireNonNull(name, "TypeName cannot be null");
        this.parentTypeId = parentTypeId;
    }

    public static Type of(TypeId id, TypeName name, TypeId parentTypeId) {
        return new Type(id, name, parentTypeId);
    }

    public static Type of(TypeId id, TypeName name) {
        return new Type(id, name, null);
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
