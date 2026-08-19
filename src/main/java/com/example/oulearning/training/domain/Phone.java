package com.example.oulearning.training.domain;

import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import java.util.regex.Pattern;

/**
 * Value object representing a valid phone number.
 *
 * @param value the normalized phone number string
 */
public record Phone(String value) {

    private static final Pattern PATTERN = Pattern.compile(TrainingConstants.PHONE_REGEX);

    public Phone {
        final var stripped = TrainingGuard.requireNonBlank(value, "Phone");
        final var normalized = stripped.replaceAll("[\\s\\-\\(\\)\\.]", "");
        if (!PATTERN.matcher(normalized).matches()) {
            throw InvalidTrainingOperationException.invalidPhoneFormat(
                    stripped, TrainingConstants.PHONE_DIGITS_MIN, TrainingConstants.PHONE_DIGITS_MAX);
        }
        value = normalized;
    }

    public static Phone of(final String value) {
        return new Phone(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
