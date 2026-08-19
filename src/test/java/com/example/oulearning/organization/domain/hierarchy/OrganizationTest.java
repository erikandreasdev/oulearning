package com.example.oulearning.organization.domain.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrganizationTest {

    @Test
    @DisplayName("should add and remove OU ids from organization")
    void should_addAndRemoveOuIds() {
        Organization organization = new Organization();
        Id ou1 = Id.of(UUID.randomUUID());
        Id ou2 = Id.of(UUID.randomUUID());

        organization.addOu(ou1);
        organization.addOu(ou2);

        assertThat(organization.ouIds()).containsExactlyInAnyOrder(ou1, ou2);

        organization.removeOu(ou1);
        assertThat(organization.ouIds()).containsExactly(ou2);
    }

    @Test
    @DisplayName("should initialize organization with OU ids")
    void should_initializeWithOuIds() {
        Id ou1 = Id.of(UUID.randomUUID());
        Organization organization = new Organization(Set.of(ou1));

        assertThat(organization.ouIds()).containsExactly(ou1);
    }
}
