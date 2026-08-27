package com.example.oulearning.organization.domain.hierarchy.model;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.domain.model.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrganizationTest {

    @Test
    @DisplayName(
            "given empty Organization, when adding and removing OrganizationalUnitIds, then set is updated accordingly")
    void givenEmptyOrganization_whenAddingAndRemovingOrganizationalUnitIds_thenSetIsUpdatedAccordingly() {
        // given
        final var organization = new Organization();
        final var unit1 = HierarchyTestFactory.randomOrganizationalUnitId();
        final var unit2 = HierarchyTestFactory.randomOrganizationalUnitId();

        // when
        final var orgWithTwo =
                organization.addOrganizationalUnit(unit1).addOrganizationalUnit(unit2);

        // then
        assertThat(orgWithTwo.organizationalUnitIds()).containsExactlyInAnyOrder(unit1, unit2);

        // when
        final var orgWithOne = orgWithTwo.removeOrganizationalUnit(unit1);

        // then
        assertThat(orgWithOne.organizationalUnitIds()).containsExactly(unit2);
    }

    @Test
    @DisplayName(
            "given initial set of OrganizationalUnitIds, when creating Organization, then contains all initial OrganizationalUnitIds")
    void givenInitialSetOfOrganizationalUnitIds_whenCreatingOrganization_thenContainsAllInitialOrganizationalUnitIds() {
        // given
        final var unit1 = HierarchyTestFactory.randomOrganizationalUnitId();

        // when
        final var organization = new Organization(Set.of(unit1));

        // then
        assertThat(organization.organizationalUnitIds()).containsExactly(unit1);
    }

    @Test
    @DisplayName(
            "given null set of OrganizationalUnitIds, when creating Organization, then initialized with empty set")
    void givenNullSetOfOrganizationalUnitIds_whenCreatingOrganization_thenInitializedWithEmptySet() {
        // given

        // when
        final var organization = new Organization(null);

        // then
        assertThat(organization.organizationalUnitIds()).isEmpty();
    }

    @Test
    @DisplayName("given organization, when calling toString, then return formatted string")
    void givenOrganization_whenCallingToString_thenReturnFormattedString() {
        // given
        final var unit1 = HierarchyTestFactory.randomOrganizationalUnitId();
        final var organization = new Organization(Set.of(unit1));

        // when
        final var str = organization.toString();

        // then
        assertThat(str).contains("Organization").contains(unit1.toString());
    }
}
