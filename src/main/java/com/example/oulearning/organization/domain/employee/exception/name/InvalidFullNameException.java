package com.example.oulearning.organization.domain.employee.exception.name;

import com.example.oulearning.shared.domain.exception.DomainException;

public final class InvalidFullNameException extends DomainException {

    public InvalidFullNameException(String message) {
        super(message);
    }
}
