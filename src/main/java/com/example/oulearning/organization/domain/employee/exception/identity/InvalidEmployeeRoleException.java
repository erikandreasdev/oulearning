package com.example.oulearning.organization.domain.employee.exception.identity;

import com.example.oulearning.shared.domain.exception.DomainException;

public final class InvalidEmployeeRoleException extends DomainException {

    public InvalidEmployeeRoleException(String message) {
        super(message);
    }
}
