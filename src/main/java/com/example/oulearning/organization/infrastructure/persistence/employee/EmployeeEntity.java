package com.example.oulearning.organization.infrastructure.persistence.employee;

/**
 * Immutable persistence entity record representing an Employee in the database.
 */
public record EmployeeEntity(
        String corporateKey,
        String firstName,
        String lastName,
        String email,
        String phone,
        String role,
        String ouId,
        Long version) {}
