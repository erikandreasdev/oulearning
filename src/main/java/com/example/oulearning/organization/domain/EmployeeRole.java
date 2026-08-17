package com.example.oulearning.organization.domain;

import java.util.Arrays;

/**
 * Functional organizational roles assigned to an employee.
 */
public enum EmployeeRole {
    EMPLOYEE,
    MANAGER,
    TRAINER,
    ADMIN;

    /**
     * Parses a raw string into an {@link EmployeeRole}.
     *
     * @param rawValue the raw role string
     * @return the matching {@link EmployeeRole}
     * @throws InvalidEmployeeRoleException if {@code rawValue} is null, blank, or not a recognized role
     */
    public static EmployeeRole parse(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new InvalidEmployeeRoleException(rawValue, "Employee role cannot be null or blank");
        }

        String normalized = rawValue.strip().toUpperCase();
        try {
            return EmployeeRole.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new InvalidEmployeeRoleException(
                    rawValue,
                    "Unknown employee role: '%s'. Valid roles are: %s".formatted(rawValue, Arrays.toString(values())));
        }
    }
}
