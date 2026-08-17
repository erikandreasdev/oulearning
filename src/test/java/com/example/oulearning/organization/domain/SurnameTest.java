package com.example.oulearning.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.instancio.Instancio;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

@ExtendWith(InstancioExtension.class)
class SurnameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should create surname when valid dynamically generated surname provided via InstancioSource")
        void should_createSurname_when_generatedAlphabeticSurnameProvided(
                @Given(DomainGivenProviders.ValidSurnameProvider.class) final String generatedSurname) {
            // when
            final var surname = Surname.of(generatedSurname);

            // then
            assertThat(surname.value()).isEqualTo(generatedSurname);
            assertThat(surname.toString()).isEqualTo(generatedSurname);
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
        @InstancioSource(samples = 5)
        @DisplayName("should throw InvalidSurnameException when surname is blank via InstancioSource")
        void should_throwException_when_surnameIsBlank(
                @Given(DomainGivenProviders.BlankStringProvider.class) final String blankSurname) {
            // when / then
            assertThatThrownBy(() -> new Surname(blankSurname))
                    .isInstanceOf(InvalidSurnameException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should throw InvalidSurnameException when format is invalid via InstancioSource")
        void should_throwException_when_formatIsInvalid(
                @Given(DomainGivenProviders.InvalidSurnameProvider.class) final String invalidSurname) {
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
            final var longSurname = Instancio.gen().string().mixedCase().length(101, 120).get();

            // when / then
            assertThatThrownBy(() -> Surname.of(longSurname))
                    .isInstanceOf(InvalidSurnameException.class)
                    .hasMessageContaining("cannot exceed 100 characters");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when values are identical after normalization")
        void should_beEqual_when_valuesAreIdenticalAfterNormalization() {
            // given
            final var base = Instancio.gen().string().mixedCase().length(5, 10).get();
            final var surname1 = Surname.of("  %s  ".formatted(base));
            final var surname2 = Surname.of(base);

            // then
            assertThat(surname1).isEqualTo(surname2);
            assertThat(surname1.hashCode()).isEqualTo(surname2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when surnames differ")
        void should_notBeEqual_when_surnamesDiffer() {
            // given
            final var surname1 = DomainGenerators.randomSurname();
            final var surname2 = Surname.of(surname1.value() + "Z");

            // then
            assertThat(surname1).isNotEqualTo(surname2);
        }

        @Test
        @DisplayName("should maintain immutability and record semantics")
        void should_maintainImmutability() {
            // given
            final var surname = DomainGenerators.randomSurname();

            // then
            assertThat(surname.getClass().isRecord()).isTrue();
            assertThat(surname.value()).isEqualTo(surname.toString());
        }
    }
}
