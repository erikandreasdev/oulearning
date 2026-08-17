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
class OuNameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should create OuName when valid dynamically generated name provided via InstancioSource")
        void should_createOuName_when_validNameProvided(
                @Given(DomainGivenProviders.ValidOuNameProvider.class) final String rawName) {
            final var ouName = OuName.of(rawName);

            assertThat(ouName.value()).isEqualTo(rawName.strip());
            assertThat(ouName.toString()).isEqualTo(rawName.strip());
        }

        @Test
        @DisplayName("should normalize multiple consecutive whitespace characters")
        void should_normalizeWhitespace() {
            final var ouName = OuName.of("  Engineering   Department  ");
            assertThat(ouName.value()).isEqualTo("Engineering Department");
        }

        @Test
        @DisplayName("should throw InvalidOuException when name is null")
        void should_throwException_when_nameIsNull() {
            assertThatThrownBy(() -> new OuName(null))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should throw InvalidOuException when name is blank via InstancioSource")
        void should_throwException_when_nameIsBlank(
                @Given(DomainGivenProviders.BlankStringProvider.class) final String blankName) {
            assertThatThrownBy(() -> new OuName(blankName))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @Test
        @DisplayName("should throw InvalidOuException when name exceeds 100 characters")
        void should_throwException_when_nameExceeds100Characters() {
            final var longName = Instancio.gen().string().mixedCase().length(101, 120).get();
            assertThatThrownBy(() -> new OuName(longName))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("cannot exceed 100 characters");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when names match after normalization")
        void should_beEqual_when_namesMatchAfterNormalization() {
            final var name1 = OuName.of("  Sales   North  ");
            final var name2 = OuName.of("Sales North");

            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when names differ")
        void should_notBeEqual_when_namesDiffer() {
            final var name1 = OuName.of("Sales");
            final var name2 = OuName.of("Marketing");

            assertThat(name1).isNotEqualTo(name2);
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var name = OuName.of("IT Operations");
            assertThat(name.getClass().isRecord()).isTrue();
        }
    }
}
