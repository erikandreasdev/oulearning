package com.example.oulearning.organization.domain.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.DomainGenerators;
import com.example.oulearning.organization.domain.employee.vo.identity.CorporateKey;
import com.example.oulearning.organization.domain.unit.exception.InvalidOuException;
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

            // when
            final var leaf = OrganizationalUnit.leaf(id, name, owners, parentIds);

            // then
            assertThat(leaf.id()).isEqualTo(id);
            assertThat(leaf.name()).isEqualTo(name);
            assertThat(leaf.type()).isEqualTo(OuType.SUBAREA);
            assertThat(leaf.owners()).isEqualTo(owners);
            assertThat(leaf.parentIds()).isEqualTo(parentIds);
            assertThat(leaf.childIds()).isEmpty();
            assertThat(leaf.loadedChildren()).isEmpty();
            assertThat(leaf.isLeaf()).isTrue();
            assertThat(leaf.isRoot()).isFalse();
            assertThat(leaf.isSubtreeLoaded()).isTrue();
        }

        @Test
        @DisplayName("should identify root unit when parentIds is empty")
        void should_identifyRootUnit() {
            final var root = OrganizationalUnit.leaf(
                    DomainGenerators.randomOuId(),
                    DomainGenerators.randomOuName(),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of());

            assertThat(root.isRoot()).isTrue();
            assertThat(root.isLeaf()).isTrue();
        }

        @Test
        @DisplayName("should create unit with child IDs when subtree is not loaded")
        void should_createUnit_withChildIds_whenSubtreeNotLoaded() {
            final var id = DomainGenerators.randomOuId();
            final var child1 = DomainGenerators.randomOuId();
            final var child2 = DomainGenerators.randomOuId();

            final var unit = OrganizationalUnit.of(
                    id,
                    DomainGenerators.randomOuName(),
                    OuType.AREA,
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(DomainGenerators.randomOuId()),
                    Set.of(child1, child2));

            assertThat(unit.isLeaf()).isFalse();
            assertThat(unit.isRoot()).isFalse();
            assertThat(unit.childIds()).containsExactlyInAnyOrder(child1, child2);
            assertThat(unit.loadedChildren()).isEmpty();
            assertThat(unit.isSubtreeLoaded()).isFalse();
        }
    }

    @Nested
    @DisplayName("N-Level Hierarchy Construction")
    class NLevelHierarchyConstruction {

        @Test
        @DisplayName("should create N-level hierarchy with loaded children")
        void should_createNLevelHierarchy() {
            // Level 3
            final var l3Sub1 = OrganizationalUnit.leaf(
                    DomainGenerators.randomOuId(),
                    OuName.of("Frontend"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of());
            final var l3Sub2 = OrganizationalUnit.leaf(
                    DomainGenerators.randomOuId(),
                    OuName.of("Backend"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of());

            // Level 2 Area
            final var l2Area = OrganizationalUnit.withChildren(
                    DomainGenerators.randomOuId(),
                    OuName.of("Engineering"),
                    OuType.AREA,
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Set.of(l3Sub1, l3Sub2));

            // Level 2 Leaf
            final var l2Sales = OrganizationalUnit.leaf(
                    DomainGenerators.randomOuId(),
                    OuName.of("Sales"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of());

            // Level 1 Root
            final var root = OrganizationalUnit.withChildren(
                    DomainGenerators.randomOuId(),
                    OuName.of("Headquarters"),
                    OuType.ORGANIZATION,
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Set.of(l2Area, l2Sales));

            assertThat(root.isRoot()).isTrue();
            assertThat(root.isLeaf()).isFalse();
            assertThat(root.isSubtreeLoaded()).isTrue();
            assertThat(root.loadedChildren()).containsExactlyInAnyOrder(l2Area, l2Sales);
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

            assertThatThrownBy(() -> new OrganizationalUnit(null, name, OuType.AREA, owners, parentIds, Set.of(), Set.of()))
                    .isInstanceOf(InvalidOuException.class);
            assertThatThrownBy(() -> new OrganizationalUnit(id, null, OuType.AREA, owners, parentIds, Set.of(), Set.of()))
                    .isInstanceOf(InvalidOuException.class);
            assertThatThrownBy(() -> new OrganizationalUnit(id, name, null, owners, parentIds, Set.of(), Set.of()))
                    .isInstanceOf(InvalidOuException.class);
            assertThatThrownBy(() -> new OrganizationalUnit(id, name, OuType.AREA, null, parentIds, Set.of(), Set.of()))
                    .isInstanceOf(InvalidOuException.class);
            assertThatThrownBy(() -> new OrganizationalUnit(id, name, OuType.AREA, owners, null, Set.of(), Set.of()))
                    .isInstanceOf(InvalidOuException.class);
            assertThatThrownBy(() -> new OrganizationalUnit(id, name, OuType.AREA, owners, parentIds, null, Set.of()))
                    .isInstanceOf(InvalidOuException.class);
            assertThatThrownBy(() -> new OrganizationalUnit(id, name, OuType.AREA, owners, parentIds, Set.of(), null))
                    .isInstanceOf(InvalidOuException.class);
        }

        @Test
        @DisplayName("should be equal when all fields match")
        void should_beEqual_when_allFieldsMatch() {
            final var id = DomainGenerators.randomOuId();
            final var name = DomainGenerators.randomOuName();
            final var owners = Set.of(DomainGenerators.randomCorporateKey());
            final var parentIds = Set.of(DomainGenerators.randomOuId());

            final var ou1 = OrganizationalUnit.leaf(id, name, owners, parentIds);
            final var ou2 = OrganizationalUnit.leaf(id, name, owners, parentIds);

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
