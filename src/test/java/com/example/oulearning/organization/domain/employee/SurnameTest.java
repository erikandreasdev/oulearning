package com.example.oulearning.organization.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.DomainGenerators;
import com.example.oulearning.organization.domain.DomainGivenProviders;
import com.example.oulearning.organization.domain.employee.exception.InvalidSurnameException;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@ExtendWith(InstancioExtension.class)
class SurnameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest(name = "should reject: {0}")
        @ArgumentsSource(DomainGivenProviders.InvalidNames.class)
        @DisplayName("should throw InvalidSurnameException for invalid surname")
        void should_throwException_when_surnameIsInvalid(String invalidSurname) {
            assertThatThrownBy(() -> Surname.of(invalidSurname))
                    .isInstanceOf(InvalidSurnameException.class);
        }

        @Test
        @DisplayName("should throw InvalidSurnameException when value is null")
        void should_throwException_when_valueIsNull() {
            assertThatThrownBy(() -> Surname.of(null))
                    .isInstanceOf(InvalidSurnameException.class)
                    .hasMessageContaining("Surname cannot be null or blank");
        }

        @ParameterizedTest(name = "should normalize {0} to {1}")
        @ArgumentsSource(DomainGivenProviders.ValidNames.class)
        @DisplayName("should create and trim Surname for valid input")
        void should_createAndTrimSurname_when_inputIsValid(String input, String expected) {
            final var surname = Surname.of(input);
            assertThat(surname.value()).isEqualTo(expected);
            assertThat(surname.toString()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when values match")
        void should_beEqual_when_valuesMatch() {
            final var s1 = Surname.of("Doe");
            final var s2 = Surname.of("Doe");

            assertThat(s1).isEqualTo(s2);
            assertThat(s1.hashCode()).isEqualTo(s2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when values differ")
        void should_notBeEqual_when_valuesDiffer() {
            final var s1 = Surname.of("Doe");
            final var s2 = Surname.of("Smith");

            assertThat(s1).isNotEqualTo(s2);
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var surname = DomainGenerators.randomSurname();
            assertThat(surname.getClass().isRecord()).isTrue();
        }
    }
}
