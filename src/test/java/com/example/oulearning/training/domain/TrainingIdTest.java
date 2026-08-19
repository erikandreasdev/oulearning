package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TrainingIdTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("should create TrainingId when valid UUID provided")
        void should_createId_when_validUuidProvided() {
            UUID uuid = UUID.randomUUID();
            TrainingId id = TrainingId.of(uuid);

            assertThat(id.value()).isEqualTo(uuid);
            assertThat(id.toString()).isEqualTo(uuid.toString());
        }

        @Test
        @DisplayName("should create TrainingId from valid UUID string")
        void should_createId_from_validUuidString() {
            UUID uuid = UUID.randomUUID();
            TrainingId id = TrainingId.fromString(uuid.toString());

            assertThat(id.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("should throw InvalidTrainingOperationException when UUID is null")
        void should_throwException_when_uuidIsNull() {
            assertThatThrownBy(() -> new TrainingId(null))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidTrainingOperationException when UUID string is invalid")
        void should_throwException_when_uuidStringIsInvalid() {
            assertThatThrownBy(() -> TrainingId.fromString("invalid-uuid"))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("Invalid UUID format");

            assertThatThrownBy(() -> TrainingId.fromString(""))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null or blank");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("should be equal when UUIDs match")
        void should_beEqual_when_uuidsMatch() {
            UUID uuid = UUID.randomUUID();
            TrainingId id1 = TrainingId.of(uuid);
            TrainingId id2 = TrainingId.of(uuid);

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when UUIDs differ")
        void should_notBeEqual_when_uuidsDiffer() {
            TrainingId id1 = TrainingId.of(UUID.randomUUID());
            TrainingId id2 = TrainingId.of(UUID.randomUUID());

            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
