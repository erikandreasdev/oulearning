package com.example.oulearning.organization.domain.employee.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("InvalidEmailException Unit Tests")
final class InvalidEmailExceptionTest {

    @Nested
    @DisplayName("Constructors and Factory Methods")
    final class FactoryMethods {

        @Test
        @DisplayName("given message, when creating, then retain message")
        void givenMessage_whenCreating_thenRetainMessage() {
            // given
            final var message = "Email is invalid";

            // when
            final var exception = new InvalidEmailException(message);

            // then
            assertThat(exception.getMessage()).isEqualTo(message);
        }

        @Test
        @DisplayName("when calling nullField, then produce expected message")
        void whenCallingNullField_thenProduceExpectedMessage() {
            // given

            // when
            final var exception = InvalidEmailException.nullField();

            // then
            assertThat(exception.getMessage()).isEqualTo("Email cannot be null");
        }

        @Test
        @DisplayName("when calling blankField, then produce expected message")
        void whenCallingBlankField_thenProduceExpectedMessage() {
            // given

            // when
            final var exception = InvalidEmailException.blankField();

            // then
            assertThat(exception.getMessage()).isEqualTo("Email cannot be blank");
        }

        @Test
        @DisplayName("given email, when calling invalidFormat, then produce expected message")
        void givenEmail_whenCallingInvalidFormat_thenProduceExpectedMessage() {
            // given
            final var email = "invalid-email";

            // when
            final var exception = InvalidEmailException.invalidFormat(email);

            // then
            assertThat(exception.getMessage()).isEqualTo("Invalid email format: invalid-email");
        }
    }
}
