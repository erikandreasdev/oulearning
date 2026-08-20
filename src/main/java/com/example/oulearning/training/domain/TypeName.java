package com.example.oulearning.training.domain;


public record TypeName(String value) {

    public TypeName {
        value = TrainingGuard.requireTypeName(value);
    }

    public static TypeName of(final String value) {
        return new TypeName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
