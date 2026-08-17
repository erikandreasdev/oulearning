package com.example.oulearning.organization.application;

import java.util.Objects;
import java.util.UUID;

/**
 * Command to register a new Employee and assign them to an Organizational Unit.
 */
public record RegisterEmployeeCommand(
        String corporateKey,
        String firstName,
        String lastName,
        String email,
        String phone,
        String role,
        UUID ouId) {

    public RegisterEmployeeCommand {
        Objects.requireNonNull(corporateKey, "corporateKey cannot be null");
        Objects.requireNonNull(firstName, "firstName cannot be null");
        Objects.requireNonNull(lastName, "lastName cannot be null");
        Objects.requireNonNull(email, "email cannot be null");
        Objects.requireNonNull(role, "role cannot be null");
        Objects.requireNonNull(ouId, "ouId cannot be null");
    }
}
