package com.example.oulearning.training.domain.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

/**
 * Thrown when a rejection reason is null, blank, or invalid.
 */
public class InvalidRejectionReasonException extends DomainException {

    public InvalidRejectionReasonException(String message) {
        super(message);
    }
}
