package com.example.oulearning.organization.infrastructure.persistence.employee;

public record EmployeeEntity(Long id, String name, String surname, String email, boolean active) {}
