package com.example.oulearning.training.domain.request.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

/**
 * Thrown when an employee attempting manager actions does not possess the MANAGER role.
 */
public class UnauthorizedManagerException extends DomainException {

    public UnauthorizedManagerException(String message) {
        super(message);
    }
}
