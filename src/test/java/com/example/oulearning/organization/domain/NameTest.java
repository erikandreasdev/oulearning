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
class NameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should create name when valid dynamically generated name provided via InstancioSource")
        void should_createName_when_generatedAlphabeticNameProvided(
                @Given(DomainGivenProviders.ValidNameProvider.class) final String generatedName) {
            // when
            final var name = Name.of(generatedName);

            // then
            assertThat(name.value()).isEqualTo(generatedName);
            assertThat(name.toString()).isEqualTo(generatedName);
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
        @InstancioSource(samples = 5)
        @DisplayName("should throw InvalidNameException when name is blank via InstancioSource")
        void should_throwException_when_nameIsBlank(
                @Given(DomainGivenProviders.BlankStringProvider.class) final String blankName) {
            // when / then
            assertThatThrownBy(() -> new Name(blankName))
                    .isInstanceOf(InvalidNameException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should throw InvalidNameException when format is invalid via InstancioSource")
        void should_throwException_when_formatIsInvalid(
                @Given(DomainGivenProviders.InvalidNameProvider.class) final String invalidName) {
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
            final var longName = Instancio.gen().string().mixedCase().length(101, 120).get();

            // when / then
            assertThatThrownBy(() -> Name.of(longName))
                    .isInstanceOf(InvalidNameException.class)
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
            final var name1 = Name.of("  %s  ".formatted(base));
            final var name2 = Name.of(base);

            // then
            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when names differ")
        void should_notBeEqual_when_namesDiffer() {
            // given
            final var name1 = DomainGenerators.randomName();
            final var name2 = Name.of(name1.value() + "Z");

            // then
            assertThat(name1).isNotEqualTo(name2);
        }

        @Test
        @DisplayName("should maintain immutability and record semantics")
        void should_maintainImmutability() {
            // given
            final var name = DomainGenerators.randomName();

            // then
            assertThat(name.getClass().isRecord()).isTrue();
            assertThat(name.value()).isEqualTo(name.toString());
        }
    }
}
