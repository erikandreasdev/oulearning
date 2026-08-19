package com.example.oulearning.organization.domain.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class IdTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("should create Id when valid UUID provided")
        void should_createId_when_validUuidProvided() {
            UUID uuid = UUID.randomUUID();
            Id id = Id.of(uuid);

            assertThat(id.value()).isEqualTo(uuid);
            assertThat(id.toString()).isEqualTo(uuid.toString());
        }

        @Test
        @DisplayName("should create Id from valid UUID string")
        void should_createId_from_validUuidString() {
            UUID uuid = UUID.randomUUID();
            Id id = Id.fromString(" " + uuid + " ");

            assertThat(id.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("should throw InvalidOuException when UUID is null")
        void should_throwException_when_uuidIsNull() {
            assertThatThrownBy(() -> new Id(null))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidOuException when UUID string is invalid")
        void should_throwException_when_uuidStringIsInvalid() {
            assertThatThrownBy(() -> Id.fromString("invalid-uuid"))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("Invalid UUID format");

            assertThatThrownBy(() -> Id.fromString(""))
                    .isInstanceOf(InvalidOuException.class)
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
            Id id1 = Id.of(uuid);
            Id id2 = Id.of(uuid);

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when UUIDs differ")
        void should_notBeEqual_when_uuidsDiffer() {
            Id id1 = Id.of(UUID.randomUUID());
            Id id2 = Id.of(UUID.randomUUID());

            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
