package com.example.oulearning.organization.infrastructure.web.dto;

public record EmployeeResponseDto(
        Long id,
        String name,
        String surname,
        String email,
        boolean active) {
}
