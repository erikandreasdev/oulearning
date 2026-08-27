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

@DisplayName("HierarchyException Unit Tests")
final class HierarchyExceptionTest {

    @Nested
    @DisplayName("Constructors")
    final class Constructors {

        @Test
        @DisplayName("given message, when creating subclass, then retain message and be instance of HierarchyException")
        void givenMessage_whenCreatingSubclass_thenRetainMessageAndBeInstanceOfHierarchyException() {
            // given
            final var message = "Hierarchy error occurred";

            // when
            final HierarchyException exception = new InvalidOrganizationalUnitException(message);

            // then
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception).isInstanceOf(HierarchyException.class);
        }

        @Test
        @DisplayName("given message and cause, when creating subclass, then retain both and be instance of HierarchyException")
        void givenMessageAndCause_whenCreatingSubclass_thenRetainBothAndBeInstanceOfHierarchyException() {
            // given
            final var message = "Hierarchy error occurred";
            final var cause = new IllegalArgumentException("Invalid hierarchy");

            // when
            final HierarchyException exception = new InvalidOrganizationalUnitException(message, cause);

            // then
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception.getCause()).isSameAs(cause);
            assertThat(exception).isInstanceOf(HierarchyException.class);
        }
    }
}
