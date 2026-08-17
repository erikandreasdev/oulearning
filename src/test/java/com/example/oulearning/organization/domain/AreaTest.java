package com.example.oulearning.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.shared.domain.Money;
import java.util.Set;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InstancioExtension.class)
class AreaTest {

    @Nested
    @DisplayName("Creation and Budget Validation")
    class CreationAndBudgetValidation {

        @Test
        @DisplayName("should create Area with no subareas (0 subareas)")
        void should_createArea_with_noSubareas() {
            // given
            final var id = DomainGenerators.randomOuId();
            final var name = DomainGenerators.randomOuName();
            final var owners = Set.of(DomainGenerators.randomCorporateKey());
            final var parentIds = Set.of(DomainGenerators.randomOuId());
            final var budget = Money.euros(10000.00);

            // when
            final var area = Area.of(id, name, owners, parentIds, budget, Set.of());

            // then
            assertThat(area.id()).isEqualTo(id);
            assertThat(area.name()).isEqualTo(name);
            assertThat(area.owners()).isEqualTo(owners);
            assertThat(area.parentIds()).isEqualTo(parentIds);
            assertThat(area.budget()).isEqualTo(budget);
            assertThat(area.subareas()).isEmpty();
            assertThat(area.type()).isEqualTo(OuType.AREA);
        }

        @Test
        @DisplayName("should create Area when non-equally distributed subareas budgets match the Area budget")
        void should_createArea_when_subareasBudgetsMatch_nonEquallyDistributed() {
            // given
            final var areaId = DomainGenerators.randomOuId();
            final var subarea1 = Subarea.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("Frontend"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(areaId),
                    Money.euros(3500.00));
            final var subarea2 = Subarea.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("Backend"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(areaId),
                    Money.euros(6500.00));
            final var totalBudget = Money.euros(10000.00);

            // when
            final var area = Area.of(
                    areaId,
                    OuName.of("Software Engineering"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    totalBudget,
                    Set.of(subarea1, subarea2));

            // then
            assertThat(area.subareas()).containsExactlyInAnyOrder(subarea1, subarea2);
            assertThat(area.budget()).isEqualTo(totalBudget);
        }

        @Test
        @DisplayName("should throw AreaBudgetMismatchException when subareas budget sum does not match Area budget")
        void should_throwException_when_subareasBudgetSumDoesNotMatch() {
            // given
            final var areaId = DomainGenerators.randomOuId();
            final var subarea1 = Subarea.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("Frontend"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(areaId),
                    Money.euros(3000.00));
            final var subarea2 = Subarea.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("Backend"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(areaId),
                    Money.euros(4000.00));
            final var declaredBudget = Money.euros(10000.00); // sum is 7000 != 10000

            // when / then
            assertThatThrownBy(() -> Area.of(
                            areaId,
                            OuName.of("Software Engineering"),
                            Set.of(DomainGenerators.randomCorporateKey()),
                            Set.of(),
                            declaredBudget,
                            Set.of(subarea1, subarea2)))
                    .isInstanceOf(AreaBudgetMismatchException.class)
                    .hasMessageContaining("does not match the sum of its subareas' budgets");
        }

        @Test
        @DisplayName("should throw InvalidOuException when ID is null")
        void should_throwException_when_idIsNull() {
            assertThatThrownBy(() -> Area.of(
                            null,
                            DomainGenerators.randomOuName(),
                            Set.of(DomainGenerators.randomCorporateKey()),
                            Set.of(),
                            Money.euros(1000.00),
                            Set.of()))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("ID cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidOuException when name is null")
        void should_throwException_when_nameIsNull() {
            assertThatThrownBy(() -> Area.of(
                            DomainGenerators.randomOuId(),
                            null,
                            Set.of(DomainGenerators.randomCorporateKey()),
                            Set.of(),
                            Money.euros(1000.00),
                            Set.of()))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("name cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidOuException when budget is null")
        void should_throwException_when_budgetIsNull() {
            assertThatThrownBy(() -> Area.of(
                            DomainGenerators.randomOuId(),
                            DomainGenerators.randomOuName(),
                            Set.of(DomainGenerators.randomCorporateKey()),
                            Set.of(),
                            null,
                            Set.of()))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("budget cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidOuException when subareas set is null")
        void should_throwException_when_subareasIsNull() {
            assertThatThrownBy(() -> Area.of(
                            DomainGenerators.randomOuId(),
                            DomainGenerators.randomOuName(),
                            Set.of(DomainGenerators.randomCorporateKey()),
                            Set.of(),
                            Money.euros(1000.00),
                            null))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("subareas cannot be null");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when all fields match")
        void should_beEqual_when_allFieldsMatch() {
            final var id = DomainGenerators.randomOuId();
            final var name = DomainGenerators.randomOuName();
            final var owners = Set.of(DomainGenerators.randomCorporateKey());
            final var parentIds = Set.of(DomainGenerators.randomOuId());
            final var budget = Money.euros(5000.00);

            final var area1 = Area.of(id, name, owners, parentIds, budget, Set.of());
            final var area2 = Area.of(id, name, owners, parentIds, budget, Set.of());

            assertThat(area1).isEqualTo(area2);
            assertThat(area1.hashCode()).isEqualTo(area2.hashCode());
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var area = DomainGenerators.randomArea();
            assertThat(area.getClass().isRecord()).isTrue();
        }
    }
}
