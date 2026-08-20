package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TypeIdTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid UUID, when creating TypeId, then create successfully")
        void givenValidUuid_whenCreatingTypeId_thenCreateSuccessfully() {
            // given
            final var uuid = TrainingTestFactory.randomUuid();

            // when
            final var id = TypeId.of(uuid);

            // then
            assertThat(id.value()).isEqualTo(uuid);
            assertThat(id).hasToString(uuid.toString());
        }

        @Test
        @DisplayName("given valid UUID string, when parsing TypeId, then parse successfully")
        void givenValidUuidString_whenParsingTypeId_thenParseSuccessfully() {
            // given
            final var uuid = TrainingTestFactory.randomUuid();

            // when
            final var id = TypeId.fromString(" %s ".formatted(uuid));

            // then
            assertThat(id.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("given null UUID, when creating TypeId, then throw exception")
        void givenNullUuid_whenCreatingTypeId_thenThrowException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> new TypeId(null))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("given invalid UUID string, when parsing TypeId, then throw exception")
        void givenInvalidUuidString_whenParsingTypeId_thenThrowException() {
            // given
            final var invalidUuid = Instancio.create(String.class);

            // when

            // then
            assertThatThrownBy(() -> TypeId.fromString(invalidUuid))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Invalid UUID format");

            assertThatThrownBy(() -> TypeId.fromString(""))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("cannot be null or blank");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical UUIDs, when comparing TypeIds, then they are equal")
        void givenIdenticalUuids_whenComparingTypeIds_thenTheyAreEqual() {
            // given
            final var uuid = TrainingTestFactory.randomUuid();
            final var id1 = TypeId.of(uuid);
            final var id2 = TypeId.of(uuid);

            // when

            // then
            assertThat(id1).isEqualTo(id2).hasSameHashCodeAs(id2);
        }

        @Test
        @DisplayName("given different UUIDs, when comparing TypeIds, then they are not equal")
        void givenDifferentUuids_whenComparingTypeIds_thenTheyAreNotEqual() {
            // given
            final var id1 = TrainingTestFactory.randomTypeId();
            final var id2 = TrainingTestFactory.randomTypeId();

            // when

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
