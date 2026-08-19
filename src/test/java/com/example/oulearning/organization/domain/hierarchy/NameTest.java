package com.example.oulearning.organization.domain.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.hierarchy.exception.InvalidOuException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid string, when creating Name, then create successfully")
        void givenValidString_whenCreatingName_thenCreateSuccessfully() {

            final var raw = HierarchyTestFactory.randomOuNameString();


            final var name = Name.of(raw);


            assertThat(name.value()).isEqualTo(raw);
            assertThat(name.toString()).isEqualTo(raw);
        }

        @Test
        @DisplayName("given padded string, when creating Name, then trim and create successfully")
        void givenPaddedString_whenCreatingName_thenTrimAndCreateSuccessfully() {

            final var raw = HierarchyTestFactory.randomOuNameString();


            final var name = Name.of("  %s  ".formatted(raw));


            assertThat(name.value()).isEqualTo(raw);
        }

        @Test
        @DisplayName("given null name string, when creating Name, then throw InvalidOuException")
        void givenNullNameString_whenCreatingName_thenThrowInvalidOuException() {





            assertThatThrownBy(() -> new Name(null))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t\n"})
        @DisplayName("given blank name string, when creating Name, then throw InvalidOuException")
        void givenBlankNameString_whenCreatingName_thenThrowInvalidOuException(final String blank) {





            assertThatThrownBy(() -> new Name(blank))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @Test
        @DisplayName("given name exceeding max length, when creating Name, then throw InvalidOuException")
        void givenNameExceedingMaxLength_whenCreatingName_thenThrowInvalidOuException() {

            final var longName = "A".repeat(HierarchyConstants.MAX_NAME_LENGTH + 1);




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

            final var raw = HierarchyTestFactory.randomOuNameString();
            final var n1 = Name.of(raw);
            final var n2 = Name.of(raw);




            assertThat(n1).isEqualTo(n2);
            assertThat(n1.hashCode()).isEqualTo(n2.hashCode());
        }

        @Test
        @DisplayName("given different names, when comparing Name, then they are not equal")
        void givenDifferentNames_whenComparingName_thenTheyAreNotEqual() {

            final var n1 = HierarchyTestFactory.randomName();
            final var n2 = HierarchyTestFactory.randomName();




            assertThat(n1).isNotEqualTo(n2);
        }
    }
}
