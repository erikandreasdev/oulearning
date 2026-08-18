package com.example.oulearning.training.application.port.in.query;

import java.util.UUID;

/**
 * Query to retrieve all training requests for a given OU and optional Fiscal Year.
 */
public record GetTrainingRequestsByOuQuery(UUID ouId, Integer fiscalYear) {

    public static GetTrainingRequestsByOuQuery of(UUID ouId) {
        return new GetTrainingRequestsByOuQuery(ouId, null);
    }

    public static GetTrainingRequestsByOuQuery of(UUID ouId, Integer fiscalYear) {
        return new GetTrainingRequestsByOuQuery(ouId, fiscalYear);
    }
}
