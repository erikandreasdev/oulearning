package com.example.oulearning.budgeting.infrastructure.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * REST Request DTO for budget fund operations (reserve, release, consume, spend-direct).
 */
@Schema(description = "Payload for performing a fund operation (reserve, release, consume, spend-direct)")
public record FundOperationRequest(
        @NotNull(message = "Amount cannot be null")
        @DecimalMin(value = "0.01", message = "Amount must be strictly positive")
        @Schema(description = "Amount for the operation", example = "500.00")
        BigDecimal amount,

        @Schema(description = "Optional currency code (defaults to budget currency)", example = "EUR")
        String currencyCode) {}
