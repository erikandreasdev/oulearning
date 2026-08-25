package com.example.oulearning.training.domain;

import java.util.Optional;

public interface TrainingRepository {
    Optional<Training> findById(TrainingId id);

    void save(Training training);
}
