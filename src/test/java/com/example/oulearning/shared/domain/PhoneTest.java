package com.example.oulearning.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class PhoneTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest
        @CsvSource({
            "'+1 (555) 123-4567', '+15551234567'",
            "'+44 20 7183 8750', '+442071838750'",
            "'15551234567', '15551234567'",
            "'555-1234', '5551234'",
            "'  +34.912.345.678  ', '+34912345678'",
            "'+123456789012345', '+123456789012345'"
        })
        @DisplayName("should create and normalize phone when valid format provided")
        void should_createAndNormalizePhone_when_validFormatProvided(String input, String expectedNormalized) {
            // when
            Phone phone = Phone.of(input);

            // then
            assertThat(phone.value()).isEqualTo(expectedNormalized);
            assertThat(phone.toString()).isEqualTo(expectedNormalized);
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
        @ValueSource(strings = {"", " ", "   ", "\t\n"})
        @DisplayName("should throw InvalidPhoneException when phone is blank")
        void should_throwException_when_phoneIsBlank(String blankPhone) {
            // when / then
            assertThatThrownBy(() -> new Phone(blankPhone))
                    .isInstanceOf(InvalidPhoneException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "123456", // 6 digits - too short (<7)
                    "+1234567890123456", // 16 digits - too long (>15)
                    "+0123456789", // leading zero after +
                    "0123456", // leading zero
                    "1-800-FLOWERS", // letters
                    "+1 555 ABC-DEFG", // letters
                    "++15551234567", // double +
                    "+1-555-123-456@" // invalid symbols
                })
        @DisplayName("should throw InvalidPhoneException when format is invalid")
        void should_throwException_when_formatIsInvalid(String invalidPhone) {
            // when / then
            assertThatThrownBy(() -> Phone.of(invalidPhone))
                    .isInstanceOfSatisfying(
                            InvalidPhoneException.class,
                            ex -> assertThat(ex.getInvalidValue()).isNotNull());
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("should be equal when values are identical after normalization")
        void should_beEqual_when_valuesAreIdenticalAfterNormalization() {
            // given
            Phone phone1 = Phone.of("+1 (555) 123-4567");
            Phone phone2 = Phone.of("+15551234567");

            // then
            assertThat(phone1).isEqualTo(phone2);
            assertThat(phone1.hashCode()).isEqualTo(phone2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when phone numbers differ")
        void should_notBeEqual_when_phoneNumbersDiffer() {
            // given
            Phone phone1 = Phone.of("+15551234567");
            Phone phone2 = Phone.of("+15559876543");

            // then
            assertThat(phone1).isNotEqualTo(phone2);
        }
    }
}
