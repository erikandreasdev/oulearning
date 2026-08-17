package com.example.oulearning.budgeting.infrastructure.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * REST Request DTO for initial budget allocation to an OU.
 */
@Schema(description = "Payload for allocating a budget to an organizational unit")
public record AllocateBudgetRequest(
        @Schema(description = "Optional budget UUID (auto-generated if omitted)", example = "d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a44")
        UUID budgetId,

        @NotNull(message = "OU ID cannot be null")
        @Schema(description = "UUID of the organizational unit receiving the budget", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
        UUID ouId,

        @NotNull(message = "Amount cannot be null")
        @DecimalMin(value = "0.00", message = "Amount must be greater than or equal to 0.00")
        @Schema(description = "Allocated amount", example = "25000.00")
        BigDecimal amount,

        @Schema(description = "Currency code (defaults to EUR)", example = "EUR")
        String currencyCode) {}
