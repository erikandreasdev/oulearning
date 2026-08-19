package com.example.oulearning.training.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * Domain object representing a training type category.
 */
public final class Type {

    private final TypeId id;
    private final TypeName name;
    private final TypeId parentTypeId;

    public Type(final TypeId id, final TypeName name, final TypeId parentTypeId) {
        this.id = TrainingGuard.requireNonNull(id, "TypeId");
        this.name = TrainingGuard.requireNonNull(name, "Name");
        this.parentTypeId = parentTypeId;
    }

    public static Type of(final TypeId id, final TypeName name, final TypeId parentTypeId) {
        return new Type(id, name, parentTypeId);
    }

    public static Type of(final TypeId id, final TypeName name) {
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
    public boolean equals(final Object o) {
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
        return "Type[id=%s, name=%s, parentTypeId=%s]".formatted(id, name, parentTypeId);
    }
}
