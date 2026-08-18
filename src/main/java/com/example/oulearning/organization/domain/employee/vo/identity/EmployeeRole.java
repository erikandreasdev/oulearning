package com.example.oulearning.organization.domain.employee.vo.identity;

import com.example.oulearning.organization.domain.employee.exception.identity.InvalidEmployeeRoleException;
import java.util.Locale;

/**
 * Functional roles assigned to employees within the organization.
 */
public enum EmployeeRole {
    EMPLOYEE,
    MANAGER,
    TRAINER,
    ADMIN;

    public static EmployeeRole fromString(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new InvalidEmployeeRoleException("EmployeeRole cannot be null or blank");
        }
        try {
            return EmployeeRole.valueOf(rawValue.strip().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new InvalidEmployeeRoleException("Invalid EmployeeRole: '%s'".formatted(rawValue));
        }
    }
}
