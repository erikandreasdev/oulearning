package com.example.oulearning.organization.domain.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrganizationTest {

    @Test
    @DisplayName("given empty Organization, when adding and removing OuIds, then set is updated accordingly")
    void givenEmptyOrganization_whenAddingAndRemovingOuIds_thenSetIsUpdatedAccordingly() {
        // given
        final var organization = new Organization();
        final var ou1 = HierarchyTestFactory.randomOuId();
        final var ou2 = HierarchyTestFactory.randomOuId();

        // when
        final var orgWithTwo = organization.addOu(ou1).addOu(ou2);

        // then
        assertThat(orgWithTwo.ouIds()).containsExactlyInAnyOrder(ou1, ou2);

        // when
        final var orgWithOne = orgWithTwo.removeOu(ou1);

        // then
        assertThat(orgWithOne.ouIds()).containsExactly(ou2);
    }

    @Test
    @DisplayName("given initial set of OuIds, when creating Organization, then contains all initial OuIds")
    void givenInitialSetOfOuIds_whenCreatingOrganization_thenContainsAllInitialOuIds() {
        // given
        final var ou1 = HierarchyTestFactory.randomOuId();

        // when
        final var organization = new Organization(Set.of(ou1));

        // then
        assertThat(organization.ouIds()).containsExactly(ou1);
    }

    @Test
    @DisplayName("given null set of OuIds, when creating Organization, then initialized with empty set")
    void givenNullSetOfOuIds_whenCreatingOrganization_thenInitializedWithEmptySet() {
        // given

        // when
        final var organization = new Organization(null);

        // then
        assertThat(organization.ouIds()).isEmpty();
    }

    @Test
    @DisplayName("given organization, when calling toString, then return formatted string")
    void givenOrganization_whenCallingToString_thenReturnFormattedString() {
        // given
        final var ou1 = HierarchyTestFactory.randomOuId();
        final var organization = new Organization(Set.of(ou1));

        // when
        final var str = organization.toString();

        // then
        assertThat(str).contains("Organization").contains(ou1.toString());
    }
}
