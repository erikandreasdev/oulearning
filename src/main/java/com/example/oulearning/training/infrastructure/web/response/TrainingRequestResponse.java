package com.example.oulearning.training.infrastructure.web.response;

import com.example.oulearning.training.domain.request.vo.identity.CorporateKey;
import com.example.oulearning.training.domain.request.TrainingRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Response DTO representing a {@link TrainingRequest} aggregate.
 */
@Schema(description = "Details of an OU training request")
public record TrainingRequestResponse(
        @Schema(description = "Unique training request identifier", example = "b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
        UUID id,

        @Schema(description = "Associated organizational unit UUID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44")
        UUID ouId,

        @Schema(description = "Corporate key of the requesting owner", example = "CK0001")
        String requesterCorporateKey,

        @Schema(description = "Training name", example = "Advanced DDD and Hexagonal Architecture")
        String name,

        @Schema(description = "Cost amount", example = "1500.00")
        BigDecimal costAmount,

        @Schema(description = "Cost currency code", example = "EUR")
        String costCurrency,

        @Schema(description = "Purpose category", example = "UPSKILLING")
        String purposeType,

        @Schema(description = "Custom purpose text if type is OTHER", example = "Specialized AI workshop")
        String purposeCustomText,

        @Schema(description = "Total hours required", example = "40")
        int trainingHours,

        @Schema(description = "Whether training is available at OrgUniversity", example = "true")
        boolean availableAtOrgUniversity,

        @Schema(description = "Employee assistants participating in training", example = "[\"CK0001\", \"CK0002\"]")
        Set<String> assistants,

        @Schema(description = "Fiscal Year of the request", example = "2026")
        int fiscalYear,

        @Schema(description = "Status of the training request (DRAFT, APPROVED, REJECTED, CANCELLED)", example = "DRAFT")
        String status,

        @Schema(description = "Corporate key of the reviewing manager", example = "CK0099")
        String reviewedBy,

        @Schema(description = "Reason for rejection if status is REJECTED", example = "Budget allocation exceeded")
        String rejectionReason,

        @Schema(description = "Optional manager review notes / extra fields", example = "Approved for Q3 initiative")
        String managerNotes,

        @Schema(description = "Timestamp when the request was reviewed", example = "2026-08-17T22:30:00Z")
        Instant reviewedAt,

        @Schema(description = "Timestamp when the request was created", example = "2026-08-17T22:00:00Z")
        Instant createdAt) {

    public static TrainingRequestResponse fromDomain(TrainingRequest domain) {
        if (domain == null) {
            return null;
        }

        final var assistants = domain.assistants().stream()
                .map(CorporateKey::value)
                .collect(Collectors.toSet());

        return new TrainingRequestResponse(
                domain.id().value(),
                domain.ouId().value(),
                domain.requester().value(),
                domain.name().value(),
                domain.cost().amount(),
                domain.cost().currency(),
                domain.purpose().type().name(),
                domain.purpose().customText(),
                domain.hours().value(),
                domain.availableAtOrgUniversity(),
                assistants,
                domain.fiscalYear().value(),
                domain.status().name(),
                domain.reviewedBy() != null ? domain.reviewedBy().value() : null,
                domain.rejectionReason() != null ? domain.rejectionReason().value() : null,
                domain.managerNotes() != null ? domain.managerNotes().value() : null,
                domain.reviewedAt(),
                domain.createdAt());
    }
}
