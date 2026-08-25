package com.example.oulearning.training.domain.exception;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.port.in.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.port.in.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.port.in.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.domain.model.*;
import com.example.oulearning.training.application.port.in.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("TrainingException Unit Tests")
final class TrainingExceptionTest {

    @Nested
    @DisplayName("Constructors")
    final class Constructors {

        @Test
        @DisplayName("given message, when creating subclass, then retain message and be instance of TrainingException")
        void givenMessage_whenCreatingSubclass_thenRetainMessageAndBeInstanceOfTrainingException() {
            // given
            final var message = "Training error occurred";

            // when
            final TrainingException exception = new InvalidTrainingOperationException(message);

            // then
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception).isInstanceOf(TrainingException.class);
        }

        @Test
        @DisplayName("given message and cause, when creating subclass, then retain both and be instance of TrainingException")
        void givenMessageAndCause_whenCreatingSubclass_thenRetainBothAndBeInstanceOfTrainingException() {
            // given
            final var message = "Training error occurred";
            final var cause = new IllegalArgumentException("Invalid input");

            // when
            final TrainingException exception = new InvalidTrainingOperationException(message, cause);

            // then
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception.getCause()).isSameAs(cause);
            assertThat(exception).isInstanceOf(TrainingException.class);
        }
    }
}
