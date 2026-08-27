package com.example.oulearning.budgeting.application.port.in.model;

import java.util.List;

public record PaginatedBudgetsResult(
        List<OrganizationalUnitBudgetDto> items,
        long totalElements,
        int totalPages,
        int page,
        int size) {}
