package com.example.oulearning.training.domain.repository;

import com.example.oulearning.training.domain.model.*;

import java.util.Optional;

public interface TrainingRepository {
    Optional<Training> findById(TrainingId id);

    void save(Training training);
}
