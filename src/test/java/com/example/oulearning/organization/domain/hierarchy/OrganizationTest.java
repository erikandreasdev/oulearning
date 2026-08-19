package com.example.oulearning.organization.domain.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrganizationTest {

    @Test
    @DisplayName("given empty Organization, when adding and removing OuIds, then set is updated accordingly")
    void givenEmptyOrganization_whenAddingAndRemovingOuIds_thenSetIsUpdatedAccordingly() {

        final var organization = new Organization();
        final var ou1 = HierarchyTestFactory.randomOuId();
        final var ou2 = HierarchyTestFactory.randomOuId();


        organization.addOu(ou1);
        organization.addOu(ou2);


        assertThat(organization.ouIds()).containsExactlyInAnyOrder(ou1, ou2);


        organization.removeOu(ou1);


        assertThat(organization.ouIds()).containsExactly(ou2);
    }

    @Test
    @DisplayName("given initial set of OuIds, when creating Organization, then contains all initial OuIds")
    void givenInitialSetOfOuIds_whenCreatingOrganization_thenContainsAllInitialOuIds() {

        final var ou1 = HierarchyTestFactory.randomOuId();


        final var organization = new Organization(Set.of(ou1));


        assertThat(organization.ouIds()).containsExactly(ou1);
    }
}
