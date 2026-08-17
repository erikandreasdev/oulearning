package com.example.oulearning.organization.domain.organization.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

/**
 * Thrown when an uploaded file cannot be parsed or lacks mandatory columns/headers.
 */
public class InvalidFileFormatException extends DomainException {

    public InvalidFileFormatException(String message) {
        super(message);
    }
}
