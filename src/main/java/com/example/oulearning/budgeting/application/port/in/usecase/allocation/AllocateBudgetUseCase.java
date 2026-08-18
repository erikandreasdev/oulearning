package com.example.oulearning.budgeting.application.port.in.usecase.allocation;

import java.util.UUID;
import com.example.oulearning.budgeting.application.port.in.command.AllocateBudgetCommand;

/**
 * Use case input port for allocating initial budget to an OU.
 */
public interface AllocateBudgetUseCase {
    UUID execute(AllocateBudgetCommand command);
}
