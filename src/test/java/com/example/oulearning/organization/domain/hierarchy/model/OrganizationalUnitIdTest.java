package com.example.oulearning.organization.domain.hierarchy.model;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.port.in.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.port.in.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.application.hierarchy.port.in.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.domain.model.*;
import com.example.oulearning.training.application.port.in.*;
import com.example.oulearning.training.application.exception.*;

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
        @DisplayName("given valid id, when creating OrganizationalUnitId, then id is created successfully")
        void givenValidId_whenCreatingOrganizationalUnitId_thenIdIsCreatedSuccessfully() {
            // given
            final var value = HierarchyTestFactory.randomId();

            // when
            final var id = OrganizationalUnitId.of(value);

            // then
            assertThat(id.value()).isEqualTo(value);
            assertThat(id).hasToString(String.valueOf(value));
        }

        @Test
        @DisplayName("given valid id string, when parsing OrganizationalUnitId, then id is parsed successfully")
        void givenValidIdString_whenParsingOrganizationalUnitId_thenIdIsParsedSuccessfully() {
            // given
            final var value = HierarchyTestFactory.randomId();
            final var idString = " %d ".formatted(value);

            // when
            final var id = OrganizationalUnitId.fromString(idString);

            // then
            assertThat(id.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("given non-positive id, when creating OrganizationalUnitId, then throw InvalidOrganizationalUnitException")
        void givenNonPositiveId_whenCreatingOrganizationalUnitId_thenThrowInvalidOrganizationalUnitException() {
            // given
            final var nonPositiveValue = Instancio.gen().longs().range(Long.MIN_VALUE, 0L).get();

            // when

            // then
            assertThatThrownBy(() -> new OrganizationalUnitId(nonPositiveValue))
                    .isInstanceOf(InvalidOrganizationalUnitException.class)
                    .hasMessageContaining("must be strictly positive");
        }

        @Test
        @DisplayName("given blank id string, when parsing OrganizationalUnitId, then throw InvalidOrganizationalUnitException")
        void givenBlankIdString_whenParsingOrganizationalUnitId_thenThrowInvalidOrganizationalUnitException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> OrganizationalUnitId.fromString("   "))
                    .isInstanceOf(InvalidOrganizationalUnitException.class)
                    .hasMessageContaining("cannot be null or blank");
        }

        @Test
        @DisplayName("given invalid id string, when parsing OrganizationalUnitId, then throw InvalidOrganizationalUnitException")
        void givenInvalidIdString_whenParsingOrganizationalUnitId_thenThrowInvalidOrganizationalUnitException() {
            // given
            final var invalidId = Instancio.create(String.class);

            // when

            // then
            assertThatThrownBy(() -> OrganizationalUnitId.fromString(invalidId))
                    .isInstanceOf(InvalidOrganizationalUnitException.class)
                    .hasMessageContaining("Invalid Organizational unit id format");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical ids, when comparing OrganizationalUnitIds, then they are equal")
        void givenIdenticalIds_whenComparingOrganizationalUnitIds_thenTheyAreEqual() {
            // given
            final var value = HierarchyTestFactory.randomId();
            final var id1 = OrganizationalUnitId.of(value);
            final var id2 = OrganizationalUnitId.of(value);

            // when

            // then
            assertThat(id1).isEqualTo(id2).hasSameHashCodeAs(id2);
        }

        @Test
        @DisplayName("given different ids, when comparing OrganizationalUnitIds, then they are not equal")
        void givenDifferentIds_whenComparingOrganizationalUnitIds_thenTheyAreNotEqual() {
            // given
            final var id1 = HierarchyTestFactory.randomOrganizationalUnitId();
            final var id2 = OrganizationalUnitId.of(id1.value() + 1L);

            // when

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
