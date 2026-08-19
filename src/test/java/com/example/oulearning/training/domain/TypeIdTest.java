package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TypeIdTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("should create TypeId when valid UUID provided")
        void should_createTypeId_when_validUuidProvided() {
            UUID uuid = UUID.randomUUID();
            TypeId id = TypeId.of(uuid);

            assertThat(id.value()).isEqualTo(uuid);
            assertThat(id.toString()).isEqualTo(uuid.toString());
        }

        @Test
        @DisplayName("should create TypeId from valid string")
        void should_createTypeId_fromValidString() {
            UUID uuid = UUID.randomUUID();
            TypeId id = TypeId.fromString(uuid.toString());

            assertThat(id.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("should throw InvalidTrainingOperationException when UUID is null")
        void should_throwException_when_uuidIsNull() {
            assertThatThrownBy(() -> new TypeId(null))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidTrainingOperationException when string format invalid")
        void should_throwException_when_stringFormatInvalid() {
            assertThatThrownBy(() -> TypeId.fromString("not-uuid"))
                    .isInstanceOf(InvalidTrainingOperationException.class);
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("should be equal when UUIDs match")
        void should_beEqual_when_uuidsMatch() {
            UUID uuid = UUID.randomUUID();
            TypeId id1 = TypeId.of(uuid);
            TypeId id2 = TypeId.of(uuid);

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when UUIDs differ")
        void should_notBeEqual_when_uuidsDiffer() {
            TypeId id1 = TypeId.of(UUID.randomUUID());
            TypeId id2 = TypeId.of(UUID.randomUUID());

            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
