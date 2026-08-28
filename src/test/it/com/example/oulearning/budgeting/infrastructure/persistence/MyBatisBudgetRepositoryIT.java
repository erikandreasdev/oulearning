package com.example.oulearning.budgeting.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.oulearning.AbstractOracleIntegrationTest;
import com.example.oulearning.budgeting.domain.model.Budget;
import com.example.oulearning.budgeting.domain.model.BudgetId;
import com.example.oulearning.budgeting.domain.model.BudgetingTestFactory;
import com.example.oulearning.budgeting.domain.model.FiscalYear;
import com.example.oulearning.budgeting.domain.model.Money;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MyBatisBudgetRepository.class, FlywayAutoConfiguration.class})
class MyBatisBudgetRepositoryIT extends AbstractOracleIntegrationTest {

    @Autowired
    private MyBatisBudgetRepository budgetRepository;

    @Test
    @DisplayName("given valid budget, when saving, then can be retrieved")
    @Sql(scripts = "/sql/cleanup-all.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/insert-employee.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/insert-ou.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void givenValidBudget_whenSaving_thenCanBeRetrieved() {
        // given
        final var budgetId = new BudgetId(1L);
        final var ouId = new OrganizationalUnitId(2L);
        final var randomFiscalYear = BudgetingTestFactory.randomFiscalYearValue();
        final var randomAmount = BudgetingTestFactory.randomBigDecimalAmount();

        final var budget = Budget.create(
                budgetId,
                ouId,
                new FiscalYear(randomFiscalYear),
                Money.of(randomAmount),
                Money.of(BigDecimal.ZERO),
                Money.of(randomAmount));

        // when
        budgetRepository.save(budget);

        // then
        final var retrieved = budgetRepository.findById(budgetId);

        assertThat(retrieved).isPresent();
        final var b = retrieved.orElseThrow();
        assertThat(b.fiscalYear().value()).isEqualTo(randomFiscalYear);
        assertThat(b.total().amount()).isEqualByComparingTo(randomAmount);
    }

    @Test
    @DisplayName("given existing budget, when updating, then changes are persisted")
    @Sql(scripts = {"/sql/cleanup-all.sql", "/sql/insert-budget.sql"})
    void givenExistingBudget_whenUpdating_thenChangesArePersisted() {
        // given
        final var budgetId = new BudgetId(2L);

        final var retrieved = budgetRepository.findById(budgetId).orElseThrow();

        final var totalAmount = BudgetingTestFactory.randomBigDecimalAmount();
        final var reservedAmount = BudgetingTestFactory.randomBigDecimalAmount();
        final var availableAmount = BudgetingTestFactory.randomBigDecimalAmount();

        final var updatedBudget = retrieved.updateAmounts(
                Money.of(totalAmount),
                Money.of(reservedAmount),
                Money.of(availableAmount));

        // when
        budgetRepository.save(updatedBudget);

        // then
        final var updated = budgetRepository.findById(budgetId);
        assertThat(updated).isPresent();
        final var b = updated.orElseThrow();
        assertThat(b.total().amount()).isEqualByComparingTo(totalAmount);
        assertThat(b.reserved().amount()).isEqualByComparingTo(reservedAmount);
        assertThat(b.available().amount()).isEqualByComparingTo(availableAmount);
    }

    @Test
    @DisplayName("given existing budget, when finding by organizational unit id, then returns matching budgets")
    @Sql(scripts = {"/sql/cleanup-all.sql", "/sql/insert-budget.sql"})
    void givenExistingBudget_whenFindingByOrganizationalUnitId_thenReturnsMatchingBudgets() {
        // given
        final var ouId = new OrganizationalUnitId(2L);

        // when
        final var budgets = budgetRepository.findByOrganizationalUnitId(ouId);

        // then
        assertThat(budgets).hasSize(1);
        assertThat(budgets.get(0).id()).isEqualTo(new BudgetId(2L));
        assertThat(budgets.get(0).organizationalUnitId()).isEqualTo(ouId);
    }

    @Test
    @DisplayName("given active budget, when deactivating, then active flag is updated")
    @Sql(scripts = {"/sql/cleanup-all.sql", "/sql/insert-budget.sql"})
    void givenActiveBudget_whenDeactivating_thenActiveFlagIsUpdated() {
        // given
        final var budgetId = new BudgetId(2L);
        final var retrieved = budgetRepository.findById(budgetId).orElseThrow();
        final var deactivatedBudget = retrieved.deactivate();

        // when
        budgetRepository.save(deactivatedBudget);

        // then
        final var updated = budgetRepository.findById(budgetId);
        assertThat(updated).isPresent();
        final var b = updated.orElseThrow();
        assertThat(b.active()).isFalse();
    }
}

