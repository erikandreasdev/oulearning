package com.example.oulearning.budgeting.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.budgeting.domain.budget.Budget;
import com.example.oulearning.budgeting.domain.budget.BudgetId;
import com.example.oulearning.budgeting.domain.budget.Money;
import com.example.oulearning.training.domain.request.vo.identity.OuId;
import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BudgetEntityMapperTest {

    private final BudgetEntityMapper mapper = new BudgetEntityMapper();

    @Nested
    @DisplayName("Domain to Entity Mapping")
    class DomainToEntityMapping {

        @Test
        @DisplayName("should map domain Budget to BudgetEntity")
        void should_mapDomainToEntity() {
            final var budgetId = BudgetId.of(UUID.randomUUID());
            final var ouId = OuId.of(UUID.randomUUID());
            final var domain = Budget.of(budgetId, ouId, FiscalYear.of(2026), Money.euros(10000.00))
                    .reserve(Money.euros(2000.00));

            final var entity = mapper.toEntity(domain, 5L);

            assertThat(entity.id()).isEqualTo(budgetId.toString());
            assertThat(entity.ouId()).isEqualTo(ouId.toString());
            assertThat(entity.fiscalYear()).isEqualTo(2026);
            assertThat(entity.allocatedAmount()).isEqualByComparingTo(new BigDecimal("10000.00"));
            assertThat(entity.allocatedCurrency()).isEqualTo("EUR");
            assertThat(entity.reservedAmount()).isEqualByComparingTo(new BigDecimal("2000.00"));
            assertThat(entity.reservedCurrency()).isEqualTo("EUR");
            assertThat(entity.spentAmount()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(entity.spentCurrency()).isEqualTo("EUR");
            assertThat(entity.version()).isEqualTo(5L);
        }

        @Test
        @DisplayName("should default version to 0L when null is passed")
        void should_defaultVersionToZero_when_nullVersion() {
            final var domain = Budget.of(
                    BudgetId.of(UUID.randomUUID()),
                    OuId.of(UUID.randomUUID()),
                    FiscalYear.of(2026),
                    Money.euros(5000.00));

            final var entity = mapper.toEntity(domain, null);

            assertThat(entity.version()).isEqualTo(0L);
            assertThat(entity.fiscalYear()).isEqualTo(2026);
        }

        @Test
        @DisplayName("should throw NullPointerException when domain model is null")
        void should_throwException_when_domainIsNull() {
            assertThatThrownBy(() -> mapper.toEntity(null, 0L))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Entity to Domain Mapping")
    class EntityToDomainMapping {

        @Test
        @DisplayName("should map BudgetEntity to domain Budget")
        void should_mapEntityToDomain() {
            final var budgetId = UUID.randomUUID();
            final var ouId = UUID.randomUUID();

            final var entity = new BudgetEntity(
                    budgetId.toString(),
                    ouId.toString(),
                    2026,
                    new BigDecimal("12000.50"), "EUR",
                    new BigDecimal("2000.00"), "EUR",
                    new BigDecimal("1500.25"), "EUR",
                    2L);

            final var domain = mapper.toDomain(entity);

            assertThat(domain.id().value()).isEqualTo(budgetId);
            assertThat(domain.ouId().value()).isEqualTo(ouId);
            assertThat(domain.fiscalYear().value()).isEqualTo(2026);
            assertThat(domain.allocated()).isEqualTo(Money.euros(12000.50));
            assertThat(domain.reserved()).isEqualTo(Money.euros(2000.00));
            assertThat(domain.spent()).isEqualTo(Money.euros(1500.25));
            assertThat(domain.available()).isEqualTo(Money.euros(8500.25));
        }

        @Test
        @DisplayName("should throw NullPointerException when entity is null")
        void should_throwException_when_entityIsNull() {
            assertThatThrownBy(() -> mapper.toDomain(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
