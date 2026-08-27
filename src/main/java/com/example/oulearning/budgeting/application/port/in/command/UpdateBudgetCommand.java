package com.example.oulearning.budgeting.application.port.in.command;

import com.example.oulearning.budgeting.domain.model.BudgetId;
import java.math.BigDecimal;

public record UpdateBudgetCommand(BudgetId id, BigDecimal total, BigDecimal reserved, BigDecimal available) {
}
