package com.example.oulearning.shared.domain.fiscal;

import com.example.oulearning.shared.domain.exception.DomainException;

/**
 * Exception thrown when a fiscal year is invalid.
 */
public class InvalidFiscalYearException extends DomainException {

    public InvalidFiscalYearException(String message) {
        super(message);
    }
}
