package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TrainingIdTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid UUID, when creating TrainingId, then create successfully")
        void givenValidUuid_whenCreatingTrainingId_thenCreateSuccessfully() {

            final var uuid = TrainingTestFactory.randomUuid();


            final var id = TrainingId.of(uuid);


            assertThat(id.value()).isEqualTo(uuid);
            assertThat(id.toString()).isEqualTo(uuid.toString());
        }

        @Test
        @DisplayName("given valid UUID string, when parsing TrainingId, then parse successfully")
        void givenValidUuidString_whenParsingTrainingId_thenParseSuccessfully() {

            final var uuid = TrainingTestFactory.randomUuid();


            final var id = TrainingId.fromString(" %s ".formatted(uuid));


            assertThat(id.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("given null UUID, when creating TrainingId, then throw exception")
        void givenNullUuid_whenCreatingTrainingId_thenThrowException() {





            assertThatThrownBy(() -> new TrainingId(null))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("given invalid UUID string, when parsing TrainingId, then throw exception")
        void givenInvalidUuidString_whenParsingTrainingId_thenThrowException() {

            final var invalidUuid = Instancio.create(String.class);




            assertThatThrownBy(() -> TrainingId.fromString(invalidUuid))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Invalid UUID format");

            assertThatThrownBy(() -> TrainingId.fromString(""))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("cannot be null or blank");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical UUIDs, when comparing TrainingIds, then they are equal")
        void givenIdenticalUuids_whenComparingTrainingIds_thenTheyAreEqual() {

            final var uuid = TrainingTestFactory.randomUuid();
            final var id1 = TrainingId.of(uuid);
            final var id2 = TrainingId.of(uuid);




            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("given different UUIDs, when comparing TrainingIds, then they are not equal")
        void givenDifferentUuids_whenComparingTrainingIds_thenTheyAreNotEqual() {

            final var id1 = TrainingTestFactory.randomTrainingId();
            final var id2 = TrainingTestFactory.randomTrainingId();




            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
