package com.example.oulearning.training.domain.model;


public record TypeName(String value) {

    public TypeName {
        value = TrainingGuard.requireValidTypeName(value);
    }

    public static TypeName of(final String value) {
        return new TypeName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
