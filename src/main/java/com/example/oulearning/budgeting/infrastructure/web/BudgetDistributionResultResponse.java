package com.example.oulearning.budgeting.infrastructure.web;

import com.example.oulearning.budgeting.application.BudgetDistributionResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * REST Response DTO representing the outcome of a budget distribution operation.
 */
@Schema(description = "Result of budget distribution operation")
public record BudgetDistributionResultResponse(
        @Schema(description = "Parent budget")
        BudgetResponse parentBudget,

        @Schema(description = "List of distributed child budgets")
        List<BudgetResponse> childBudgets) {

    public static BudgetDistributionResultResponse fromDomain(BudgetDistributionResult result) {
        if (result == null) {
            return null;
        }

        final var parent = BudgetResponse.fromDomain(result.parentBudget());
        final var children = result.childBudgets().stream()
                .map(BudgetResponse::fromDomain)
                .toList();

        return new BudgetDistributionResultResponse(parent, children);
    }
}
