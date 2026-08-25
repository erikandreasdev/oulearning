package com.example.oulearning.budgeting.application;

import com.example.oulearning.budgeting.domain.BudgetId;
import java.math.BigDecimal;

public record UpdateBudgetCommand(BudgetId id, BigDecimal total, BigDecimal reserved, BigDecimal available) {
}
