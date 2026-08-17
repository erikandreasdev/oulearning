package com.example.oulearning.organization.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.DomainGenerators;
import com.example.oulearning.organization.domain.DomainGivenProviders;
import com.example.oulearning.organization.domain.employee.exception.InvalidNameException;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@ExtendWith(InstancioExtension.class)
class NameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest(name = "should reject: {0}")
        @ArgumentsSource(DomainGivenProviders.InvalidNames.class)
        @DisplayName("should throw InvalidNameException for invalid name")
        void should_throwException_when_nameIsInvalid(String invalidName) {
            assertThatThrownBy(() -> Name.of(invalidName))
                    .isInstanceOf(InvalidNameException.class);
        }

        @Test
        @DisplayName("should throw InvalidNameException when value is null")
        void should_throwException_when_valueIsNull() {
            assertThatThrownBy(() -> Name.of(null))
                    .isInstanceOf(InvalidNameException.class)
                    .hasMessageContaining("Name cannot be null or blank");
        }

        @ParameterizedTest(name = "should normalize {0} to {1}")
        @ArgumentsSource(DomainGivenProviders.ValidNames.class)
        @DisplayName("should create and trim Name for valid input")
        void should_createAndTrimName_when_inputIsValid(String input, String expected) {
            final var name = Name.of(input);
            assertThat(name.value()).isEqualTo(expected);
            assertThat(name.toString()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when values match")
        void should_beEqual_when_valuesMatch() {
            final var n1 = Name.of("John");
            final var n2 = Name.of("John");

            assertThat(n1).isEqualTo(n2);
            assertThat(n1.hashCode()).isEqualTo(n2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when values differ")
        void should_notBeEqual_when_valuesDiffer() {
            final var n1 = Name.of("John");
            final var n2 = Name.of("Jane");

            assertThat(n1).isNotEqualTo(n2);
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var name = DomainGenerators.randomName();
            assertThat(name.getClass().isRecord()).isTrue();
        }
    }
}
