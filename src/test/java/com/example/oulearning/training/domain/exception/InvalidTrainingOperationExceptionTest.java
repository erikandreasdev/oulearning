package com.example.oulearning.training.domain.exception;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.domain.model.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("InvalidTrainingOperationException Unit Tests")
final class InvalidTrainingOperationExceptionTest {

    @Nested
    @DisplayName("Constructors and Factory Methods")
    final class FactoryMethods {

        @Test
        @DisplayName("given message and cause, when creating, then retain both")
        void givenMessageAndCause_whenCreating_thenRetainBoth() {
            // given
            final var message = "custom error";
            final var cause = new RuntimeException("root cause");

            // when
            final var exception = new InvalidTrainingOperationException(message, cause);

            // then
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception.getCause()).isSameAs(cause);
        }

        @Test
        @DisplayName("given fieldName, when calling nullField, then produce expected message")
        void givenFieldName_whenCallingNullField_thenProduceExpectedMessage() {
            // given
            final var fieldName = "Training id";

            // when
            final var exception = InvalidTrainingOperationException.nullField(fieldName);

            // then
            assertThat(exception.getMessage()).isEqualTo("Training id cannot be null");
        }

        @Test
        @DisplayName("given fieldName, when calling blankField, then produce expected message")
        void givenFieldName_whenCallingBlankField_thenProduceExpectedMessage() {
            // given
            final var fieldName = "Training name";

            // when
            final var exception = InvalidTrainingOperationException.blankField(fieldName);

            // then
            assertThat(exception.getMessage()).isEqualTo("Training name cannot be blank");
        }

        @Test
        @DisplayName("given fieldName, when calling nullOrBlank, then produce expected message")
        void givenFieldName_whenCallingNullOrBlank_thenProduceExpectedMessage() {
            // given
            final var fieldName = "Training id";

            // when
            final var exception = InvalidTrainingOperationException.nullOrBlank(fieldName);

            // then
            assertThat(exception.getMessage()).isEqualTo("Training id string cannot be null or blank");
        }

        @Test
        @DisplayName("given range bounds, when calling lengthOutOfRange, then produce expected message")
        void givenRangeBounds_whenCallingLengthOutOfRange_thenProduceExpectedMessage() {
            // given
            final var fieldName = "Training name";
            final var min = 2;
            final var max = 150;
            final var actual = "A";

            // when
            final var exception =
                    InvalidTrainingOperationException.lengthOutOfRange(fieldName, min, max, actual);

            // then
            assertThat(exception.getMessage())
                    .isEqualTo("Training name length must be between 2 and 150 characters: A");
        }

        @Test
        @DisplayName("given non-positive id, when calling nonPositiveId, then produce expected message")
        void givenNonPositiveId_whenCallingNonPositiveId_thenProduceExpectedMessage() {
            // given
            final var fieldName = "Training id";
            final var value = 0L;

            // when
            final var exception = InvalidTrainingOperationException.nonPositiveId(fieldName, value);

            // then
            assertThat(exception.getMessage())
                    .isEqualTo("Training id must be strictly positive (at least 1): 0");
        }

        @Test
        @DisplayName("given invalid id string and cause, when calling invalidId, then retain details")
        void givenInvalidIdStringAndCause_whenCallingInvalidId_thenRetainDetails() {
            // given
            final var fieldName = "Training id";
            final var value = "abc";
            final var cause = new NumberFormatException("invalid");

            // when
            final var exception = InvalidTrainingOperationException.invalidId(fieldName, value, cause);

            // then
            assertThat(exception.getMessage()).isEqualTo("Invalid Training id format: abc");
            assertThat(exception.getCause()).isSameAs(cause);
        }

        @Test
        @DisplayName("given negative amount, when calling negativeCost, then produce expected message")
        void givenNegativeAmount_whenCallingNegativeCost_thenProduceExpectedMessage() {
            // given
            final var amount = BigDecimal.valueOf(-10);

            // when
            final var exception = InvalidTrainingOperationException.negativeCost(amount);

            // then
            assertThat(exception.getMessage()).isEqualTo("Cost amount cannot be negative: -10");
        }

        @Test
        @DisplayName("given currency string, when calling invalidCurrency, then produce expected message")
        void givenCurrencyString_whenCallingInvalidCurrency_thenProduceExpectedMessage() {
            // given
            final var currency = "INVALID";

            // when
            final var exception = InvalidTrainingOperationException.invalidCurrency(currency);

            // then
            assertThat(exception.getMessage()).isEqualTo("Invalid currency code: INVALID");
        }

        @Test
        @DisplayName("given currency and cause, when calling invalidCurrency with cause, then retain details")
        void givenCurrencyAndCause_whenCallingInvalidCurrencyWithCause_thenRetainDetails() {
            // given
            final var currency = "INVALID";
            final var cause = new IllegalArgumentException("Unknown currency");

            // when
            final var exception = InvalidTrainingOperationException.invalidCurrency(currency, cause);

            // then
            assertThat(exception.getMessage()).isEqualTo("Invalid currency code: INVALID");
            assertThat(exception.getCause()).isSameAs(cause);
        }

        @Test
        @DisplayName("given min and actual hours, when calling invalidHours, then produce expected message")
        void givenMinAndActualHours_whenCallingInvalidHours_thenProduceExpectedMessage() {
            // given
            final var min = 1;
            final var actual = 0;

            // when
            final var exception = InvalidTrainingOperationException.invalidHours(min, actual);

            // then
            assertThat(exception.getMessage())
                    .isEqualTo("Training hours must be strictly positive (at least 1): 0");
        }

        @Test
        @DisplayName("given invalid phone raw and bounds, when calling invalidPhoneFormat, then produce expected message")
        void givenInvalidPhoneRawAndBounds_whenCallingInvalidPhoneFormat_thenProduceExpectedMessage() {
            // given
            final var raw = "123";
            final var min = 7;
            final var max = 15;

            // when
            final var exception = InvalidTrainingOperationException.invalidPhoneFormat(raw, min, max);

            // then
            assertThat(exception.getMessage())
                    .isEqualTo("Invalid phone number format: 123. Must contain 7-15 digits.");
        }

        @Test
        @DisplayName("given invalid date range, when calling invalidDateRange, then produce expected message")
        void givenInvalidDateRange_whenCallingInvalidDateRange_thenProduceExpectedMessage() {
            // given
            final var start = Instant.parse("2026-06-01T10:00:00Z");
            final var end = Instant.parse("2026-05-01T10:00:00Z");

            // when
            final var exception = InvalidTrainingOperationException.invalidDateRange(start, end);

            // then
            assertThat(exception.getMessage())
                    .isEqualTo("End date (2026-05-01T10:00:00Z) cannot be before start date (2026-06-01T10:00:00Z)");
        }
    }
}
