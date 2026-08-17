package com.example.oulearning.organization.infrastructure.web;

import com.example.oulearning.organization.domain.employee.Employee;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

/**
 * Response DTO representing an Employee and their assigned Organizational Unit.
 */
@Schema(description = "Employee representation with organizational unit assignment")
public record EmployeeResponse(
        @Schema(description = "Corporate Key identifier", example = "CK0001")
        String corporateKey,

        @Schema(description = "First Name", example = "Alice")
        String firstName,

        @Schema(description = "Last Name", example = "Smith")
        String lastName,

        @Schema(description = "Email address", example = "alice.smith@example.com")
        String email,

        @Schema(description = "Contact Phone", example = "+34911223344", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String phone,

        @Schema(description = "Role", example = "MANAGER")
        String role,

        @Schema(description = "Assigned Organizational Unit ID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
        UUID ouId) {

    public static EmployeeResponse fromDomain(Employee employee) {
        if (employee == null) {
            return null;
        }
        final var phoneStr = employee.phone() != null ? employee.phone().value() : null;
        return new EmployeeResponse(
                employee.corporateKey().value(),
                employee.fullName().name().value(),
                employee.fullName().surname().value(),
                employee.email().value(),
                phoneStr,
                employee.role().name(),
                employee.ouId().value());
    }
}
