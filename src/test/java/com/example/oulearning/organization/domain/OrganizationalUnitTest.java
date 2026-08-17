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
class OrganizationalUnitTest {

    @Nested
    @DisplayName("Creation and Leaf/Root Behavior")
    class CreationAndLeafRootBehavior {

        @Test
        @DisplayName("should create leaf OrganizationalUnit with no children")
        void should_createLeafUnit_withNoChildren() {
            // given
            final var id = DomainGenerators.randomOuId();
            final var name = DomainGenerators.randomOuName();
            final var owners = Set.of(DomainGenerators.randomCorporateKey());
            final var parentIds = Set.of(DomainGenerators.randomOuId());
            final var budget = Money.euros(5000.00);

            // when
            final var leaf = OrganizationalUnit.leaf(id, name, owners, parentIds, budget);

            // then
            assertThat(leaf.id()).isEqualTo(id);
            assertThat(leaf.name()).isEqualTo(name);
            assertThat(leaf.type()).isEqualTo(OuType.SUBAREA);
            assertThat(leaf.owners()).isEqualTo(owners);
            assertThat(leaf.parentIds()).isEqualTo(parentIds);
            assertThat(leaf.budget()).isEqualTo(budget);
            assertThat(leaf.childIds()).isEmpty();
            assertThat(leaf.loadedChildren()).isEmpty();
            assertThat(leaf.isLeaf()).isTrue();
            assertThat(leaf.isRoot()).isFalse();
            assertThat(leaf.isSubtreeLoaded()).isTrue();
            assertThat(leaf.totalSubtreeBudget()).isEqualTo(budget);
        }

        @Test
        @DisplayName("should identify root unit when parentIds is empty")
        void should_identifyRootUnit() {
            final var root = OrganizationalUnit.leaf(
                    DomainGenerators.randomOuId(),
                    DomainGenerators.randomOuName(),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(10000.00));

            assertThat(root.isRoot()).isTrue();
            assertThat(root.isLeaf()).isTrue();
        }

        @Test
        @DisplayName("should create unit with child IDs when subtree is not loaded")
        void should_createUnit_withChildIds_whenSubtreeNotLoaded() {
            final var id = DomainGenerators.randomOuId();
            final var child1 = DomainGenerators.randomOuId();
            final var child2 = DomainGenerators.randomOuId();
            final var budget = Money.euros(10000.00);

            final var unit = OrganizationalUnit.of(
                    id,
                    DomainGenerators.randomOuName(),
                    OuType.AREA,
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(DomainGenerators.randomOuId()),
                    budget,
                    Set.of(child1, child2));

            assertThat(unit.isLeaf()).isFalse();
            assertThat(unit.isRoot()).isFalse();
            assertThat(unit.childIds()).containsExactlyInAnyOrder(child1, child2);
            assertThat(unit.loadedChildren()).isEmpty();
            assertThat(unit.isSubtreeLoaded()).isFalse();
            assertThat(unit.totalSubtreeBudget()).isEqualTo(budget);
        }
    }

    @Nested
    @DisplayName("N-Level Hierarchy and Budget Validation")
    class NLevelHierarchyAndBudgetValidation {

