package com.example.oulearning.training.domain.event;

import com.example.oulearning.training.domain.Cost;
import com.example.oulearning.training.domain.Hours;
import com.example.oulearning.training.domain.Id;
import com.example.oulearning.training.domain.TrainingName;
import com.example.oulearning.training.domain.TrainingPurpose;
import com.example.oulearning.training.domain.TypeId;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a new training is requested.
 */
public record TrainingRequested(
        Id trainingId,
        com.example.oulearning.organization.domain.employee.Id requestedBy,
        com.example.oulearning.organization.domain.hierarchy.Id ouId,
        TrainingName name,
        Cost cost,
        Hours hours,
        TrainingPurpose purpose,
        TypeId typeId,
        Instant occurredAt) {

    public TrainingRequested {
        Objects.requireNonNull(trainingId, "trainingId cannot be null");
        Objects.requireNonNull(requestedBy, "requestedBy cannot be null");
        Objects.requireNonNull(ouId, "ouId cannot be null");
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(cost, "cost cannot be null");
        Objects.requireNonNull(hours, "hours cannot be null");
        Objects.requireNonNull(purpose, "purpose cannot be null");
        Objects.requireNonNull(typeId, "typeId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }
}
