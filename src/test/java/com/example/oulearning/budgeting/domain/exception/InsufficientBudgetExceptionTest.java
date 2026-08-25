package com.example.oulearning.budgeting.domain.exception;

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
import org.junit.jupiter.api.Test;

@DisplayName("InsufficientBudgetException Unit Tests")
final class InsufficientBudgetExceptionTest {

    @Test
    @DisplayName("given message, when creating, then retain message")
    void givenMessage_whenCreating_thenRetainMessage() {
        // given
        final var message = "Insufficient budget for operation";

        // when
        final var exception = new InsufficientBudgetException(message);

        // then
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
