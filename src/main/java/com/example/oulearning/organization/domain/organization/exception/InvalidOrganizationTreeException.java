package com.example.oulearning.organization.domain.organization.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

/**
 * Thrown when the hierarchical structure in an uploaded organization file is invalid (e.g. missing parent or multiple roots).
 */
public class InvalidOrganizationTreeException extends DomainException {

    public InvalidOrganizationTreeException(String message) {
        super(message);
    }
}
