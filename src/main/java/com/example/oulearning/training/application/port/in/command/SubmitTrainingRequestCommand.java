package com.example.oulearning.training.application.port.in.command;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * Immutable command to submit a new training request.
 */
public record SubmitTrainingRequestCommand(
        UUID id,
        UUID ouId,
        String requesterCorporateKey,
        String name,
        BigDecimal costAmount,
        String costCurrency,
        String purposeType,
        String purposeCustomText,
        int trainingHours,
        boolean availableAtOrgUniversity,
        Set<String> assistantCorporateKeys) {

    public SubmitTrainingRequestCommand {
        assistantCorporateKeys = assistantCorporateKeys != null ? Set.copyOf(assistantCorporateKeys) : Set.of();
    }
}
