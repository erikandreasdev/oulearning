package com.example.oulearning.training.domain;

/**
 * Value object representing a training type name.
 *
 * @param value the non-blank type name string
 */
public record TypeName(String value) {

    public TypeName {
        if (value == null) {
            throw new InvalidTrainingOperationException("TypeName cannot be null");
        }
        value = value.strip();
        if (value.isBlank()) {
            throw new InvalidTrainingOperationException("TypeName cannot be blank");
        }
    }

    public static TypeName of(String value) {
        return new TypeName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
