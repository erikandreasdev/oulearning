package com.example.oulearning.training.application;

import com.example.oulearning.organization.domain.employee.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;
import com.example.oulearning.training.domain.TrainingPurposeType;
import java.math.BigDecimal;

public record CreateTrainingCommand(
        EmployeeId requestedBy,
        OrganizationalUnitId ouId,
        String name,
        BigDecimal costAmount,
        String currency,
        int hours,
        TrainingPurposeType purposeType,
        String purposeDescription,
        long typeId) {
}
