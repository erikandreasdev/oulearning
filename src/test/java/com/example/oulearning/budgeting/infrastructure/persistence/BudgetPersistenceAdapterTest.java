package com.example.oulearning.budgeting.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.budgeting.domain.budget.Budget;
import com.example.oulearning.budgeting.domain.budget.BudgetId;
import com.example.oulearning.budgeting.domain.budget.Money;
import com.example.oulearning.organization.domain.unit.OuId;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;

class BudgetPersistenceAdapterTest {

    private BudgetMyBatisMapper mapper;
    private BudgetEntityMapper entityMapper;
    private BudgetPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        mapper = mock(BudgetMyBatisMapper.class);
        entityMapper = new BudgetEntityMapper();
        adapter = new BudgetPersistenceAdapter(mapper, entityMapper);
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperations {

        @Test
        @DisplayName("should find budget by budget ID")
        void should_findBudgetById() {
            final var budgetId = BudgetId.of(UUID.randomUUID());
            final var ouId = OuId.of(UUID.randomUUID());

            final var entity = new BudgetEntity(
                    budgetId.toString(),
                    ouId.toString(),
                    new BigDecimal("10000.00"), "EUR",
                    new BigDecimal("2000.00"), "EUR",
                    new BigDecimal("1000.00"), "EUR",
                    0L);

            when(mapper.findBudgetById(budgetId.toString())).thenReturn(entity);

            final var result = adapter.findById(budgetId);

            assertThat(result).isPresent();
            final var budget = result.get();
            assertThat(budget.id()).isEqualTo(budgetId);
            assertThat(budget.ouId()).isEqualTo(ouId);
            assertThat(budget.allocated()).isEqualTo(Money.euros(10000.00));
            assertThat(budget.reserved()).isEqualTo(Money.euros(2000.00));
            assertThat(budget.spent()).isEqualTo(Money.euros(1000.00));
            assertThat(budget.available()).isEqualTo(Money.euros(7000.00));
        }

        @Test
        @DisplayName("should find budget by OU ID")
        void should_findBudgetByOuId() {
            final var budgetId = BudgetId.of(UUID.randomUUID());
            final var ouId = OuId.of(UUID.randomUUID());

            final var entity = new BudgetEntity(
                    budgetId.toString(),
                    ouId.toString(),
                    new BigDecimal("5000.00"), "EUR",
                    new BigDecimal("0.00"), "EUR",
                    new BigDecimal("0.00"), "EUR",
                    0L);

            when(mapper.findBudgetByOuId(ouId.toString())).thenReturn(entity);

            final var result = adapter.findByOuId(ouId);

            assertThat(result).isPresent();
            assertThat(result.get().ouId()).isEqualTo(ouId);
        }

        @Test
        @DisplayName("should batch query budgets for a collection of OU IDs")
        void should_batchQueryBudgets_byOuIds() {
            final var ouId1 = OuId.of(UUID.randomUUID());
            final var ouId2 = OuId.of(UUID.randomUUID());

            final var e1 = new BudgetEntity(
                    UUID.randomUUID().toString(), ouId1.toString(),
                    new BigDecimal("3000.00"), "EUR",
                    new BigDecimal("0.00"), "EUR",
                    new BigDecimal("0.00"), "EUR", 0L);

            final var e2 = new BudgetEntity(
                    UUID.randomUUID().toString(), ouId2.toString(),
                    new BigDecimal("4000.00"), "EUR",
                    new BigDecimal("0.00"), "EUR",
                    new BigDecimal("0.00"), "EUR", 0L);

            when(mapper.findAllBudgetsByOuIds(List.of(ouId1.toString(), ouId2.toString())))
                    .thenReturn(List.of(e1, e2));

            final var list = adapter.findAllByOuIds(List.of(ouId1, ouId2));

            assertThat(list).hasSize(2);
        }

        @Test
        @DisplayName("should return empty Optional when budget is not found")
        void should_returnEmpty_when_budgetNotFound() {
            when(mapper.findBudgetById(any())).thenReturn(null);

            final var result = adapter.findById(BudgetId.of(UUID.randomUUID()));

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Save Operations & Optimistic Locking")
    class SaveOperationsAndOptimisticLocking {

        @Test
        @DisplayName("should insert new budget when it does not exist")
        void should_insertBudget_when_doesNotExist() {
            final var budgetId = BudgetId.of(UUID.randomUUID());
            final var ouId = OuId.of(UUID.randomUUID());
            final var budget = Budget.of(budgetId, ouId, Money.euros(15000.00));

            when(mapper.findBudgetById(budgetId.toString())).thenReturn(null);

            adapter.save(budget);

            verify(mapper).insertBudget(any(BudgetEntity.class));
        }

        @Test
        @DisplayName("should update existing budget when entity exists")
        void should_updateBudget_when_exists() {
            final var budgetId = BudgetId.of(UUID.randomUUID());
            final var ouId = OuId.of(UUID.randomUUID());
            final var budget = Budget.of(budgetId, ouId, Money.euros(15000.00));

            final var existing = new BudgetEntity(
                    budgetId.toString(), ouId.toString(),
                    new BigDecimal("10000.00"), "EUR",
                    BigDecimal.ZERO, "EUR",
                    BigDecimal.ZERO, "EUR", 1L);

            when(mapper.findBudgetById(budgetId.toString())).thenReturn(existing);
            when(mapper.updateBudget(any(BudgetEntity.class))).thenReturn(1);

            adapter.save(budget);

            verify(mapper).updateBudget(any(BudgetEntity.class));
        }

        @Test
        @DisplayName("should throw OptimisticLockingFailureException when update affected 0 rows")
        void should_throwException_when_updateFailsDueToVersionConflict() {
            final var budgetId = BudgetId.of(UUID.randomUUID());
            final var ouId = OuId.of(UUID.randomUUID());
            final var budget = Budget.of(budgetId, ouId, Money.euros(15000.00));

            final var existing = new BudgetEntity(
                    budgetId.toString(), ouId.toString(),
                    new BigDecimal("10000.00"), "EUR",
                    BigDecimal.ZERO, "EUR",
                    BigDecimal.ZERO, "EUR", 1L);

            when(mapper.findBudgetById(budgetId.toString())).thenReturn(existing);
            when(mapper.updateBudget(any(BudgetEntity.class))).thenReturn(0);

            assertThatThrownBy(() -> adapter.save(budget))
                    .isInstanceOf(OptimisticLockingFailureException.class)
                    .hasMessageContaining("version conflict");
        }
    }
}
