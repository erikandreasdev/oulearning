package com.example.oulearning.budgeting.infrastructure.persistence;

import com.example.oulearning.budgeting.domain.model.Budget;
import com.example.oulearning.budgeting.domain.model.BudgetId;
import com.example.oulearning.budgeting.domain.model.FiscalYear;
import com.example.oulearning.budgeting.domain.model.Money;
import com.example.oulearning.budgeting.domain.repository.BudgetRepository;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
class MyBatisBudgetRepository implements BudgetRepository {

    private final BudgetMapper budgetMapper;

    MyBatisBudgetRepository(final BudgetMapper budgetMapper) {
        this.budgetMapper = budgetMapper;
    }

    @Override
    public Optional<Budget> findById(final BudgetId id) {
        return budgetMapper.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<Budget> findByOrganizationalUnitId(final OrganizationalUnitId organizationalUnitId) {
        return budgetMapper.findByOrganizationalUnitId(organizationalUnitId.value()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void save(final Budget budget) {
        final var entity = toEntity(budget);
        if (budgetMapper.findById(budget.id().value()).isEmpty()) {
            budgetMapper.insert(entity);
        } else {
            budgetMapper.update(entity);
        }
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private Budget toDomain(final BudgetEntity entity) {
        return Budget.reconstitute(
                new BudgetId(entity.getId()),
                new OrganizationalUnitId(entity.getOrganizationalUnitId()),
                new FiscalYear(entity.getFiscalYear()),
                Money.of(entity.getTotalAmount()),
                Money.of(entity.getReservedAmount()),
                Money.of(entity.getAvailableAmount()),
                entity.getActive());
    }

    private BudgetEntity toEntity(final Budget budget) {
        final var entity = new BudgetEntity();
        if (budget.id() != null) {
            entity.setId(budget.id().value());
        }
        entity.setOrganizationalUnitId(budget.organizationalUnitId().value());
        entity.setFiscalYear(budget.fiscalYear().value());
        entity.setTotalAmount(budget.total().amount());
        entity.setReservedAmount(budget.reserved().amount());
        entity.setAvailableAmount(budget.available().amount());
        entity.setActive(budget.active());
        return entity;
    }
}
