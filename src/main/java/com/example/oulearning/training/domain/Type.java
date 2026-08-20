package com.example.oulearning.training.domain;

import java.util.Objects;
import java.util.Optional;

public record Type(TypeId id, TypeName name, TypeId rawParentTypeId) {

    public Type {
        id = TrainingGuard.requireTypeId(id);
        name = TrainingGuard.requireTypeName(name);
    }

    public static Type of(final TypeId id, final TypeName name, final TypeId parentTypeId) {
        return new Type(id, name, parentTypeId);
    }

    public static Type of(final TypeId id, final TypeName name) {
        return new Type(id, name, null);
    }

    public Optional<TypeId> parentTypeId() {
        return Optional.ofNullable(rawParentTypeId);
    }

    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof final Type type && Objects.equals(id, type.id));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Type[id=%s, name=%s, parentTypeId=%s]".formatted(id, name, rawParentTypeId);
    }
}
