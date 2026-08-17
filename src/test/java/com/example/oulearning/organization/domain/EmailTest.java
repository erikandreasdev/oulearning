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
class EmailTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should create email when valid dynamically generated email provided via InstancioSource")
        void should_createEmail_when_validFormatProvided(
                @Given(DomainGivenProviders.ValidEmailProvider.class) final String rawEmail) {
            // when
            final var email = Email.of(rawEmail);

            // then
            assertThat(email.value()).isEqualTo(rawEmail.strip().toLowerCase());
            assertThat(email.toString()).isEqualTo(rawEmail.strip().toLowerCase());
        }

        @Test
        @DisplayName("should throw InvalidEmailException when email is null")
        void should_throwException_when_emailIsNull() {
            // when / then
            assertThatThrownBy(() -> new Email(null))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should throw InvalidEmailException when email is blank via InstancioSource")
        void should_throwException_when_emailIsBlank(
                @Given(DomainGivenProviders.BlankStringProvider.class) final String blankEmail) {
            // when / then
            assertThatThrownBy(() -> new Email(blankEmail))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should throw InvalidEmailException when format is invalid via InstancioSource")
        void should_throwException_when_formatIsInvalid(
                @Given(DomainGivenProviders.InvalidEmailProvider.class) final String invalidEmail) {
            // when / then
            assertThatThrownBy(() -> Email.of(invalidEmail))
                    .isInstanceOfSatisfying(
                            InvalidEmailException.class,
                            ex -> assertThat(ex.getInvalidValue()).isEqualTo(invalidEmail.strip().toLowerCase()));
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when values are identical after normalization")
        void should_beEqual_when_valuesAreIdenticalAfterNormalization() {
            // given
            final var local = Instancio.gen().string().lowerCase().length(4, 8).get();
            final var domain = Instancio.gen().string().lowerCase().length(4, 8).get();
            final var email1 = Email.of("  %s@%s.com  ".formatted(local.toUpperCase(), domain.toLowerCase()));
            final var email2 = Email.of("%s@%s.com".formatted(local.toLowerCase(), domain.toLowerCase()));

            // then
            assertThat(email1).isEqualTo(email2);
            assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when emails differ")
        void should_notBeEqual_when_emailsDiffer() {
            // given
            final var email1 = DomainGenerators.randomEmail();
            final var email2 = Email.of("different." + email1.value());

            // then
            assertThat(email1).isNotEqualTo(email2);
        }

        @Test
        @DisplayName("should maintain immutability and record semantics")
        void should_maintainImmutability() {
            // given
            final var email = DomainGenerators.randomEmail();

            // then
            assertThat(email.getClass().isRecord()).isTrue();
            assertThat(email.value()).isEqualTo(email.toString());
        }
    }
}
