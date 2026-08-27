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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("InvalidTrainingStateException Unit Tests")
final class InvalidTrainingStateExceptionTest {

    @Nested
    @DisplayName("Constructors")
    final class Constructors {

        @Test
        @DisplayName("given message, when creating, then retain message")
        void givenMessage_whenCreating_thenRetainMessage() {
            // given
            final var message = "Training state is invalid";

            // when
            final var exception = new InvalidTrainingStateException(message);

            // then
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception).isInstanceOf(TrainingException.class);
        }

        @Test
        @DisplayName("given message and cause, when creating, then retain both")
        void givenMessageAndCause_whenCreating_thenRetainBoth() {
            // given
            final var message = "Training state is invalid";
            final var cause = new IllegalStateException("Invalid transition");

            // when
            final var exception = new InvalidTrainingStateException(message, cause);

            // then
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception.getCause()).isSameAs(cause);
            assertThat(exception).isInstanceOf(TrainingException.class);
        }
    }
}
