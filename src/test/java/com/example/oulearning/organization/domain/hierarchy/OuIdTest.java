package com.example.oulearning.organization.domain.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.hierarchy.exception.InvalidOuException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OuIdTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid UUID, when creating OuId, then id is created successfully")
        void givenValidUuid_whenCreatingOuId_thenIdIsCreatedSuccessfully() {
            // given
            final var uuid = HierarchyTestFactory.randomUuid();

            // when
            final var id = OuId.of(uuid);

            // then
            assertThat(id.value()).isEqualTo(uuid);
            assertThat(id).hasToString(uuid.toString());
        }

        @Test
        @DisplayName("given valid UUID string, when parsing OuId, then id is parsed successfully")
        void givenValidUuidString_whenParsingOuId_thenIdIsParsedSuccessfully() {
            // given
            final var uuid = HierarchyTestFactory.randomUuid();
            final var uuidString = " %s ".formatted(uuid);

            // when
            final var id = OuId.fromString(uuidString);

            // then
            assertThat(id.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("given null UUID, when creating OuId, then throw InvalidOuException")
        void givenNullUuid_whenCreatingOuId_thenThrowInvalidOuException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> new OuId(null))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("given blank UUID string, when parsing OuId, then throw InvalidOuException")
        void givenBlankUuidString_whenParsingOuId_thenThrowInvalidOuException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> OuId.fromString("   "))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("cannot be null or blank");
        }

        @Test
        @DisplayName("given invalid UUID string, when parsing OuId, then throw InvalidOuException")
        void givenInvalidUuidString_whenParsingOuId_thenThrowInvalidOuException() {
            // given
            final var invalidUuid = Instancio.create(String.class);

            // when

            // then
            assertThatThrownBy(() -> OuId.fromString(invalidUuid))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("Invalid UUID format");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical UUIDs, when comparing OuIds, then they are equal")
        void givenIdenticalUuids_whenComparingOuIds_thenTheyAreEqual() {
            // given
            final var uuid = HierarchyTestFactory.randomUuid();
            final var id1 = OuId.of(uuid);
            final var id2 = OuId.of(uuid);

            // when

            // then
            assertThat(id1).isEqualTo(id2).hasSameHashCodeAs(id2);
        }

        @Test
        @DisplayName("given different UUIDs, when comparing OuIds, then they are not equal")
        void givenDifferentUuids_whenComparingOuIds_thenTheyAreNotEqual() {
            // given
            final var id1 = HierarchyTestFactory.randomOuId();
            final var id2 = HierarchyTestFactory.randomOuId();

            // when

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
