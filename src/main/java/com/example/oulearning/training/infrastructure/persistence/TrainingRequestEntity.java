package com.example.oulearning.training.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Persistence entity representing a row in the TRAINING_REQUESTS table.
 */
public record TrainingRequestEntity(
        String id,
        String ouId,
        String requesterCorporateKey,
        String name,
        BigDecimal costAmount,
        String costCurrency,
        String purposeType,
        String purposeCustomText,
        Integer trainingHours,
        Integer availableAtOrgUniversity,
        Integer fiscalYear,
        String status,
        String reviewedBy,
        String rejectionReason,
        String managerNotes,
        Instant reviewedAt,
        Instant createdAt,
        Long version) {}
