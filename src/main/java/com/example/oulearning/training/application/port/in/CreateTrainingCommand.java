package com.example.oulearning.training.application.port.in;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.training.domain.model.TrainingPurposeType;
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
