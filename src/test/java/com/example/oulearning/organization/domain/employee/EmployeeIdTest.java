package com.example.oulearning.organization.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.exception.InvalidEmployeeException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EmployeeIdTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid UUID, when creating EmployeeId, then id is created successfully")
        void givenValidUuid_whenCreatingEmployeeId_thenIdIsCreatedSuccessfully() {
            // given
            final var uuid = EmployeeTestFactory.randomUuid();

            // when
            final var id = EmployeeId.of(uuid);

            // then
            assertThat(id.value()).isEqualTo(uuid);
            assertThat(id).hasToString(uuid.toString());
        }

        @Test
        @DisplayName("given valid UUID string, when parsing EmployeeId, then id is parsed successfully")
        void givenValidUuidString_whenParsingEmployeeId_thenIdIsParsedSuccessfully() {
            // given
            final var uuid = EmployeeTestFactory.randomUuid();
            final var uuidString = " %s ".formatted(uuid);

            // when
            final var id = EmployeeId.fromString(uuidString);

            // then
            assertThat(id.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("given null UUID, when creating EmployeeId, then throw InvalidEmployeeException")
        void givenNullUuid_whenCreatingEmployeeId_thenThrowInvalidEmployeeException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> new EmployeeId(null))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("given blank UUID string, when parsing EmployeeId, then throw InvalidEmployeeException")
        void givenBlankUuidString_whenParsingEmployeeId_thenThrowInvalidEmployeeException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> EmployeeId.fromString("   "))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("cannot be null or blank");
        }

        @Test
        @DisplayName("given invalid UUID string, when parsing EmployeeId, then throw InvalidEmployeeException")
        void givenInvalidUuidString_whenParsingEmployeeId_thenThrowInvalidEmployeeException() {
            // given
            final var invalidUuid = Instancio.create(String.class);

            // when

            // then
            assertThatThrownBy(() -> EmployeeId.fromString(invalidUuid))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Invalid UUID format");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical UUIDs, when comparing EmployeeIds, then they are equal")
        void givenIdenticalUuids_whenComparingEmployeeIds_thenTheyAreEqual() {
            // given
            final var uuid = EmployeeTestFactory.randomUuid();
            final var id1 = EmployeeId.of(uuid);
            final var id2 = EmployeeId.of(uuid);

            // when

            // then
            assertThat(id1).isEqualTo(id2).hasSameHashCodeAs(id2);
        }

        @Test
        @DisplayName("given different UUIDs, when comparing EmployeeIds, then they are not equal")
        void givenDifferentUuids_whenComparingEmployeeIds_thenTheyAreNotEqual() {
            // given
            final var id1 = EmployeeTestFactory.randomEmployeeId();
            final var id2 = EmployeeTestFactory.randomEmployeeId();

            // when

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
