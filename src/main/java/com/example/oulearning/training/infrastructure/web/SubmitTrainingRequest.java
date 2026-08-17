package com.example.oulearning.training.infrastructure.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * REST Request DTO for submitting a new training request.
 */
@Schema(description = "Payload for submitting an OU employee training request")
public record SubmitTrainingRequest(
        @Schema(description = "Optional request UUID (auto-generated if omitted)", example = "b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
        UUID id,

        @NotNull(message = "OU ID cannot be null")
        @Schema(description = "UUID of the organizational unit requesting the training", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44")
        UUID ouId,

        @NotBlank(message = "Requester corporate key cannot be blank")
        @Schema(description = "Corporate key of the OU owner requesting the training", example = "CK0001")
        String requesterCorporateKey,

        @NotBlank(message = "Training name cannot be blank")
        @Size(max = 200, message = "Training name cannot exceed 200 characters")
        @Schema(description = "Name or title of the training program", example = "Advanced DDD and Hexagonal Architecture")
        String name,

        @NotNull(message = "Cost amount cannot be null")
        @DecimalMin(value = "0.00", message = "Cost amount must be greater than or equal to 0.00")
        @Schema(description = "Total financial cost of the training", example = "1500.00")
        BigDecimal costAmount,

        @Schema(description = "Currency code (defaults to EUR)", example = "EUR")
        String costCurrency,

        @NotBlank(message = "Purpose type cannot be blank")
        @Schema(description = "Training purpose category (UPSKILLING, RESKILLING, CERTIFICATION, COMPLIANCE, OTHER)", example = "UPSKILLING")
        String purposeType,

        @Schema(description = "Custom purpose text required when purposeType is OTHER", example = "Specialized AI workflow tooling workshop")
        String purposeCustomText,

        @Min(value = 1, message = "Training hours must be at least 1")
        @Max(value = 1000, message = "Training hours cannot exceed 1000")
        @Schema(description = "Total hours required to complete training", example = "40")
        int trainingHours,

        @Schema(description = "Whether this training is available at OrgUniversity (internal elearning platform)", example = "true")
        boolean availableAtOrgUniversity,

        @NotEmpty(message = "Assistants list cannot be empty")
        @Schema(description = "List of employee corporate keys who will assist to the training (must be members of the OU)", example = "[\"CK0001\", \"CK0002\"]")
        Set<String> assistantCorporateKeys) {}
