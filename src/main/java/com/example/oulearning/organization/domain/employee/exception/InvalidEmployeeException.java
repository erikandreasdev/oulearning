package com.example.oulearning.organization.domain.employee.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

public final class InvalidEmployeeException extends DomainException {

    public InvalidEmployeeException(String message) {
        super(message);
    }
}
