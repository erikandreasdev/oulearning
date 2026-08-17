package com.example.oulearning.budgeting.infrastructure.persistence;

import com.example.oulearning.budgeting.domain.budget.Budget;
import com.example.oulearning.budgeting.domain.budget.BudgetId;
import com.example.oulearning.budgeting.domain.budget.repository.BudgetRepository;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence adapter implementing {@link BudgetRepository} using MyBatis and {@link BudgetEntityMapper}.
 */
@Repository
public class BudgetPersistenceAdapter implements BudgetRepository {

    private final BudgetMyBatisMapper mapper;
    private final BudgetEntityMapper entityMapper;

    public BudgetPersistenceAdapter(
            BudgetMyBatisMapper mapper,
            BudgetEntityMapper entityMapper) {
        this.mapper = Objects.requireNonNull(mapper, "BudgetMyBatisMapper cannot be null");
        this.entityMapper = Objects.requireNonNull(entityMapper, "BudgetEntityMapper cannot be null");
    }

    @Override
    @Transactional
    public void save(Budget budget) {
        Objects.requireNonNull(budget, "Budget cannot be null");

        final var budgetIdStr = budget.id().toString();
        final var existing = mapper.findBudgetById(budgetIdStr);

        final var entity = entityMapper.toEntity(budget, existing != null ? existing.version() : 0L);

        if (existing == null) {
            mapper.insertBudget(entity);
        } else {
            final var updatedRows = mapper.updateBudget(entity);
            if (updatedRows == 0) {
                throw new OptimisticLockingFailureException(
                        "Failed to update budget '%s': version conflict or record deleted".formatted(budgetIdStr));
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Budget> findById(BudgetId id) {
        Objects.requireNonNull(id, "BudgetId cannot be null");

        final var entity = mapper.findBudgetById(id.toString());
        if (entity == null) {
            return Optional.empty();
        }

        return Optional.of(entityMapper.toDomain(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Budget> findByOuId(OuId ouId) {
        Objects.requireNonNull(ouId, "OuId cannot be null");

        final var entity = mapper.findBudgetByOuId(ouId.toString());
        if (entity == null) {
            return Optional.empty();
        }

        return Optional.of(entityMapper.toDomain(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Budget> findByOuIdAndFiscalYear(OuId ouId, FiscalYear fiscalYear) {
        Objects.requireNonNull(ouId, "OuId cannot be null");
        Objects.requireNonNull(fiscalYear, "FiscalYear cannot be null");

        final var entity = mapper.findBudgetByOuIdAndFiscalYear(ouId.toString(), fiscalYear.value());
        if (entity == null) {
            return Optional.empty();
        }

        return Optional.of(entityMapper.toDomain(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Budget> findAllByOuIds(Collection<OuId> ouIds) {
        if (ouIds == null || ouIds.isEmpty()) {
            return List.of();
        }

        final var idStrings = ouIds.stream().map(OuId::toString).toList();
        final var entities = mapper.findAllBudgetsByOuIds(idStrings);

        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        final var result = new ArrayList<Budget>(entities.size());
        for (final var entity : entities) {
            result.add(entityMapper.toDomain(entity));
        }

        return List.copyOf(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Budget> findAllByOuIdsAndFiscalYear(Collection<OuId> ouIds, FiscalYear fiscalYear) {
        if (ouIds == null || ouIds.isEmpty()) {
            return List.of();
        }
        Objects.requireNonNull(fiscalYear, "FiscalYear cannot be null");

        final var idStrings = ouIds.stream().map(OuId::toString).toList();
        final var entities = mapper.findAllBudgetsByOuIdsAndFiscalYear(idStrings, fiscalYear.value());

        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        final var result = new ArrayList<Budget>(entities.size());
        for (final var entity : entities) {
            result.add(entityMapper.toDomain(entity));
        }

        return List.copyOf(result);
    }
}
