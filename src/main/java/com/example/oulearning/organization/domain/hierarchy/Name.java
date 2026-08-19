package com.example.oulearning.organization.domain.hierarchy;


public record Name(String value) {

    public Name {
        value = HierarchyGuard.requireLengthBetween(
                value, "Ou name", HierarchyConstants.MIN_NAME_LENGTH, HierarchyConstants.MAX_NAME_LENGTH);
    }

    public static Name of(final String value) {
        return new Name(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
