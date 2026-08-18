package com.example.oulearning.budgeting.infrastructure.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Request DTO for distributing budget from a parent OU to child OUs.
 */
@Schema(description = "Payload for distributing parent OU budget among child OUs")
public record DistributeBudgetRequest(
        @NotNull(message = "Parent OU ID cannot be null")
        @Schema(description = "UUID of the parent organizational unit", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
        UUID parentOuId,

        @NotNull(message = "Strategy type cannot be null")
        @Schema(description = "Distribution strategy: EXCLUSIVE, EQUAL, or EXPLICIT", example = "EQUAL")
        String strategyType,

        @Schema(description = "Child OU UUIDs (required for EQUAL strategy)", example = "[\"b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22\", \"b2eebc99-9c0b-4ef8-bb6d-6bb9bd380a33\"]")
        List<UUID> childOuIds,

        @Schema(description = "Explicit allocation mapping: Child OU UUID -> Amount (required for EXPLICIT strategy)")
        Map<UUID, BigDecimal> explicitAllocations,

        @Schema(description = "Optional currency code (defaults to parent budget currency)", example = "EUR")
        String currencyCode) {

    public DistributeBudgetRequest {
        childOuIds = childOuIds != null ? List.copyOf(childOuIds) : List.of();
        explicitAllocations = explicitAllocations != null ? Map.copyOf(explicitAllocations) : Map.of();
    }
}
