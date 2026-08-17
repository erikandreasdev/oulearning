package com.example.oulearning.budgeting.domain.budget;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.budgeting.domain.budget.exception.BudgetFiscalYearExpiredException;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BudgetFiscalYearTest {

    @Test
    @DisplayName("should allow edits for current fiscal year and previous fiscal year")
    void should_allowEdits_whenCurrentOrPreviousFiscalYear() {
        final var budget2026 = Budget.of(
                BudgetId.of(UUID.randomUUID()),
                OuId.of(UUID.randomUUID()),
                FiscalYear.of(2026),
                Money.euros(10000));

        final var budget2025 = Budget.of(
                BudgetId.of(UUID.randomUUID()),
                OuId.of(UUID.randomUUID()),
                FiscalYear.of(2025),
                Money.euros(10000));

        final var current2026 = FiscalYear.of(2026);

        assertThat(budget2026.isEditable(current2026)).isTrue();
        assertThat(budget2025.isEditable(current2026)).isTrue();

        final var reserved2026 = budget2026.reserve(Money.euros(1000), current2026);
        assertThat(reserved2026.reserved()).isEqualTo(Money.euros(1000));

        final var spent2025 = budget2025.spendDirect(Money.euros(2000), current2026);
        assertThat(spent2025.spent()).isEqualTo(Money.euros(2000));
    }

    @Test
    @DisplayName("should throw BudgetFiscalYearExpiredException when editing expired fiscal year")
    void should_throwException_whenEditingExpiredBudget() {
        final var budget2024 = Budget.of(
                BudgetId.of(UUID.randomUUID()),
                OuId.of(UUID.randomUUID()),
                FiscalYear.of(2024),
                Money.euros(10000));

        final var current2026 = FiscalYear.of(2026);

        assertThat(budget2024.isEditable(current2026)).isFalse();

        assertThatThrownBy(() -> budget2024.reserve(Money.euros(1000), current2026))
                .isInstanceOf(BudgetFiscalYearExpiredException.class)
                .hasMessageContaining("expired");

        assertThatThrownBy(() -> budget2024.spendDirect(Money.euros(1000), current2026))
                .isInstanceOf(BudgetFiscalYearExpiredException.class);
    }
}
