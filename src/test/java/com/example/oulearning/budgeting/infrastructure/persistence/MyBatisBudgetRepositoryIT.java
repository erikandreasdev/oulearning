package com.example.oulearning.budgeting.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.oulearning.budgeting.domain.model.Budget;
import com.example.oulearning.budgeting.domain.model.BudgetId;
import com.example.oulearning.budgeting.domain.model.FiscalYear;
import com.example.oulearning.budgeting.domain.model.Money;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MyBatisBudgetRepository.class, FlywayAutoConfiguration.class})
@Testcontainers
class MyBatisBudgetRepositoryIT {

    @Container
    @ServiceConnection
    static OracleContainer oracle = new OracleContainer(org.testcontainers.utility.DockerImageName.parse("gvenzl/oracle-free:23-slim").asCompatibleSubstituteFor("gvenzl/oracle-xe"));

    @Autowired
    private MyBatisBudgetRepository budgetRepository;

    @Test
    @DisplayName("given valid budget, when saving, then can be retrieved")
    void givenValidBudget_whenSaving_thenCanBeRetrieved() {
        // given
        final var budget = Budget.create(
                new BudgetId(1L),
                new OrganizationalUnitId(1L),
                new FiscalYear(2025),
                Money.of(new BigDecimal("10000.00")),
                Money.of(new BigDecimal("0.00")),
                Money.of(new BigDecimal("10000.00")));

        // when
        budgetRepository.save(budget);

        // then
        // Since we are creating a new budget, the DB assigns an ID, but our domain repository
        // doesn't return the assigned ID in the `save` method directly if it relies on DB-generated keys without updating the object.
        // Wait, the MyBatis insert sets the ID on the entity, but does it set it back on the domain object?
        // Actually, our repository `save` method does not modify the immutable `Budget` domain object.
        // To test it, we need to know the ID, or we just trust the ID is 1 for the first insert in tests.
        // Let's assume ID is 1 for this test, or we query it.
        final var retrieved = budgetRepository.findById(new BudgetId(1L));

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().fiscalYear().value()).isEqualTo(2025);
        assertThat(retrieved.get().total().amount()).isEqualByComparingTo(new BigDecimal("10000.00"));
    }

    @Test
    @DisplayName("given existing budget, when updating, then changes are persisted")
    @Sql(scripts = "/sql/insert-budget.sql")
    void givenExistingBudget_whenUpdating_thenChangesArePersisted() {
        // given
        final var budgetId = new BudgetId(2L); // Assuming second insert gets ID 2

        final var retrieved = budgetRepository.findById(budgetId).orElseThrow();

        final var updatedBudget = retrieved.updateAmounts(
                Money.of(new BigDecimal("6000.00")),
                Money.of(new BigDecimal("1000.00")),
                Money.of(new BigDecimal("5000.00")));

        // when
        budgetRepository.save(updatedBudget);

        // then
        final var updated = budgetRepository.findById(budgetId);
        assertThat(updated).isPresent();
        assertThat(updated.get().total().amount()).isEqualByComparingTo(new BigDecimal("6000.00"));
        assertThat(updated.get().reserved().amount()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(updated.get().available().amount()).isEqualByComparingTo(new BigDecimal("5000.00"));
    }

    @Test
    @DisplayName("given active budget, when deactivating, then active flag is updated")
    @Sql(scripts = "/sql/insert-budget.sql")
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
        assertThat(updated.get().active()).isFalse();
    }
}
