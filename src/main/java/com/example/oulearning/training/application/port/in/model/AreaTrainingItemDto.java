package com.example.oulearning.training.application.port.in.model;

import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.training.domain.model.Cost;
import com.example.oulearning.training.domain.model.TrainingId;
import com.example.oulearning.training.domain.model.TrainingName;
import com.example.oulearning.training.domain.model.TrainingStatus;
import java.util.List;

public record AreaTrainingItemDto(
        TrainingId id,
        TrainingName name,
        List<OrganizationalUnitId> organizationalUnitIds,
        Cost cost,
        TrainingStatus status) {}
