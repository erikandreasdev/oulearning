package com.example.oulearning.budgeting.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("InvalidBudgetOperationException Unit Tests")
final class InvalidBudgetOperationExceptionTest {

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
            final var exception = new InvalidBudgetOperationException(message, cause);

            // then
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception.getCause()).isSameAs(cause);
        }

        @Test
        @DisplayName("given fieldName, when calling nullField, then produce expected message")
        void givenFieldName_whenCallingNullField_thenProduceExpectedMessage() {
            // given
            final var fieldName = "Budget id";

            // when
            final var exception = InvalidBudgetOperationException.nullField(fieldName);

            // then
            assertThat(exception.getMessage()).isEqualTo("Budget id cannot be null");
        }

        @Test
        @DisplayName("given fieldName, when calling blankField, then produce expected message")
        void givenFieldName_whenCallingBlankField_thenProduceExpectedMessage() {
            // given
            final var fieldName = "Description";

            // when
            final var exception = InvalidBudgetOperationException.blankField(fieldName);

            // then
            assertThat(exception.getMessage()).isEqualTo("Description cannot be blank");
        }

        @Test
        @DisplayName("given fieldName, when calling nullOrBlank, then produce expected message")
        void givenFieldName_whenCallingNullOrBlank_thenProduceExpectedMessage() {
            // given
            final var fieldName = "Budget id";

            // when
            final var exception = InvalidBudgetOperationException.nullOrBlank(fieldName);

            // then
            assertThat(exception.getMessage()).isEqualTo("Budget id string cannot be null or blank");
        }

        @Test
        @DisplayName("given fiscal year bounds, when calling fiscalYearOutOfRange, then produce expected message")
        void givenFiscalYearBounds_whenCallingFiscalYearOutOfRange_thenProduceExpectedMessage() {
            // given
            final var min = 2000;
            final var max = 2100;
            final var actual = 1999;

            // when
            final var exception = InvalidBudgetOperationException.fiscalYearOutOfRange(min, max, actual);

            // then
            assertThat(exception.getMessage()).isEqualTo("Fiscal year must be between 2000 and 2100: 1999");
        }

        @Test
        @DisplayName("given non-positive id, when calling nonPositiveId, then produce expected message")
        void givenNonPositiveId_whenCallingNonPositiveId_thenProduceExpectedMessage() {
            // given
            final var fieldName = "Budget id";
            final var value = 0L;

            // when
            final var exception = InvalidBudgetOperationException.nonPositiveId(fieldName, value);

            // then
            assertThat(exception.getMessage())
                    .isEqualTo("Budget id must be strictly positive (at least 1): 0");
        }

        @Test
        @DisplayName("given invalid id string and cause, when calling invalidId, then retain details")
        void givenInvalidIdStringAndCause_whenCallingInvalidId_thenRetainDetails() {
            // given
            final var fieldName = "Budget id";
            final var value = "abc";
            final var cause = new NumberFormatException("invalid");

            // when
            final var exception = InvalidBudgetOperationException.invalidId(fieldName, value, cause);

            // then
            assertThat(exception.getMessage()).isEqualTo("Invalid Budget id format: abc");
            assertThat(exception.getCause()).isSameAs(cause);
        }
    }
}
