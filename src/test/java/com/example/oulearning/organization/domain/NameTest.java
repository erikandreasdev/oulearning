package com.example.oulearning.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class NameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest
        @CsvSource({
            "'John', 'John'",
            "'  Mary-Jane  ', 'Mary-Jane'",
            "'Jean-Luc', 'Jean-Luc'",
            "'José', 'José'",
            "'Renée', 'Renée'",
            "'Anne Marie', 'Anne Marie'",
            "'Anne   Marie', 'Anne Marie'",
            "'O''Connor', 'O''Connor'"
        })
        @DisplayName("should create and normalize name when valid name provided")
        void should_createAndNormalizeName_when_validNameProvided(String input, String expectedNormalized) {
            // when
            Name name = Name.of(input);

            // then
            assertThat(name.value()).isEqualTo(expectedNormalized);
            assertThat(name.toString()).isEqualTo(expectedNormalized);
        }

        @Test
        @DisplayName("should throw InvalidNameException when name is null")
        void should_throwException_when_nameIsNull() {
            // when / then
            assertThatThrownBy(() -> new Name(null))
                    .isInstanceOf(InvalidNameException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   ", "\t\n"})
        @DisplayName("should throw InvalidNameException when name is blank")
        void should_throwException_when_nameIsBlank(String blankName) {
            // when / then
            assertThatThrownBy(() -> new Name(blankName))
                    .isInstanceOf(InvalidNameException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "John123",
                    "Mary_Jane",
                    "John@Doe",
                    "John!",
                    "-John",
                    "John-",
                    "'John",
                    "John'"
                })
        @DisplayName("should throw InvalidNameException when format is invalid")
        void should_throwException_when_formatIsInvalid(String invalidName) {
            // when / then
            assertThatThrownBy(() -> Name.of(invalidName))
                    .isInstanceOfSatisfying(
                            InvalidNameException.class,
                            ex -> assertThat(ex.getInvalidValue()).isNotNull());
        }

        @Test
        @DisplayName("should throw InvalidNameException when name exceeds max length")
        void should_throwException_when_nameExceedsMaxLength() {
            // given
            String longName = "A".repeat(101);

            // when / then
            assertThatThrownBy(() -> Name.of(longName))
                    .isInstanceOf(InvalidNameException.class)
                    .hasMessageContaining("cannot exceed 100 characters");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("should be equal when values are identical after normalization")
        void should_beEqual_when_valuesAreIdenticalAfterNormalization() {
            // given
            Name name1 = Name.of("  John   Paul  ");
            Name name2 = Name.of("John Paul");

            // then
            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when names differ")
        void should_notBeEqual_when_namesDiffer() {
            // given
            Name name1 = Name.of("John");
            Name name2 = Name.of("Jane");

            // then
            assertThat(name1).isNotEqualTo(name2);
        }
    }
}
