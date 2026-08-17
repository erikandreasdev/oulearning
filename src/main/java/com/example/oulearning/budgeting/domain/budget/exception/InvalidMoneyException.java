package com.example.oulearning.budgeting.domain.budget.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

/**
 * Domain exception thrown when monetary operations or amounts violate domain invariants.
 */
public final class InvalidMoneyException extends DomainException {

    public InvalidMoneyException(String message) {
        super(message);
    }
}
