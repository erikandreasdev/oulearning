package com.example.oulearning.budgeting.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.oulearning.budgeting.domain.budget.Budget;
import com.example.oulearning.budgeting.domain.budget.BudgetId;
import com.example.oulearning.budgeting.domain.budget.Money;
import com.example.oulearning.organization.domain.unit.OuId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
class BudgetPersistenceAdapterIT {

    @Container
    static final OracleContainer oracle = new OracleContainer("gvenzl/oracle-free:slim-faststart")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", oracle::getJdbcUrl);
        registry.add("spring.datasource.username", oracle::getUsername);
        registry.add("spring.datasource.password", oracle::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private BudgetPersistenceAdapter adapter;

    @Nested
    @DisplayName("Budget Persistence Integration")
    class BudgetPersistenceIntegration {

        @Test
        @DisplayName("should persist, update and retrieve budget by ID and OU ID")
        void should_persistUpdateAndRetrieveBudget() {
            final var budgetId = BudgetId.of(UUID.randomUUID());
            final var ouId = OuId.of(UUID.randomUUID());
            final var initialBudget = Budget.of(budgetId, ouId, Money.euros(10000.00));

            // Save new budget
            adapter.save(initialBudget);

            // Find by ID
            final var foundById = adapter.findById(budgetId);
            assertThat(foundById).isPresent();
            assertThat(foundById.get().allocated()).isEqualTo(Money.euros(10000.00));
            assertThat(foundById.get().available()).isEqualTo(Money.euros(10000.00));

            // Reserve funds & update
            final var updated = foundById.get().reserve(Money.euros(3000.00));
            adapter.save(updated);

            // Find by OU ID
            final var foundByOu = adapter.findByOuId(ouId);
            assertThat(foundByOu).isPresent();
            assertThat(foundByOu.get().reserved()).isEqualTo(Money.euros(3000.00));
            assertThat(foundByOu.get().available()).isEqualTo(Money.euros(7000.00));
        }

        @Test
        @DisplayName("should batch query budgets for a list of OU IDs")
        void should_batchQueryBudgets_byOuIds() {
            final var ouId1 = OuId.of(UUID.randomUUID());
            final var ouId2 = OuId.of(UUID.randomUUID());

            final var b1 = Budget.of(BudgetId.of(UUID.randomUUID()), ouId1, Money.euros(5000.00));
            final var b2 = Budget.of(BudgetId.of(UUID.randomUUID()), ouId2, Money.euros(8000.00));

            adapter.save(b1);
            adapter.save(b2);

            final var list = adapter.findAllByOuIds(List.of(ouId1, ouId2));

            assertThat(list).hasSize(2);
            assertThat(list).extracting(Budget::ouId).containsExactlyInAnyOrder(ouId1, ouId2);
        }
    }
}
