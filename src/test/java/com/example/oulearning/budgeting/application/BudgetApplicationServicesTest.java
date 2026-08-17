package com.example.oulearning.budgeting.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.budgeting.domain.budget.Budget;
import com.example.oulearning.budgeting.domain.budget.BudgetId;
import com.example.oulearning.budgeting.domain.budget.Money;
import com.example.oulearning.budgeting.domain.budget.exception.InsufficientBudgetException;
import com.example.oulearning.budgeting.domain.budget.repository.BudgetRepository;
import com.example.oulearning.organization.domain.unit.OuId;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BudgetApplicationServicesTest {

    private InMemoryBudgetRepository repository;

    private AllocateBudgetService allocateService;
    private GetBudgetService getService;
    private ReserveBudgetFundsService reserveService;
    private ReleaseBudgetFundsService releaseService;
    private ConsumeBudgetFundsService consumeService;
    private SpendDirectBudgetFundsService spendDirectService;
    private DistributeBudgetService distributeService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBudgetRepository();

        allocateService = new AllocateBudgetService(repository);
        getService = new GetBudgetService(repository);
        reserveService = new ReserveBudgetFundsService(repository);
        releaseService = new ReleaseBudgetFundsService(repository);
        consumeService = new ConsumeBudgetFundsService(repository);
        spendDirectService = new SpendDirectBudgetFundsService(repository);
        distributeService = new DistributeBudgetService(repository);
    }

    @Test
    @DisplayName("should allocate initial budget and retrieve it")
    void should_allocateAndRetrieve() {
        final var budgetId = UUID.randomUUID();
        final var ouId = UUID.randomUUID();
        final var command = new AllocateBudgetCommand(budgetId, ouId, BigDecimal.valueOf(15000.00), "EUR");

        final var createdId = allocateService.execute(command);
        assertThat(createdId).isEqualTo(budgetId);

        final var byId = getService.execute(GetBudgetQuery.byBudgetId(budgetId));
        assertThat(byId).isPresent();
        assertThat(byId.get().allocated()).isEqualTo(Money.euros(15000.00));

        final var byOu = getService.execute(GetBudgetQuery.byOuId(ouId));
        assertThat(byOu).isPresent();
        assertThat(byOu.get().id().value()).isEqualTo(budgetId);
    }

    @Test
    @DisplayName("should perform complete fund lifecycle: reserve -> release -> consume -> spend-direct")
    void should_manageFundLifecycle() {
        final var budgetId = UUID.randomUUID();
        final var ouId = UUID.randomUUID();
        allocateService.execute(new AllocateBudgetCommand(budgetId, ouId, BigDecimal.valueOf(10000.00), "EUR"));

        // Reserve 4,000
        final var reserved = reserveService.execute(new ReserveFundsCommand(budgetId, BigDecimal.valueOf(4000.00), "EUR"));
        assertThat(reserved.reserved()).isEqualTo(Money.euros(4000.00));
        assertThat(reserved.available()).isEqualTo(Money.euros(6000.00));

        // Release 1,000
        final var released = releaseService.execute(new ReleaseFundsCommand(budgetId, BigDecimal.valueOf(1000.00), "EUR"));
        assertThat(released.reserved()).isEqualTo(Money.euros(3000.00));
        assertThat(released.available()).isEqualTo(Money.euros(7000.00));

        // Consume 2,000
        final var consumed = consumeService.execute(new ConsumeFundsCommand(budgetId, BigDecimal.valueOf(2000.00), "EUR"));
        assertThat(consumed.reserved()).isEqualTo(Money.euros(1000.00));
        assertThat(consumed.spent()).isEqualTo(Money.euros(2000.00));
        assertThat(consumed.available()).isEqualTo(Money.euros(7000.00));

        // Spend Direct 3,000
        final var spent = spendDirectService.execute(new SpendDirectCommand(budgetId, BigDecimal.valueOf(3000.00), "EUR"));
        assertThat(spent.reserved()).isEqualTo(Money.euros(1000.00));
        assertThat(spent.spent()).isEqualTo(Money.euros(5000.00));
        assertThat(spent.available()).isEqualTo(Money.euros(4000.00));
    }

    @Test
    @DisplayName("should distribute budget equally among child OUs")
    void should_distributeBudgetEqually() {
        final var parentOuId = UUID.randomUUID();
        final var child1 = UUID.randomUUID();
        final var child2 = UUID.randomUUID();

        allocateService.execute(new AllocateBudgetCommand(
                UUID.randomUUID(), parentOuId, BigDecimal.valueOf(20000.00), "EUR"));

        final var result = distributeService.execute(new DistributeBudgetCommand(
                parentOuId, "EQUAL", List.of(child1, child2), Map.of(), "EUR"));

        assertThat(result.childBudgets()).hasSize(2);
        assertThat(result.childBudgets().get(0).allocated()).isEqualTo(Money.euros(10000.00));
        assertThat(result.childBudgets().get(1).allocated()).isEqualTo(Money.euros(10000.00));
    }

    @Test
    @DisplayName("should throw InsufficientBudgetException when direct spend exceeds available funds")
    void should_throw_whenSpendExceedsAvailable() {
        final var budgetId = UUID.randomUUID();
        final var ouId = UUID.randomUUID();
        allocateService.execute(new AllocateBudgetCommand(budgetId, ouId, BigDecimal.valueOf(1000.00), "EUR"));

        assertThatThrownBy(() -> spendDirectService.execute(
                        new SpendDirectCommand(budgetId, BigDecimal.valueOf(5000.00), "EUR")))
                .isInstanceOf(InsufficientBudgetException.class);
    }

    static class InMemoryBudgetRepository implements BudgetRepository {
        private final Map<BudgetId, Budget> store = new HashMap<>();

        @Override
        public Optional<Budget> findById(BudgetId id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public Optional<Budget> findByOuId(OuId ouId) {
            return store.values().stream().filter(b -> b.ouId().equals(ouId)).findFirst();
        }

        @Override
        public List<Budget> findAllByOuIds(java.util.Collection<OuId> ouIds) {
            if (ouIds == null || ouIds.isEmpty()) {
                return List.of();
            }
            return store.values().stream().filter(b -> ouIds.contains(b.ouId())).toList();
        }

        @Override
        public void save(Budget budget) {
            store.put(budget.id(), budget);
        }
    }
}
