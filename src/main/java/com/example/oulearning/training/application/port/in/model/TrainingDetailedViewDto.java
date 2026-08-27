package com.example.oulearning.training.application.port.in.model;

import com.example.oulearning.training.domain.model.Cost;
import com.example.oulearning.training.domain.model.Hours;
import com.example.oulearning.training.domain.model.TrainingId;
import com.example.oulearning.training.domain.model.TrainingName;
import com.example.oulearning.training.domain.model.TrainingPurpose;
import com.example.oulearning.training.domain.model.TypeId;
import java.util.List;

public record TrainingDetailedViewDto(
        TrainingId id,
        TrainingName name,
        Cost cost,
        String requestedByName,
        TrainingPurpose purpose,
        TypeId typeId,
        Hours hours,
        List<AttendeeDetailsDto> attendees) {}
