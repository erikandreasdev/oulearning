package com.example.oulearning.organization.domain.employee.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

public final class InvalidNameException extends DomainException {

    public InvalidNameException(String message) {
        super(message);
    }
}
