package com.example.oulearning.training.application.port.in.model;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;

public record AttendeeDetailsDto(
        EmployeeId id,
        String name,
        String email) {}
