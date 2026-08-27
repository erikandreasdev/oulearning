package com.example.oulearning.training.application.port.in.command;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.training.domain.model.TrainingPurposeType;
import com.example.oulearning.training.domain.model.TypeId;
import java.math.BigDecimal;
import java.util.Set;

public record RequestNewTrainingCommand(
        EmployeeId requestedBy,
        OrganizationalUnitId organizationalUnitId,
        String name,
        BigDecimal costAmount,
        String currency,
        Integer hours,
        TrainingPurposeType purposeType,
        String purposeDescription,
        TypeId typeId,
        Set<EmployeeId> attendees) {}
