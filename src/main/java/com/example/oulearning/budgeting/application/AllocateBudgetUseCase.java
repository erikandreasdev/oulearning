package com.example.oulearning.budgeting.application;

import java.util.UUID;

/**
 * Use case input port for allocating initial budget to an OU.
 */
public interface AllocateBudgetUseCase {
    UUID execute(AllocateBudgetCommand command);
}