        @Test
        @DisplayName("should create N-level hierarchy with loaded children when budgets match")
        void should_createNLevelHierarchy_when_budgetsMatch() {
            // Level 3
            final var l3Sub1 = OrganizationalUnit.leaf(
                    DomainGenerators.randomOuId(),
                    OuName.of("Frontend"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(2000.00));
            final var l3Sub2 = OrganizationalUnit.leaf(
                    DomainGenerators.randomOuId(),
                    OuName.of("Backend"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(3000.00));

            // Level 2 Area
            final var l2Area = OrganizationalUnit.withChildren(
                    DomainGenerators.randomOuId(),
                    OuName.of("Engineering"),
                    OuType.AREA,
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(5000.00),
                    Set.of(l3Sub1, l3Sub2));

            // Level 2 Leaf
            final var l2Sales = OrganizationalUnit.leaf(
                    DomainGenerators.randomOuId(),
                    OuName.of("Sales"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(5000.00));

            // Level 1 Root
            final var root = OrganizationalUnit.withChildren(
                    DomainGenerators.randomOuId(),
                    OuName.of("Headquarters"),
                    OuType.ORGANIZATION,
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(10000.00),
                    Set.of(l2Area, l2Sales));

            assertThat(root.isRoot()).isTrue();
            assertThat(root.isLeaf()).isFalse();
            assertThat(root.isSubtreeLoaded()).isTrue();
            assertThat(root.loadedChildren()).containsExactlyInAnyOrder(l2Area, l2Sales);
            assertThat(root.totalSubtreeBudget()).isEqualTo(Money.euros(10000.00));
        }

        @Test
        @DisplayName("should throw OuBudgetMismatchException when children budget sum does not match total budget")
        void should_throwException_when_childrenBudgetSumDoesNotMatch() {
            final var child1 = OrganizationalUnit.leaf(
                    DomainGenerators.randomOuId(),
                    OuName.of("Team A"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(3000.00));
            final var child2 = OrganizationalUnit.leaf(
                    DomainGenerators.randomOuId(),
                    OuName.of("Team B"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(3000.00));
            final var declaredBudget = Money.euros(10000.00); // 6000 != 10000

            assertThatThrownBy(() -> OrganizationalUnit.withChildren(
                            DomainGenerators.randomOuId(),
                            OuName.of("Parent Area"),
                            OuType.AREA,
                            Set.of(DomainGenerators.randomCorporateKey()),
                            Set.of(),
                            declaredBudget,
                            Set.of(child1, child2)))
                    .isInstanceOf(OuBudgetMismatchException.class)
                    .hasMessageContaining("does not match the sum of its child OU budgets");
        }

        @Test
        @DisplayName("should throw InvalidOuException when loaded children have IDs not in childIds")
        void should_throwException_when_loadedChildrenNotMatchingChildIds() {
            final var child = DomainGenerators.randomLeafOu();
            final var otherId = DomainGenerators.randomOuId();

            assertThatThrownBy(() -> new OrganizationalUnit(
                            DomainGenerators.randomOuId(),
                            DomainGenerators.randomOuName(),
                            OuType.AREA,
                            Set.of(DomainGenerators.randomCorporateKey()),
                            Set.of(),
                            child.budget(),
                            Set.of(otherId),
                            Set.of(child)))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("not registered in childIds");
        }
    }

    @Nested
    @DisplayName("Validation and Immutability")
    class ValidationAndImmutability {

        @Test
        @DisplayName("should throw InvalidOuException when required fields are null")
        void should_throwException_when_fieldsAreNull() {
            final var id = DomainGenerators.randomOuId();
            final var name = DomainGenerators.randomOuName();
            final var owners = Set.of(DomainGenerators.randomCorporateKey());
            final var parentIds = Set.of(DomainGenerators.randomOuId());
            final var budget = Money.euros(1000.00);

            assertThatThrownBy(() -> new OrganizationalUnit(null, name, OuType.AREA, owners, parentIds, budget, Set.of(), Set.of()))
                    .isInstanceOf(InvalidOuException.class);
            assertThatThrownBy(() -> new OrganizationalUnit(id, null, OuType.AREA, owners, parentIds, budget, Set.of(), Set.of()))
                    .isInstanceOf(InvalidOuException.class);
            assertThatThrownBy(() -> new OrganizationalUnit(id, name, null, owners, parentIds, budget, Set.of(), Set.of()))
                    .isInstanceOf(InvalidOuException.class);
            assertThatThrownBy(() -> new OrganizationalUnit(id, name, OuType.AREA, null, parentIds, budget, Set.of(), Set.of()))
                    .isInstanceOf(InvalidOuException.class);
            assertThatThrownBy(() -> new OrganizationalUnit(id, name, OuType.AREA, owners, null, budget, Set.of(), Set.of()))
                    .isInstanceOf(InvalidOuException.class);
            assertThatThrownBy(() -> new OrganizationalUnit(id, name, OuType.AREA, owners, parentIds, null, Set.of(), Set.of()))
                    .isInstanceOf(InvalidOuException.class);
        }

        @Test
        @DisplayName("should be equal when all fields match")
        void should_beEqual_when_allFieldsMatch() {
            final var id = DomainGenerators.randomOuId();
            final var name = DomainGenerators.randomOuName();
            final var owners = Set.of(DomainGenerators.randomCorporateKey());
            final var parentIds = Set.of(DomainGenerators.randomOuId());
            final var budget = Money.euros(5000.00);

            final var ou1 = OrganizationalUnit.leaf(id, name, owners, parentIds, budget);
            final var ou2 = OrganizationalUnit.leaf(id, name, owners, parentIds, budget);

            assertThat(ou1).isEqualTo(ou2);
            assertThat(ou1.hashCode()).isEqualTo(ou2.hashCode());
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var unit = DomainGenerators.randomOrganizationalUnit();
            assertThat(unit.getClass().isRecord()).isTrue();
        }
    }
}
