package com.example.oulearning.budgeting.domain.distribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.budgeting.domain.budget.Budget;
import com.example.oulearning.budgeting.domain.budget.BudgetId;
import com.example.oulearning.budgeting.domain.budget.Money;
import com.example.oulearning.budgeting.domain.distribution.exception.BudgetDistributionException;
import com.example.oulearning.training.domain.request.vo.identity.OuId;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InstancioExtension.class)
class BudgetDistributionServiceTest {

    private final BudgetDistributionService service = new BudgetDistributionService();

    private OuId randomOuId() {
        return OuId.of(UUID.randomUUID());
    }

    private BudgetId randomBudgetId() {
        return BudgetId.of(UUID.randomUUID());
    }

    @Nested
    @DisplayName("Exclusive Allocation Strategy")
    class ExclusiveAllocationStrategy {

        @Test
        @DisplayName("should not distribute any budget to child OUs")
        void should_notDistributeToChildren() {
            final var parentOuId = randomOuId();
            final var parentBudget = Budget.of(randomBudgetId(), parentOuId, Money.euros(10000.00));
            final var child1 = randomOuId();
            final var child2 = randomOuId();

            final var childBudgets = service.distribute(
                    parentBudget,
                    Set.of(child1, child2),
                    new BudgetDistributionStrategy.ExclusiveAllocation(),
                    BudgetDistributionServiceTest.this::randomBudgetId);

            assertThat(childBudgets).isEmpty();
        }
    }

    @Nested
    @DisplayName("Equal Distribution Strategy")
    class EqualDistributionStrategy {

        @Test
        @DisplayName("should divide parent budget equally among child OUs")
        void should_divideBudgetEqually() {
            final var parentBudget = Budget.of(randomBudgetId(), randomOuId(), Money.euros(10000.00));
            final var child1 = randomOuId();
            final var child2 = randomOuId();

            final var childBudgets = service.distribute(
                    parentBudget,
                    Set.of(child1, child2),
                    new BudgetDistributionStrategy.EqualDistribution(),
                    BudgetDistributionServiceTest.this::randomBudgetId);

            assertThat(childBudgets).hasSize(2);
            assertThat(childBudgets.get(0).allocated()).isEqualTo(Money.euros(5000.00));
            assertThat(childBudgets.get(1).allocated()).isEqualTo(Money.euros(5000.00));
        }

        @Test
        @DisplayName("should handle cent remainder precision accurately without losing funds")
        void should_handleCentRemainder_accurately() {
            final var parentBudget = Budget.of(randomBudgetId(), randomOuId(), Money.euros(100.00));
            final var child1 = randomOuId();
            final var child2 = randomOuId();
            final var child3 = randomOuId();

            final var childBudgets = service.distribute(
                    parentBudget,
                    Set.of(child1, child2, child3),
                    new BudgetDistributionStrategy.EqualDistribution(),
                    BudgetDistributionServiceTest.this::randomBudgetId);

            assertThat(childBudgets).hasSize(3);
            final var totalSum = childBudgets.stream()
                    .map(Budget::allocated)
                    .reduce(Money.zero(), Money::plus);

            assertThat(totalSum).isEqualTo(Money.euros(100.00));
        }

        @Test
        @DisplayName("should return empty list when child OUs set is empty")
        void should_returnEmpty_when_noChildren() {
            final var parentBudget = Budget.of(randomBudgetId(), randomOuId(), Money.euros(5000.00));

            final var childBudgets = service.distribute(
                    parentBudget,
                    Set.of(),
                    new BudgetDistributionStrategy.EqualDistribution(),
                    BudgetDistributionServiceTest.this::randomBudgetId);

            assertThat(childBudgets).isEmpty();
        }
    }

    @Nested
    @DisplayName("Explicit Distribution Strategy")
    class ExplicitDistributionStrategy {

        @Test
        @DisplayName("should allocate custom explicit amounts to child OUs")
        void should_allocateExplicitAmounts() {
            final var parentBudget = Budget.of(randomBudgetId(), randomOuId(), Money.euros(10000.00));
            final var child1 = randomOuId();
            final var child2 = randomOuId();

            final var explicit = BudgetDistributionStrategy.ExplicitDistribution.of(Map.of(
                    child1, Money.euros(3500.00),
                    child2, Money.euros(6500.00)));

            final var childBudgets = service.distribute(
                    parentBudget,
                    Set.of(child1, child2),
                    explicit,
                    BudgetDistributionServiceTest.this::randomBudgetId);

            assertThat(childBudgets).hasSize(2);
            final var b1 = childBudgets.stream()
                    .filter(b -> b.ouId().equals(child1))
                    .findFirst()
                    .orElseThrow();
            final var b2 = childBudgets.stream()
                    .filter(b -> b.ouId().equals(child2))
                    .findFirst()
                    .orElseThrow();

            assertThat(b1.allocated()).isEqualTo(Money.euros(3500.00));
            assertThat(b2.allocated()).isEqualTo(Money.euros(6500.00));
        }

        @Test
        @DisplayName("should allocate zero budget to children not explicitly assigned")
        void should_allocateZero_toUnassignedChildren() {
            final var parentBudget = Budget.of(randomBudgetId(), randomOuId(), Money.euros(10000.00));
            final var child1 = randomOuId();
            final var child2 = randomOuId();

            final var explicit = BudgetDistributionStrategy.ExplicitDistribution.of(Map.of(
                    child1, Money.euros(7000.00)));

            final var childBudgets = service.distribute(
                    parentBudget,
                    Set.of(child1, child2),
                    explicit,
                    BudgetDistributionServiceTest.this::randomBudgetId);

            final var b2 = childBudgets.stream()
                    .filter(b -> b.ouId().equals(child2))
                    .findFirst()
                    .orElseThrow();

            assertThat(b2.allocated()).isEqualTo(Money.zero());
        }

        @Test
        @DisplayName("should throw BudgetDistributionException when explicit total exceeds parent budget")
        void should_throwException_when_explicitTotalExceedsParentBudget() {
            final var parentBudget = Budget.of(randomBudgetId(), randomOuId(), Money.euros(5000.00));
            final var child1 = randomOuId();

            final var explicit = BudgetDistributionStrategy.ExplicitDistribution.of(Map.of(
                    child1, Money.euros(6000.00)));

            assertThatThrownBy(() -> service.distribute(
                            parentBudget,
                            Set.of(child1),
                            explicit,
                            BudgetDistributionServiceTest.this::randomBudgetId))
                    .isInstanceOf(BudgetDistributionException.class)
                    .hasMessageContaining("exceed parent allocated budget");
        }

        @Test
        @DisplayName("should throw BudgetDistributionException when explicit allocation targets unknown OU")
        void should_throwException_when_explicitTargetsUnknownOu() {
            final var parentBudget = Budget.of(randomBudgetId(), randomOuId(), Money.euros(5000.00));
            final var child1 = randomOuId();
            final var unknownOu = randomOuId();

            final var explicit = BudgetDistributionStrategy.ExplicitDistribution.of(Map.of(
                    unknownOu, Money.euros(1000.00)));

            assertThatThrownBy(() -> service.distribute(
                            parentBudget,
                            Set.of(child1),
                            explicit,
                            BudgetDistributionServiceTest.this::randomBudgetId))
                    .isInstanceOf(BudgetDistributionException.class)
                    .hasMessageContaining("is not a child of parent OU");
        }
    }
}
