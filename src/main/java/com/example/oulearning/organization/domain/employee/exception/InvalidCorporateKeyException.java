package com.example.oulearning.organization.domain.employee.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

public final class InvalidCorporateKeyException extends DomainException {

    public InvalidCorporateKeyException(String message) {
        super(message);
    }
}
