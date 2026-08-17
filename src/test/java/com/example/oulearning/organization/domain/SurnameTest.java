package com.example.oulearning.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class SurnameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest
        @CsvSource({
            "'Smith', 'Smith'",
            "'  Van der Bilt  ', 'Van der Bilt'",
            "'García Márquez', 'García Márquez'",
            "'O''Neill', 'O''Neill'",
            "'Saint-Germain', 'Saint-Germain'",
            "'Müller', 'Müller'"
        })
        @DisplayName("should create and normalize surname when valid surname provided")
        void should_createAndNormalizeSurname_when_validSurnameProvided(
                String input, String expectedNormalized) {
            // when
            Surname surname = Surname.of(input);

            // then
            assertThat(surname.value()).isEqualTo(expectedNormalized);
            assertThat(surname.toString()).isEqualTo(expectedNormalized);
        }

        @Test
        @DisplayName("should throw InvalidSurnameException when surname is null")
        void should_throwException_when_surnameIsNull() {
            // when / then
            assertThatThrownBy(() -> new Surname(null))
                    .isInstanceOf(InvalidSurnameException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   ", "\t\n"})
        @DisplayName("should throw InvalidSurnameException when surname is blank")
        void should_throwException_when_surnameIsBlank(String blankSurname) {
            // when / then
            assertThatThrownBy(() -> new Surname(blankSurname))
                    .isInstanceOf(InvalidSurnameException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "Smith123",
                    "Van_der_Bilt",
                    "Smith@Doe",
                    "Smith!",
                    "-Smith",
                    "Smith-",
                    "'Smith",
                    "Smith'"
                })
        @DisplayName("should throw InvalidSurnameException when format is invalid")
        void should_throwException_when_formatIsInvalid(String invalidSurname) {
            // when / then
            assertThatThrownBy(() -> Surname.of(invalidSurname))
                    .isInstanceOfSatisfying(
                            InvalidSurnameException.class,
                            ex -> assertThat(ex.getInvalidValue()).isNotNull());
        }

        @Test
        @DisplayName("should throw InvalidSurnameException when surname exceeds max length")
        void should_throwException_when_surnameExceedsMaxLength() {
            // given
            String longSurname = "B".repeat(101);

            // when / then
            assertThatThrownBy(() -> Surname.of(longSurname))
                    .isInstanceOf(InvalidSurnameException.class)
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
            Surname surname1 = Surname.of("  García   Márquez  ");
            Surname surname2 = Surname.of("García Márquez");

            // then
            assertThat(surname1).isEqualTo(surname2);
            assertThat(surname1.hashCode()).isEqualTo(surname2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when surnames differ")
        void should_notBeEqual_when_surnamesDiffer() {
            // given
            Surname surname1 = Surname.of("Smith");
            Surname surname2 = Surname.of("Jones");

            // then
            assertThat(surname1).isNotEqualTo(surname2);
        }
    }
}
