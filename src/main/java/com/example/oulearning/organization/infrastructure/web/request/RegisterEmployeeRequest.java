package com.example.oulearning.organization.infrastructure.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

/**
 * Request DTO for registering an employee and assigning them to an Organizational Unit.
 */
@Schema(description = "Payload for registering a new employee with an assigned organizational unit")
public record RegisterEmployeeRequest(
        @Schema(description = "Corporate Key identifier (e.g. CK0001)", example = "CK0001")
        @NotBlank(message = "Corporate key is required")
        @Pattern(regexp = "^[A-Za-z0-9]{3,10}$", message = "Corporate key must be 3-10 alphanumeric characters")
        String corporateKey,

        @Schema(description = "First Name", example = "Alice")
        @NotBlank(message = "First name is required")
        String firstName,

        @Schema(description = "Last Name", example = "Smith")
        @NotBlank(message = "Last name is required")
        String lastName,

        @Schema(description = "Email address", example = "alice.smith@example.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @Schema(description = "Contact Phone number", example = "+34911223344", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String phone,

        @Schema(description = "Employee Role (EMPLOYEE, MANAGER, TRAINER, ADMIN)", example = "MANAGER")
        @NotBlank(message = "Role is required")
        String role,

        @Schema(description = "Target Organizational Unit ID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
        @NotNull(message = "OU ID is required")
        UUID ouId) {}
