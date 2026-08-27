package com.example.oulearning.organization.domain.hierarchy.model;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.domain.model.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.employee.model.EmployeeTestFactory;
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
                            null, name, parentId, emptyOuSet, emptyEmployeeSet, emptyEmployeeSet, true))
                    .isInstanceOf(InvalidOrganizationalUnitException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> new OrganizationalUnit(
                            id, null, parentId, emptyOuSet, emptyEmployeeSet, emptyEmployeeSet, true))
                    .isInstanceOf(InvalidOrganizationalUnitException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("given parameters, when creating with create factory, then active is true")
        void givenParams_whenCreatingWithFactory_thenActiveIsTrue() {
            // given

            // when
            final var ou = OrganizationalUnit.create(id, name, parentId);

            // then
            assertThat(ou.id()).isEqualTo(id);
            assertThat(ou.name()).isEqualTo(name);
            assertThat(ou.parentId()).contains(parentId);
            assertThat(ou.active()).isTrue();
        }

        @Test
        @DisplayName("given parameters, when reconstituting, then instance is reconstructed")
        void givenParams_whenReconstituting_thenInstanceIsReconstructed() {
            // given

            // when
            final var ou = OrganizationalUnit.reconstitute(
                    id, name, parentId, Set.of(), Set.of(emp1), Set.of(emp2), false);

            // then
            assertThat(ou.id()).isEqualTo(id);
            assertThat(ou.owners()).containsExactly(emp1);
            assertThat(ou.members()).containsExactly(emp2);
            assertThat(ou.active()).isFalse();
        }
    }

    @Nested
    @DisplayName("Mutations and Role Management")
    class MutationsAndRoleManagement {

        @Test
        @DisplayName("given new name, when renaming, then organizational unit has new name")
        void givenNewName_whenRenaming_thenOrganizationalUnitHasNewName() {
            // given
            final var ou = OrganizationalUnit.create(id, name, parentId);
            final var newName = HierarchyTestFactory.randomName();

            // when
            final var updated = ou.rename(newName);

            // then
            assertThat(updated.name()).isEqualTo(newName);
            assertThat(updated.id()).isEqualTo(id);
        }

        @Test
        @DisplayName("given owner, when adding owner, then owner is added")
        void givenOwner_whenAddingOwner_thenOwnerIsAdded() {
            // given
            final var ou = OrganizationalUnit.create(id, name, parentId);

            // when
            final var updated = ou.addOwner(emp1);

            // then
            assertThat(updated.owners()).containsExactly(emp1);
        }

        @Test
        @DisplayName("given existing owner, when removing owner, then owner is removed")
        void givenExistingOwner_whenRemovingOwner_thenOwnerIsRemoved() {
            // given
            final var ou = OrganizationalUnit.create(id, name, parentId).addOwner(emp1);

            // when
            final var updated = ou.removeOwner(emp1);

            // then
            assertThat(updated.owners()).isEmpty();
        }

        @Test
        @DisplayName("given multiple owners, when adding owners in batch, then all owners are added")
        void givenMultipleOwners_whenAddingOwnersInBatch_thenAllOwnersAreAdded() {
            // given
            final var ou = OrganizationalUnit.create(id, name, parentId);
            final var owners = Set.of(emp1, emp2);

            // when
            final var updated = ou.addOwners(owners);

            // then
            assertThat(updated.owners()).containsExactlyInAnyOrder(emp1, emp2);
        }

        @Test
        @DisplayName("given multiple owners, when removing owners in batch, then owners are removed")
        void givenMultipleOwners_whenRemovingOwnersInBatch_thenOwnersAreRemoved() {
            // given
            final var ou = OrganizationalUnit.create(id, name, parentId).addOwners(Set.of(emp1, emp2));

            // when
            final var updated = ou.removeOwners(Set.of(emp1));

            // then
            assertThat(updated.owners()).containsExactly(emp2);
        }

        @Test
        @DisplayName("given member, when adding member, then member is added")
        void givenMember_whenAddingMember_thenMemberIsAdded() {
            // given
            final var ou = OrganizationalUnit.create(id, name, parentId);

            // when
            final var updated = ou.addMember(emp1);

            // then
            assertThat(updated.members()).containsExactly(emp1);
        }

        @Test
        @DisplayName("given existing member, when removing member, then member is removed")
        void givenExistingMember_whenRemovingMember_thenMemberIsRemoved() {
            // given
            final var ou = OrganizationalUnit.create(id, name, parentId).addMember(emp1);

            // when
            final var updated = ou.removeMember(emp1);

            // then
            assertThat(updated.members()).isEmpty();
        }

        @Test
        @DisplayName("given multiple members, when adding members in batch, then all members are added")
        void givenMultipleMembers_whenAddingMembersInBatch_thenAllMembersAreAdded() {
            // given
            final var ou = OrganizationalUnit.create(id, name, parentId);
            final var members = Set.of(emp1, emp2);

            // when
            final var updated = ou.addMembers(members);

            // then
            assertThat(updated.members()).containsExactlyInAnyOrder(emp1, emp2);
        }

        @Test
        @DisplayName("given multiple members, when removing members in batch, then members are removed")
        void givenMultipleMembers_whenRemovingMembersInBatch_thenMembersAreRemoved() {
            // given
            final var ou = OrganizationalUnit.create(id, name, parentId).addMembers(Set.of(emp1, emp2));

            // when
            final var updated = ou.removeMembers(Set.of(emp1));

            // then
            assertThat(updated.members()).containsExactly(emp2);
        }

        @Test
        @DisplayName("given active unit, when deactivating, then unit is inactive")
        void givenActiveUnit_whenDeactivating_thenUnitIsInactive() {
            // given
            final var ou = OrganizationalUnit.create(id, name, parentId);

            // when
            final var deactivated = ou.deactivate();

            // then
            assertThat(deactivated.active()).isFalse();
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
