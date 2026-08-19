package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PhoneTest {

    @Nested
    @DisplayName("Creation and Normalization")
    class CreationAndNormalization {

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "+1-555-123-4567",
                    "(555) 123-4567",
                    "555.123.4567",
                    "  +34 600 123 456  ",
                    "+123456789012345"
                })
        @DisplayName("given various valid phone formats, when creating Phone, then normalize and create successfully")
        void givenVariousValidPhoneFormats_whenCreatingPhone_thenNormalizeAndCreateSuccessfully(final String rawPhone) {
            // given

            // when
            final var phone = Phone.of(rawPhone);

            // then
            assertThat(phone.value()).matches("^\\+?[0-9]{7,15}$");
        }

        @Test
        @DisplayName("given null phone string, when creating Phone, then throw InvalidTrainingOperationException")
        void givenNullPhoneString_whenCreatingPhone_thenThrowInvalidTrainingOperationException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> new Phone(null))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t\n"})
        @DisplayName("given blank phone string, when creating Phone, then throw InvalidTrainingOperationException")
        void givenBlankPhoneString_whenCreatingPhone_thenThrowInvalidTrainingOperationException(final String blank) {
            // given

            // when

            // then
            assertThatThrownBy(() -> new Phone(blank))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @ParameterizedTest
        @ValueSource(strings = {"12345", "123456", "1234567890123456", "phone-number", "++1234567", "+12345abc"})
        @DisplayName("given invalid phone numbers, when creating Phone, then throw InvalidTrainingOperationException")
        void givenInvalidPhoneNumbers_whenCreatingPhone_thenThrowInvalidTrainingOperationException(final String invalid) {
            // given

            // when

            // then
            assertThatThrownBy(() -> Phone.of(invalid))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("Invalid phone number format");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical phone numbers with different formatting, when comparing, then they are equal")
        void givenIdenticalPhoneNumbersWithDifferentFormatting_whenComparing_thenTheyAreEqual() {
            // given
            final var d1 = TrainingTestFactory.randomPhoneDigits().substring(0, 3);
            final var d2 = TrainingTestFactory.randomPhoneDigits().substring(0, 3);
            final var d3 = TrainingTestFactory.randomPhoneDigits().substring(0, 4);

            final var p1 = Phone.of("+1 (%s) %s-%s".formatted(d1, d2, d3));
            final var p2 = Phone.of("+1.%s.%s.%s".formatted(d1, d2, d3));

            // when

            // then
            assertThat(p1).isEqualTo(p2);
            assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
        }

        @Test
        @DisplayName("given different phone numbers, when comparing, then they are not equal")
        void givenDifferentPhoneNumbers_whenComparing_thenTheyAreNotEqual() {
            // given
            final var p1 = TrainingTestFactory.randomPhone();
            final var p2 = TrainingTestFactory.randomPhone();

            // when

            // then
            assertThat(p1).isNotEqualTo(p2);
        }
    }
}
