package com.example.oulearning.training.application.port.in.command;

import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.training.domain.model.TrainingPurposeType;
import com.example.oulearning.training.domain.model.TrainingStatus;
import com.example.oulearning.training.domain.model.TypeId;
import java.math.BigDecimal;

public record ListTrainingRequestsQuery(
        String name,
        BigDecimal costAmount,
        OrganizationalUnitId organizationalUnitId,
        TrainingPurposeType purposeType,
        TypeId typeId,
        Integer hours,
        TrainingStatus status,
        Integer page,
        Integer size) {}
