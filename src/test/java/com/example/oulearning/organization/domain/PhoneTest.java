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
class PhoneTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should create phone when valid dynamically generated phone provided via InstancioSource")
        void should_createAndNormalizePhone_when_validFormatProvided(
                @Given(DomainGivenProviders.ValidPhoneProvider.class) final String rawPhone) {
            // when
            final var phone = Phone.of(rawPhone);

            // then
            assertThat(phone.value()).isEqualTo(rawPhone);
            assertThat(phone.toString()).isEqualTo(rawPhone);
        }

        @Test
        @DisplayName("should throw InvalidPhoneException when phone is null")
        void should_throwException_when_phoneIsNull() {
            // when / then
            assertThatThrownBy(() -> new Phone(null))
                    .isInstanceOf(InvalidPhoneException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should throw InvalidPhoneException when phone is blank via InstancioSource")
        void should_throwException_when_phoneIsBlank(
                @Given(DomainGivenProviders.BlankStringProvider.class) final String blankPhone) {
            // when / then
            assertThatThrownBy(() -> new Phone(blankPhone))
                    .isInstanceOf(InvalidPhoneException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should throw InvalidPhoneException when format is invalid via InstancioSource")
        void should_throwException_when_formatIsInvalid(
                @Given(DomainGivenProviders.InvalidPhoneProvider.class) final String invalidPhone) {
            // when / then
            assertThatThrownBy(() -> Phone.of(invalidPhone))
                    .isInstanceOfSatisfying(
                            InvalidPhoneException.class,
                            ex -> assertThat(ex.getInvalidValue()).isNotNull());
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when values are identical after normalization")
        void should_beEqual_when_valuesAreIdenticalAfterNormalization() {
            // given
            final var number = Instancio.gen().longs().range(1000000000L, 9999999999L).get();
            final var phone1 = Phone.of("+ %d".formatted(number));
            final var phone2 = Phone.of("+%d".formatted(number));

            // then
            assertThat(phone1).isEqualTo(phone2);
            assertThat(phone1.hashCode()).isEqualTo(phone2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when phone numbers differ")
        void should_notBeEqual_when_phoneNumbersDiffer() {
            // given
            final var phone1 = DomainGenerators.randomPhone();
            final var phone2 = Phone.of(phone1.value() + "1");

            // then
            assertThat(phone1).isNotEqualTo(phone2);
        }

        @Test
        @DisplayName("should maintain immutability and record semantics")
        void should_maintainImmutability() {
            // given
            final var phone = DomainGenerators.randomPhone();

            // then
            assertThat(phone.getClass().isRecord()).isTrue();
            assertThat(phone.value()).isEqualTo(phone.toString());
        }
    }
}
