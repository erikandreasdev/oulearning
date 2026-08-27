package com.example.oulearning.training.domain.model;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PhoneTest {

    @Nested
    @DisplayName("Creation and Normalization")
    class CreationAndNormalization {

        @Test
        @DisplayName("given valid phone format from factory, when creating Phone, then normalize and create successfully")
        void givenValidPhoneFormat_whenCreatingPhone_thenNormalizeAndCreateSuccessfully() {
            // given
            final var rawPhone = TrainingTestFactory.randomPhoneString();

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

        @Test
        @DisplayName("given blank phone string, when creating Phone, then throw InvalidTrainingOperationException")
        void givenBlankPhoneString_whenCreatingPhone_thenThrowInvalidTrainingOperationException() {
            // given
            final var blank = " ".repeat(Instancio.gen().ints().range(1, 5).get());

            // when

            // then
            assertThatThrownBy(() -> new Phone(blank))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @Test
        @DisplayName("given invalid non-numeric phone, when creating Phone, then throw InvalidTrainingOperationException")
        void givenInvalidNonNumericPhone_whenCreatingPhone_thenThrowInvalidTrainingOperationException() {
            // given
            final var invalid = Instancio.gen().string().alphaNumeric().length(5, 10).get() + "@";

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
            assertThat(p1).isEqualTo(p2).hasSameHashCodeAs(p2);
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
