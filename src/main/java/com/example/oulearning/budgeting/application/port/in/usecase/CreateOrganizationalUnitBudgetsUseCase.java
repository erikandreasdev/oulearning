package com.example.oulearning.budgeting.application.port.in.usecase;

import com.example.oulearning.budgeting.application.port.in.command.CreateOrganizationalUnitBudgetsCommand;
import com.example.oulearning.budgeting.application.port.in.model.PaginatedBudgetsResult;

public interface CreateOrganizationalUnitBudgetsUseCase {
    PaginatedBudgetsResult execute(CreateOrganizationalUnitBudgetsCommand command);
}
