package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PhoneTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "+1234567890",
                    "1234567890",
                    "+44 20 7946 0958",
                    "(555) 123-4567",
                    "  +1-800-555-0199  ",
                    "+49.30.123456"
                })
        @DisplayName("should create phone when valid format provided")
        void should_createPhone_when_validFormatProvided(String raw) {
            Phone phone = Phone.of(raw);

            String expected = raw.strip().replaceAll("[\\s\\-\\(\\)\\.]", "");
            assertThat(phone.value()).isEqualTo(expected);
            assertThat(phone.toString()).isEqualTo(expected);
        }

        @Test
        @DisplayName("should throw InvalidTrainingOperationException when phone is null")
        void should_throwException_when_phoneIsNull() {
            assertThatThrownBy(() -> new Phone(null))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   ", "\t\n"})
        @DisplayName("should throw InvalidTrainingOperationException when phone is blank")
        void should_throwException_when_phoneIsBlank(String blank) {
            assertThatThrownBy(() -> new Phone(blank))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "not-a-phone",
                    "12345", // too short (< 7 digits)
                    "+12345678901234567", // too long (> 15 digits)
                    "+012345678", // starts with 0 after +
                    "++123456789"
                })
        @DisplayName("should throw InvalidTrainingOperationException when format is invalid")
        void should_throwException_when_formatIsInvalid(String invalid) {
            assertThatThrownBy(() -> Phone.of(invalid))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("Invalid phone number format");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("should be equal when phone numbers are identical after normalization")
        void should_beEqual_when_numbersIdenticalAfterNormalization() {
            Phone p1 = Phone.of("+1 (555) 123-4567");
            Phone p2 = Phone.of("+15551234567");

            assertThat(p1).isEqualTo(p2);
            assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when numbers differ")
        void should_notBeEqual_when_numbersDiffer() {
            Phone p1 = Phone.of("+15551234567");
            Phone p2 = Phone.of("+15559876543");

            assertThat(p1).isNotEqualTo(p2);
        }
    }
}
