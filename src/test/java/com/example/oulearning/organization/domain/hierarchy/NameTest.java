package com.example.oulearning.organization.domain.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.hierarchy.exception.InvalidOuException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class NameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid string, when creating Name, then create successfully")
        void givenValidString_whenCreatingName_thenCreateSuccessfully() {
            // given
            final var raw = HierarchyTestFactory.randomOuNameString();

            // when
            final var name = Name.of(raw);

            // then
            assertThat(name.value()).isEqualTo(raw);
            assertThat(name).hasToString(raw);
        }

        @Test
        @DisplayName("given padded string, when creating Name, then trim and create successfully")
        void givenPaddedString_whenCreatingName_thenTrimAndCreateSuccessfully() {
            // given
            final var raw = HierarchyTestFactory.randomOuNameString();

            // when
            final var name = Name.of("  %s  ".formatted(raw));

            // then
            assertThat(name.value()).isEqualTo(raw);
        }

        @Test
        @DisplayName("given null name string, when creating Name, then throw InvalidOuException")
        void givenNullNameString_whenCreatingName_thenThrowInvalidOuException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> new Name(null))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("given blank name string, when creating Name, then throw InvalidOuException")
        void givenBlankNameString_whenCreatingName_thenThrowInvalidOuException() {
            // given
            final var blank = " ".repeat(Instancio.gen().ints().range(1, 5).get());

            // when

            // then
            assertThatThrownBy(() -> new Name(blank))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @Test
        @DisplayName("given name exceeding max length, when creating Name, then throw InvalidOuException")
        void givenNameExceedingMaxLength_whenCreatingName_thenThrowInvalidOuException() {
            // given
            final var longName = "A".repeat(HierarchyConstants.MAX_NAME_LENGTH + 1);

            // when

            // then
            assertThatThrownBy(() -> new Name(longName))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("Ou name length must be between");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical names, when comparing Name, then they are equal")
        void givenIdenticalNames_whenComparingName_thenTheyAreEqual() {
            // given
            final var raw = HierarchyTestFactory.randomOuNameString();
            final var n1 = Name.of(raw);
            final var n2 = Name.of(raw);

            // when

            // then
            assertThat(n1).isEqualTo(n2).hasSameHashCodeAs(n2);
        }

        @Test
        @DisplayName("given different names, when comparing Name, then they are not equal")
        void givenDifferentNames_whenComparingName_thenTheyAreNotEqual() {
            // given
            final var n1 = HierarchyTestFactory.randomName();
            final var n2 = HierarchyTestFactory.randomName();

            // when

            // then
            assertThat(n1).isNotEqualTo(n2);
        }
    }
}
