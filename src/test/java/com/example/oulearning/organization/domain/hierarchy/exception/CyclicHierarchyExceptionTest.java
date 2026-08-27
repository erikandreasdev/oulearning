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

@DisplayName("CyclicHierarchyException Unit Tests")
final class CyclicHierarchyExceptionTest {

    @Nested
    @DisplayName("Constructors")
    final class Constructors {

        @Test
        @DisplayName("given message, when creating, then retain message")
        void givenMessage_whenCreating_thenRetainMessage() {
            // given
            final var message = "Cyclic hierarchy detected";

            // when
            final var exception = new CyclicHierarchyException(message);

            // then
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception).isInstanceOf(HierarchyException.class);
        }

        @Test
        @DisplayName("given message and cause, when creating, then retain both")
        void givenMessageAndCause_whenCreating_thenRetainBoth() {
            // given
            final var message = "Cyclic hierarchy detected";
            final var cause = new IllegalStateException("Cycle in path");

            // when
            final var exception = new CyclicHierarchyException(message, cause);

            // then
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception.getCause()).isSameAs(cause);
            assertThat(exception).isInstanceOf(HierarchyException.class);
        }
    }
}
