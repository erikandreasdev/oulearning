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
        @DisplayName("should create Area with no children (0 children)")
        void should_createArea_with_noChildren() {
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
            assertThat(area.childIds()).isEmpty();
            assertThat(area.loadedChildren()).isEmpty();
            assertThat(area.isSubtreeLoaded()).isTrue();
            assertThat(area.type()).isEqualTo(OuType.AREA);
            assertThat(area.totalSubtreeBudget()).isEqualTo(budget);
        }

        @Test
        @DisplayName("should create Area with child IDs when subtree is not loaded")
        void should_createArea_with_childIds_subtreeNotLoaded() {
            // given
            final var id = DomainGenerators.randomOuId();
            final var subId1 = DomainGenerators.randomOuId();
            final var subId2 = DomainGenerators.randomOuId();
            final var budget = Money.euros(10000.00);

            // when
            final var area = Area.of(
                    id,
                    DomainGenerators.randomOuName(),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    budget,
                    Set.of(subId1, subId2));

            // then
            assertThat(area.childIds()).containsExactlyInAnyOrder(subId1, subId2);
            assertThat(area.loadedChildren()).isEmpty();
            assertThat(area.isSubtreeLoaded()).isFalse();
            assertThat(area.totalSubtreeBudget()).isEqualTo(budget);
        }

        @Test
        @DisplayName("should create Area with N-level child Areas and Subareas when budgets match")
        void should_createArea_with_nLevelChildren() {
            // given (Level 3 subareas)
            final var subarea1 = Subarea.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("Frontend Subarea"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(2000.00));
            final var subarea2 = Subarea.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("Backend Subarea"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(3000.00));

            // Level 2 child Area
            final var childArea = Area.withChildren(
                    DomainGenerators.randomOuId(),
                    OuName.of("Engineering Area"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(5000.00),
                    Set.of(subarea1, subarea2));

            // Level 2 sibling Subarea
            final var marketingSubarea = Subarea.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("Marketing"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(5000.00));

            final var totalBudget = Money.euros(10000.00);

            // when (Level 1 Root Area)
            final var rootArea = Area.withChildren(
                    DomainGenerators.randomOuId(),
                    OuName.of("Headquarters"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    totalBudget,
                    Set.of(childArea, marketingSubarea));

            // then
            assertThat(rootArea.loadedChildren()).containsExactlyInAnyOrder(childArea, marketingSubarea);
            assertThat(rootArea.budget()).isEqualTo(totalBudget);
            assertThat(rootArea.totalSubtreeBudget()).isEqualTo(totalBudget);
            assertThat(rootArea.isSubtreeLoaded()).isTrue();
        }

        @Test
        @DisplayName("should throw AreaBudgetMismatchException when loaded children budget sum does not match Area budget")
        void should_throwException_when_childrenBudgetSumDoesNotMatch() {
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
            assertThatThrownBy(() -> Area.withChildren(
                            areaId,
                            OuName.of("Software Engineering"),
                            Set.of(DomainGenerators.randomCorporateKey()),
                            Set.of(),
                            declaredBudget,
                            Set.of(subarea1, subarea2)))
                    .isInstanceOf(AreaBudgetMismatchException.class)
                    .hasMessageContaining("does not match the sum of its child OU budgets");
        }

        @Test
        @DisplayName("should throw InvalidOuException when loaded children have IDs not in childIds")
        void should_throwException_when_loadedChildrenNotMatchingChildIds() {
            final var sub1 = DomainGenerators.randomSubarea();
            final var otherId = DomainGenerators.randomOuId();

            assertThatThrownBy(() -> new Area(
                            DomainGenerators.randomOuId(),
                            DomainGenerators.randomOuName(),
                            Set.of(DomainGenerators.randomCorporateKey()),
                            Set.of(),
                            sub1.budget(),
                            Set.of(otherId),
                            Set.of(sub1)))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("not registered in childIds");
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
        @DisplayName("should throw InvalidOuException when childIds set is null")
        void should_throwException_when_childIdsIsNull() {
            assertThatThrownBy(() -> new Area(
                            DomainGenerators.randomOuId(),
                            DomainGenerators.randomOuName(),
                            Set.of(DomainGenerators.randomCorporateKey()),
                            Set.of(),
                            Money.euros(1000.00),
                            null,
                            Set.of()))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("child IDs cannot be null");
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
