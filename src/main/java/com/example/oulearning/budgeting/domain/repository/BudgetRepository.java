package com.example.oulearning.budgeting.domain.repository;

import com.example.oulearning.budgeting.domain.model.Budget;
import com.example.oulearning.budgeting.domain.model.BudgetId;

import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository {
    Optional<Budget> findById(BudgetId id);

    List<Budget> findByOrganizationalUnitId(OrganizationalUnitId organizationalUnitId);

    void save(Budget budget);
}
