package com.example.oulearning.organization.domain.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.hierarchy.exception.InvalidOrganizationalUnitException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrganizationalUnitIdTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid UUID, when creating OrganizationalUnitId, then id is created successfully")
        void givenValidUuid_whenCreatingOrganizationalUnitId_thenIdIsCreatedSuccessfully() {
            // given
            final var uuid = HierarchyTestFactory.randomUuid();

            // when
            final var id = OrganizationalUnitId.of(uuid);

            // then
            assertThat(id.value()).isEqualTo(uuid);
            assertThat(id).hasToString(uuid.toString());
        }

        @Test
        @DisplayName("given valid UUID string, when parsing OrganizationalUnitId, then id is parsed successfully")
        void givenValidUuidString_whenParsingOrganizationalUnitId_thenIdIsParsedSuccessfully() {
            // given
            final var uuid = HierarchyTestFactory.randomUuid();
            final var uuidString = " %s ".formatted(uuid);

            // when
            final var id = OrganizationalUnitId.fromString(uuidString);

            // then
            assertThat(id.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("given null UUID, when creating OrganizationalUnitId, then throw InvalidOrganizationalUnitException")
        void givenNullUuid_whenCreatingOrganizationalUnitId_thenThrowInvalidOrganizationalUnitException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> new OrganizationalUnitId(null))
                    .isInstanceOf(InvalidOrganizationalUnitException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("given blank UUID string, when parsing OrganizationalUnitId, then throw InvalidOrganizationalUnitException")
        void givenBlankUuidString_whenParsingOrganizationalUnitId_thenThrowInvalidOrganizationalUnitException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> OrganizationalUnitId.fromString("   "))
                    .isInstanceOf(InvalidOrganizationalUnitException.class)
                    .hasMessageContaining("cannot be null or blank");
        }

        @Test
        @DisplayName("given invalid UUID string, when parsing OrganizationalUnitId, then throw InvalidOrganizationalUnitException")
        void givenInvalidUuidString_whenParsingOrganizationalUnitId_thenThrowInvalidOrganizationalUnitException() {
            // given
            final var invalidUuid = Instancio.create(String.class);

            // when

            // then
            assertThatThrownBy(() -> OrganizationalUnitId.fromString(invalidUuid))
                    .isInstanceOf(InvalidOrganizationalUnitException.class)
                    .hasMessageContaining("Invalid UUID format");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical UUIDs, when comparing OrganizationalUnitIds, then they are equal")
        void givenIdenticalUuids_whenComparingOrganizationalUnitIds_thenTheyAreEqual() {
            // given
            final var uuid = HierarchyTestFactory.randomUuid();
            final var id1 = OrganizationalUnitId.of(uuid);
            final var id2 = OrganizationalUnitId.of(uuid);

            // when

            // then
            assertThat(id1).isEqualTo(id2).hasSameHashCodeAs(id2);
        }

        @Test
        @DisplayName("given different UUIDs, when comparing OrganizationalUnitIds, then they are not equal")
        void givenDifferentUuids_whenComparingOrganizationalUnitIds_thenTheyAreNotEqual() {
            // given
            final var id1 = HierarchyTestFactory.randomOrganizationalUnitId();
            final var id2 = HierarchyTestFactory.randomOrganizationalUnitId();

            // when

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
