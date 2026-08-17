package com.example.oulearning.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(InstancioExtension.class)
class SnapshotIdTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("should create SnapshotId from UUID")
        void should_createSnapshotId_from_uuid() {
            final var uuid = UUID.randomUUID();
            final var snapshotId = SnapshotId.of(uuid);

            assertThat(snapshotId.value()).isEqualTo(uuid);
            assertThat(snapshotId.toString()).isEqualTo(uuid.toString());
        }

        @Test
        @DisplayName("should create SnapshotId from valid string")
        void should_createSnapshotId_from_validString() {
            final var uuid = UUID.randomUUID();
            final var snapshotId = SnapshotId.of(uuid.toString());

            assertThat(snapshotId.value()).isEqualTo(uuid);
            assertThat(snapshotId.toString()).isEqualTo(uuid.toString());
        }

        @Test
        @DisplayName("should throw InvalidOrganizationException when UUID is null")
        void should_throwException_when_uuidIsNull() {
            assertThatThrownBy(() -> new SnapshotId(null))
                    .isInstanceOf(InvalidOrganizationException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "\t"})
        @DisplayName("should throw InvalidOrganizationException when string is blank")
        void should_throwException_when_stringIsBlank(String blank) {
            assertThatThrownBy(() -> SnapshotId.of(blank))
                    .isInstanceOf(InvalidOrganizationException.class)
                    .hasMessageContaining("cannot be null or blank");
        }

        @Test
        @DisplayName("should throw InvalidOrganizationException when string is not a valid UUID")
        void should_throwException_when_stringIsNotValidUuid() {
            assertThatThrownBy(() -> SnapshotId.of("invalid-uuid"))
                    .isInstanceOf(InvalidOrganizationException.class)
                    .hasMessageContaining("Invalid UUID format");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when UUIDs match")
        void should_beEqual_when_uuidsMatch() {
            final var uuid = UUID.randomUUID();
            final var id1 = SnapshotId.of(uuid);
            final var id2 = SnapshotId.of(uuid);

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when UUIDs differ")
        void should_notBeEqual_when_uuidsDiffer() {
            final var id1 = SnapshotId.of(UUID.randomUUID());
            final var id2 = SnapshotId.of(UUID.randomUUID());

            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var id = DomainGenerators.randomSnapshotId();
            assertThat(id.getClass().isRecord()).isTrue();
        }
    }
}
