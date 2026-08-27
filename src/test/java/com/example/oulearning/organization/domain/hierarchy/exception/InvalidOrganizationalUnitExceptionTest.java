package com.example.oulearning.organization.domain.hierarchy.exception;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.domain.model.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("InvalidOrganizationalUnitException Unit Tests")
final class InvalidOrganizationalUnitExceptionTest {

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
            final var exception = new InvalidOrganizationalUnitException(message, cause);

            // then
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception.getCause()).isSameAs(cause);
        }

        @Test
        @DisplayName("given fieldName, when calling nullField, then produce expected message")
        void givenFieldName_whenCallingNullField_thenProduceExpectedMessage() {
            // given
            final var fieldName = "Organizational unit id";

            // when
            final var exception = InvalidOrganizationalUnitException.nullField(fieldName);

            // then
            assertThat(exception.getMessage()).isEqualTo("Organizational unit id cannot be null");
        }

        @Test
        @DisplayName("given fieldName, when calling blankField, then produce expected message")
        void givenFieldName_whenCallingBlankField_thenProduceExpectedMessage() {
            // given
            final var fieldName = "Name";

            // when
            final var exception = InvalidOrganizationalUnitException.blankField(fieldName);

            // then
            assertThat(exception.getMessage()).isEqualTo("Name cannot be blank");
        }

        @Test
        @DisplayName("given fieldName, when calling nullOrBlank, then produce expected message")
        void givenFieldName_whenCallingNullOrBlank_thenProduceExpectedMessage() {
            // given
            final var fieldName = "Organizational unit id";

            // when
            final var exception = InvalidOrganizationalUnitException.nullOrBlank(fieldName);

            // then
            assertThat(exception.getMessage())
                    .isEqualTo("Organizational unit id string cannot be null or blank");
        }

        @Test
        @DisplayName("given range bounds, when calling lengthOutOfRange, then produce expected message")
        void givenRangeBounds_whenCallingLengthOutOfRange_thenProduceExpectedMessage() {
            // given
            final var fieldName = "Organizational unit name";
            final var min = 2;
            final var max = 100;
            final var actual = "A";

            // when
            final var exception =
                    InvalidOrganizationalUnitException.lengthOutOfRange(fieldName, min, max, actual);

            // then
            assertThat(exception.getMessage())
                    .isEqualTo("Organizational unit name length must be between 2 and 100 characters: A");
        }

        @Test
        @DisplayName("given non-positive id, when calling nonPositiveId, then produce expected message")
        void givenNonPositiveId_whenCallingNonPositiveId_thenProduceExpectedMessage() {
            // given
            final var fieldName = "Organizational unit id";
            final var value = 0L;

            // when
            final var exception = InvalidOrganizationalUnitException.nonPositiveId(fieldName, value);

            // then
            assertThat(exception.getMessage())
                    .isEqualTo("Organizational unit id must be strictly positive (at least 1): 0");
        }

        @Test
        @DisplayName("given invalid id string and cause, when calling invalidId, then retain details")
        void givenInvalidIdStringAndCause_whenCallingInvalidId_thenRetainDetails() {
            // given
            final var fieldName = "Organizational unit id";
            final var value = "abc";
            final var cause = new NumberFormatException("invalid");

            // when
            final var exception = InvalidOrganizationalUnitException.invalidId(fieldName, value, cause);

            // then
            assertThat(exception.getMessage()).isEqualTo("Invalid Organizational unit id format: abc");
            assertThat(exception.getCause()).isSameAs(cause);
        }
    }
}
