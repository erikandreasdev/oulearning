package com.example.oulearning.training.domain.repository;

import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.training.domain.model.Cost;
import com.example.oulearning.training.domain.model.Hours;
import com.example.oulearning.training.domain.model.TrainingName;
import com.example.oulearning.training.domain.model.TrainingPurposeType;
import com.example.oulearning.training.domain.model.TrainingStatus;
import com.example.oulearning.training.domain.model.TypeId;

public record TrainingFilterCriteria(
        TrainingName name,
        Cost cost,
        OrganizationalUnitId organizationalUnitId,
        TrainingPurposeType purposeType,
        TypeId typeId,
        Hours hours,
        TrainingStatus status) {}
