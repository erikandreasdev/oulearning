package com.example.oulearning.training.domain.request.vo.identity;

import com.example.oulearning.training.domain.request.exception.InvalidTrainingRequestException;
import java.util.regex.Pattern;

/**
 * Value Object representing a corporate employee key in Training domain.
 */
public record CorporateKey(String value) {

    private static final Pattern PATTERN = Pattern.compile("^CK\\d{4}$");

    public CorporateKey {
        if (value == null || value.isBlank()) {
            throw new InvalidTrainingRequestException("CorporateKey cannot be null or blank");
        }
        value = value.trim().toUpperCase();
        if (!PATTERN.matcher(value).matches()) {
            throw new InvalidTrainingRequestException(
                    "Invalid CorporateKey format: '%s'. Expected format: CK followed by 4 digits (e.g. CK0001)".formatted(value));
        }
    }

    public static CorporateKey of(String value) {
        return new CorporateKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
