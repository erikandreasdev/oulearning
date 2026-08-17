package com.example.oulearning.training.application;

import java.util.UUID;

/**
 * Query to retrieve a training request by its ID.
 */
public record GetTrainingRequestQuery(UUID id) {}
