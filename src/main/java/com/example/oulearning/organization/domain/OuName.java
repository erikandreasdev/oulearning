package com.example.oulearning.organization.domain;

/**
 * Value object representing the name of an organizational unit.
 *
 * @param value the normalized OU name string
 */
public record OuName(String value) {

    /**
     * Compact constructor enforcing non-null, non-blank, and max 100 character length.
     */
    public OuName {
        if (value == null) {
            throw new InvalidOuException("OuName cannot be null");
        }

        String normalized = value.strip().replaceAll("\\s+", " ");
        if (normalized.isBlank()) {
            throw new InvalidOuException("OuName cannot be blank");
        }
        if (normalized.length() > 100) {
            throw new InvalidOuException(
                    "OuName '%s' cannot exceed 100 characters (actual: %d)".formatted(normalized, normalized.length()));
        }

        value = normalized;
    }

    /**
     * Factory method to create an {@link OuName}.
     *
     * @param value the raw OU name string
     * @return the {@link OuName}
     */
    public static OuName of(String value) {
        return new OuName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
