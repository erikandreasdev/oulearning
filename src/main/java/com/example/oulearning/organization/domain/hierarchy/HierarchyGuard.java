package com.example.oulearning.organization.domain.hierarchy;

import com.example.oulearning.organization.domain.employee.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.exception.InvalidOrganizationalUnitException;
import java.util.Set;

final class HierarchyGuard {

    private static final String FIELD_OU_ID = "Organizational unit id";
    private static final String FIELD_OU_NAME = "Organizational unit name";
    private static final String FIELD_NAME_VO = "Name";
    private static final String FIELD_EMPLOYEE_ID = "Employee id";
    private static final String FIELD_EMPLOYEE_IDS = "Employee ids";

    private HierarchyGuard() {
    }

    static void requireOrganizationalUnitId(final OrganizationalUnitId id) {
        requireNonNull(id, FIELD_OU_ID);
    }

    static void requirePositiveOrganizationalUnitId(final long value) {
        requirePositiveId(value, FIELD_OU_ID);
    }

    static long requireValidOrganizationalUnitId(final String value) {
        return requireValidId(value, FIELD_OU_ID);
    }

    static void requireName(final Name name) {
        requireNonNull(name, FIELD_NAME_VO);
    }

    static String requireValidOrganizationalUnitName(final String value) {
        return requireLengthBetween(
                value,
                FIELD_OU_NAME,
                HierarchyConstants.MIN_NAME_LENGTH,
                HierarchyConstants.MAX_NAME_LENGTH);
    }

    static void requireEmployeeId(final EmployeeId employeeId) {
        requireNonNull(employeeId, FIELD_EMPLOYEE_ID);
    }

    static void requireEmployeeIds(final Set<EmployeeId> employeeIds) {
        requireNonNull(employeeIds, FIELD_EMPLOYEE_IDS);
        employeeIds.forEach(id -> requireNonNull(id, FIELD_EMPLOYEE_ID));
    }

    private static <T> void requireNonNull(final T value, final String fieldName) {
        if (value == null) {
            throw InvalidOrganizationalUnitException.nullField(fieldName);
        }
    }

    private static String requireNonBlank(final String value, final String fieldName) {
        requireNonNull(value, fieldName);
        final var notNull = value.strip();
        if (notNull.isBlank()) {
            throw InvalidOrganizationalUnitException.blankField(fieldName);
        }
        return notNull;
    }

    private static String requireLengthBetween(
            final String value, final String fieldName, final int min, final int max) {
        final var stripped = requireNonBlank(value, fieldName);
        if (stripped.length() < min || stripped.length() > max) {
            throw InvalidOrganizationalUnitException.lengthOutOfRange(
                    fieldName, min, max, stripped);
        }
        return stripped;
    }

    private static void requirePositiveId(final long value, final String fieldName) {
        if (value < HierarchyConstants.MIN_ID) {
            throw InvalidOrganizationalUnitException.nonPositiveId(fieldName, value);
        }
    }

    private static long requireValidId(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw InvalidOrganizationalUnitException.nullOrBlank(fieldName);
        }
        try {
            final var parsed = Long.parseLong(value.strip());
            requirePositiveId(parsed, fieldName);
            return parsed;
        } catch (final NumberFormatException e) {
            throw InvalidOrganizationalUnitException.invalidId(fieldName, value, e);
        }
    }
}
