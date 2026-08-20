package com.example.oulearning.organization.domain.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.EmployeeId;
import com.example.oulearning.organization.domain.employee.EmployeeTestFactory;
import com.example.oulearning.organization.domain.hierarchy.exception.InvalidOrganizationalUnitException;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrganizationalUnitTest {

    private final OrganizationalUnitId id = HierarchyTestFactory.randomOrganizationalUnitId();
    private final Name name = HierarchyTestFactory.randomName();
    private final OrganizationalUnitId parentId = HierarchyTestFactory.randomOrganizationalUnitId();
    private final EmployeeId emp1 = EmployeeTestFactory.randomEmployeeId();
    private final EmployeeId emp2 = EmployeeTestFactory.randomEmployeeId();

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName(
                "given all valid fields, when creating OrganizationalUnit, then OrganizationalUnit is created successfully")
        void givenAllValidFields_whenCreatingOrganizationalUnit_thenOrganizationalUnitIsCreatedSuccessfully() {
            // given
            final var childId = HierarchyTestFactory.randomOrganizationalUnitId();

            // when
            final var organizationalUnit =
                    OrganizationalUnit.of(id, name, parentId, Set.of(childId), Set.of(emp1), Set.of(emp2));

            // then
            assertThat(organizationalUnit.id()).isEqualTo(id);
            assertThat(organizationalUnit.name()).isEqualTo(name);
            assertThat(organizationalUnit.parentId()).contains(parentId);
            assertThat(organizationalUnit.childIds()).containsExactly(childId);
            assertThat(organizationalUnit.owners()).containsExactly(emp1);
            assertThat(organizationalUnit.members()).containsExactly(emp2);
            assertThat(organizationalUnit)
                    .hasToString("OrganizationalUnit[id=%s, name=%s, parentId=%s]".formatted(id, name, parentId));
        }

        @Test
        @DisplayName(
                "given root organizational unit parameters, when creating root OrganizationalUnit, then collections are empty")
        void givenRootOrganizationalUnitParams_whenCreatingRootOrganizationalUnit_thenCollectionsAreEmpty() {
            // given

            // when
            final var organizationalUnit = OrganizationalUnit.of(id, name);

            // then
            assertThat(organizationalUnit.id()).isEqualTo(id);
            assertThat(organizationalUnit.name()).isEqualTo(name);
            assertThat(organizationalUnit.parentId()).isEmpty();
            assertThat(organizationalUnit.childIds()).isEmpty();
            assertThat(organizationalUnit.owners()).isEmpty();
            assertThat(organizationalUnit.members()).isEmpty();
        }

        @Test
        @DisplayName(
                "given null required parameters, when creating OrganizationalUnit, then throw InvalidOrganizationalUnitException")
        void givenNullRequiredParams_whenCreatingOrganizationalUnit_thenThrowInvalidOrganizationalUnitException() {
            // given
            final var emptyOuSet = Set.<OrganizationalUnitId>of();
            final var emptyEmployeeSet = Set.<EmployeeId>of();

            // when

            // then
            assertThatThrownBy(() -> new OrganizationalUnit(
                            null, name, parentId, emptyOuSet, emptyEmployeeSet, emptyEmployeeSet))
                    .isInstanceOf(InvalidOrganizationalUnitException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> new OrganizationalUnit(
                            id, null, parentId, emptyOuSet, emptyEmployeeSet, emptyEmployeeSet))
                    .isInstanceOf(InvalidOrganizationalUnitException.class)
                    .hasMessageContaining("cannot be null");
        }
    }

    @Nested
    @DisplayName("Identity and Equality")
    class IdentityAndEquality {

        @Test
        @DisplayName("given organizational units with same id, when comparing, then they are equal")
        void givenOrganizationalUnitsWithSameId_whenComparing_thenTheyAreEqual() {
            // given
            final var unit1 = OrganizationalUnit.of(id, name, parentId, Set.of(), Set.of(), Set.of());
            final var unit2 = OrganizationalUnit.of(
                    id, HierarchyTestFactory.randomName(), null, Set.of(), Set.of(), Set.of());

            // when

            // then
            assertThat(unit1).isEqualTo(unit2).hasSameHashCodeAs(unit2);
        }

        @Test
        @DisplayName("given organizational units with different ids, when comparing, then they are not equal")
        void givenOrganizationalUnitsWithDifferentIds_whenComparing_thenTheyAreNotEqual() {
            // given
            final var unit1 = OrganizationalUnit.of(id, name, parentId, Set.of(), Set.of(), Set.of());
            final var unit2 = OrganizationalUnit.of(
                    HierarchyTestFactory.randomOrganizationalUnitId(),
                    name,
                    parentId,
                    Set.of(),
                    Set.of(),
                    Set.of());

            // when

            // then
            assertThat(unit1).isNotEqualTo(unit2);
        }

        @Test
        @DisplayName("given same organizational unit instance, when comparing, then they are equal")
        void givenSameOrganizationalUnitInstance_whenComparing_thenTheyAreEqual() {
            // given
            final var organizationalUnit = OrganizationalUnit.of(id, name);

            // when

            // then
            assertThat(organizationalUnit).isEqualTo(organizationalUnit);
        }

        @Test
        @DisplayName("given null or different object type, when comparing, then they are not equal")
        void givenNullOrDifferentType_whenComparing_thenTheyAreNotEqual() {
            // given
            final var organizationalUnit = OrganizationalUnit.of(id, name);

            // when

            // then
            assertThat(organizationalUnit).isNotEqualTo(null).isNotEqualTo(new Object());
        }
    }
}
