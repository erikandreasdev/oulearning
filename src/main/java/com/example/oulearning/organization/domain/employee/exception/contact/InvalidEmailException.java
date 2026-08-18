package com.example.oulearning.organization.domain.employee.exception.contact;

import com.example.oulearning.shared.domain.exception.DomainException;

public final class InvalidEmailException extends DomainException {

    public InvalidEmailException(String message) {
        super(message);
    }
}
