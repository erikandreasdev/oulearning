package com.example.oulearning.training.domain.request.vo.decision;

import com.example.oulearning.training.domain.request.exception.InvalidTrainingRequestException;

/**
 * Value Object representing optional reviewer/manager notes or extra informational context.
 */
public record ManagerNotes(String value) {

    public static final int MAX_LENGTH = 1000;

    public ManagerNotes {
        if (value != null) {
            value = value.strip();
            if (value.length() > MAX_LENGTH) {
                throw new InvalidTrainingRequestException(
                        "Manager notes length cannot exceed %d characters".formatted(MAX_LENGTH));
            }
        }
    }

    public static ManagerNotes of(String value) {
        return value != null && !value.isBlank() ? new ManagerNotes(value) : null;
    }
}
