package com.example.oulearning.organization.domain.employee.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

public final class InvalidPhoneException extends DomainException {

    public InvalidPhoneException(String message) {
        super(message);
    }
}
