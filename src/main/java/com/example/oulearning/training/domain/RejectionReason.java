package com.example.oulearning.training.domain;

import com.example.oulearning.training.domain.exception.InvalidRejectionReasonException;

/**
 * Value Object representing a formal reason why a training request was rejected by a manager.
 */
public record RejectionReason(String value) {

    public static final int MAX_LENGTH = 500;

    public RejectionReason {
        if (value == null || value.isBlank()) {
            throw new InvalidRejectionReasonException("Rejection reason cannot be null or blank");
        }
        value = value.strip();
        if (value.length() > MAX_LENGTH) {
            throw new InvalidRejectionReasonException(
                    "Rejection reason length cannot exceed %d characters".formatted(MAX_LENGTH));
        }
    }

    public static RejectionReason of(String value) {
        return new RejectionReason(value);
    }
}
