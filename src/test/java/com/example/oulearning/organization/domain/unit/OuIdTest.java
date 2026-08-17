package com.example.oulearning.organization.domain.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.unit.exception.InvalidOuIdException;
import java.util.UUID;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(InstancioExtension.class)
class OuIdTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("should create OuId from UUID")
        void should_createOuId_from_uuid() {
            final var uuid = UUID.randomUUID();
            final var ouId = OuId.of(uuid);

            assertThat(ouId.value()).isEqualTo(uuid);
            assertThat(ouId.toString()).isEqualTo(uuid.toString());
        }

        @Test
        @DisplayName("should create OuId from valid string")
        void should_createOuId_from_validString() {
            final var uuid = UUID.randomUUID();
            final var ouId = OuId.of(uuid.toString());

            assertThat(ouId.value()).isEqualTo(uuid);
            assertThat(ouId.toString()).isEqualTo(uuid.toString());
        }

        @Test
        @DisplayName("should throw InvalidOuIdException when UUID is null")
        void should_throwException_when_uuidIsNull() {
            assertThatThrownBy(() -> new OuId(null))
                    .isInstanceOf(InvalidOuIdException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "\t"})
        @DisplayName("should throw InvalidOuIdException when string is blank")
        void should_throwException_when_stringIsBlank(String blank) {
            assertThatThrownBy(() -> OuId.of(blank))
                    .isInstanceOf(InvalidOuIdException.class)
                    .hasMessageContaining("cannot be null or blank");
        }

        @Test
        @DisplayName("should throw InvalidOuIdException when string is not a valid UUID")
        void should_throwException_when_stringIsNotValidUuid() {
            assertThatThrownBy(() -> OuId.of("not-a-valid-uuid"))
                    .isInstanceOf(InvalidOuIdException.class)
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
            final var id1 = OuId.of(uuid);
            final var id2 = OuId.of(uuid);

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when UUIDs differ")
        void should_notBeEqual_when_uuidsDiffer() {
            final var id1 = OuId.of(UUID.randomUUID());
            final var id2 = OuId.of(UUID.randomUUID());

            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var id = OuId.of(UUID.randomUUID());
            assertThat(id.getClass().isRecord()).isTrue();
        }
    }
}
