package com.example.oulearning.training.domain.repository;

import com.example.oulearning.training.domain.model.Training;
import com.example.oulearning.training.domain.model.TrainingId;

import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.util.List;
import java.util.Optional;

public interface TrainingRepository {
    Optional<Training> findById(TrainingId id);

    List<Training> findByOrganizationalUnitId(OrganizationalUnitId organizationalUnitId);

    List<Training> findAll(TrainingFilterCriteria criteria, int offset, int limit);

    long count(TrainingFilterCriteria criteria);

    void save(Training training);
}
