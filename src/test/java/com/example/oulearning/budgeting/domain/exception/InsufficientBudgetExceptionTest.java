package com.example.oulearning.budgeting.domain.exception;

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
