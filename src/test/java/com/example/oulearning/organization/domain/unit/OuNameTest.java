package com.example.oulearning.organization.domain.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.DomainGenerators;
import com.example.oulearning.organization.domain.DomainGivenProviders;
import com.example.oulearning.organization.domain.unit.exception.InvalidOuException;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@ExtendWith(InstancioExtension.class)
class OuNameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest(name = "should reject: {0}")
        @ArgumentsSource(DomainGivenProviders.InvalidOuNames.class)
        @DisplayName("should throw InvalidOuException for invalid OU name")
        void should_throwException_when_nameIsInvalid(String invalidName) {
            assertThatThrownBy(() -> OuName.of(invalidName))
                    .isInstanceOf(InvalidOuException.class);
        }

        @Test
        @DisplayName("should throw InvalidOuException when value is null")
        void should_throwException_when_valueIsNull() {
            assertThatThrownBy(() -> OuName.of(null))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("OuName cannot be null or blank");
        }

        @ParameterizedTest(name = "should normalize {0} to {1}")
        @ArgumentsSource(DomainGivenProviders.ValidOuNames.class)
        @DisplayName("should create and trim OuName for valid input")
        void should_createAndTrimOuName_when_inputIsValid(String input, String expected) {
            final var name = OuName.of(input);
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
            final var n1 = OuName.of("Engineering Area");
            final var n2 = OuName.of("Engineering Area");

            assertThat(n1).isEqualTo(n2);
            assertThat(n1.hashCode()).isEqualTo(n2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when values differ")
        void should_notBeEqual_when_valuesDiffer() {
            final var n1 = OuName.of("Engineering Area");
            final var n2 = OuName.of("Sales Area");

            assertThat(n1).isNotEqualTo(n2);
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var name = DomainGenerators.randomOuName();
            assertThat(name.getClass().isRecord()).isTrue();
        }
    }
}
