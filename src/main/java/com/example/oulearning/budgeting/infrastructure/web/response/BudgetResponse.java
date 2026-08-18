package com.example.oulearning.budgeting.infrastructure.web.response;

import com.example.oulearning.budgeting.domain.budget.Budget;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * REST Response DTO representing a Budget aggregate.
 */
@Schema(description = "Details of an organizational unit budget")
public record BudgetResponse(
        @Schema(description = "Unique budget identifier", example = "d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a44")
        UUID budgetId,

        @Schema(description = "Associated organizational unit UUID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
        UUID ouId,

        @Schema(description = "Fiscal year of the budget", example = "2026")
        Integer fiscalYear,

        @Schema(description = "Total allocated amount", example = "25000.00")
        BigDecimal allocatedAmount,

        @Schema(description = "Reserved amount", example = "5000.00")
        BigDecimal reservedAmount,

        @Schema(description = "Spent amount", example = "2000.00")
        BigDecimal spentAmount,

        @Schema(description = "Computed available amount to use", example = "18000.00")
        BigDecimal availableAmount,

        @Schema(description = "Currency code", example = "EUR")
        String currency) {

    public static BudgetResponse fromDomain(Budget budget) {
        if (budget == null) {
            return null;
        }

        return new BudgetResponse(
                budget.id().value(),
                budget.ouId().value(),
                budget.fiscalYear().value(),
                budget.allocated().amount(),
                budget.reserved().amount(),
                budget.spent().amount(),
                budget.available().amount(),
                budget.allocated().currency().getCurrencyCode());
    }
}
