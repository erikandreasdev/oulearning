package com.example.oulearning.organization.domain.employee.exception.name;

import com.example.oulearning.shared.domain.exception.DomainException;

public final class InvalidSurnameException extends DomainException {

    public InvalidSurnameException(String message) {
        super(message);
    }
}
