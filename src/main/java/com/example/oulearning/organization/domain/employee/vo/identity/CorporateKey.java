package com.example.oulearning.organization.domain.employee.vo.identity;

import com.example.oulearning.organization.domain.employee.exception.identity.InvalidCorporateKeyException;
import java.util.regex.Pattern;

/**
 * Value object representing a Corporate Key.
 * Format: "CK" followed by exactly 4 digits (e.g. "CK0001", "CK1234").
 */
public record CorporateKey(String value) {

    private static final Pattern PATTERN = Pattern.compile("^CK\\d{4}$");

    public CorporateKey {
        if (value == null || value.isBlank()) {
            throw new InvalidCorporateKeyException("CorporateKey cannot be null or blank");
        }
        final var trimmed = value.strip();
        if (!PATTERN.matcher(trimmed).matches()) {
            throw new InvalidCorporateKeyException(
                    "Invalid CorporateKey format: '%s'. Expected format: CK followed by 4 digits (e.g. CK0001)"
                            .formatted(value));
        }
        value = trimmed;
    }

    public static CorporateKey of(String value) {
        return new CorporateKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
