package com.example.oulearning.organization.domain;

import static com.example.oulearning.shared.domain.DomainPatterns.CORPORATE_KEY_PATTERN;

/**
 * Value object representing a corporate key assigned to an employee.
 * <p>
 * Ensures the corporate key is normalized (trimmed and converted to uppercase) and matches the format {@code CK}
 * followed by exactly 4 numeric digits (e.g. {@code CK0001}, {@code CK1234}).
 * </p>
 *
 * @param value the normalized corporate key string
 */
public record CorporateKey(String value) {

    /**
     * Compact constructor enforcing corporate key invariants and normalization.
     */
    public CorporateKey {
        if (value == null) {
            throw new InvalidCorporateKeyException(null, "Corporate key cannot be null");
        }

        String normalized = value.strip().toUpperCase();
        if (normalized.isBlank()) {
            throw new InvalidCorporateKeyException(value, "Corporate key cannot be blank");
        }

        if (!CORPORATE_KEY_PATTERN.matcher(normalized).matches()) {
            throw new InvalidCorporateKeyException(
                    normalized, "Invalid corporate key format: %s. Expected CK followed by 4 digits".formatted(normalized));
        }

        value = normalized;
    }

    /**
     * Factory method to create a {@link CorporateKey}.
     *
     * @param value the raw corporate key string
     * @return a validated {@link CorporateKey} value object
     */
    public static CorporateKey of(String value) {
        return new CorporateKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
