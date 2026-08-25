package com.example.oulearning.organization.domain.employee.model;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.port.in.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.application.employee.port.in.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.port.in.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.domain.model.*;
import com.example.oulearning.training.application.port.in.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.exception.InvalidEmailException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EmailTest {

    @Nested
    @DisplayName("Creation and Normalization")
    class CreationAndNormalization {

        @Test
        @DisplayName("given uppercase and padded email, when creating Email, then normalize to lowercase and trim spaces")
        void givenUppercaseAndPaddedEmail_whenCreatingEmail_thenNormalizeToLowercaseAndTrim() {
            // given
            final var user = EmployeeTestFactory.randomUsername();
            final var domain = EmployeeTestFactory.randomDomain();
            final var rawEmail = "  %s@%s.COM  ".formatted(user.toUpperCase(), domain.toUpperCase());
            final var expectedNormalized = "%s@%s.com".formatted(user, domain);

            // when
            final var email = Email.of(rawEmail);

            // then
            assertThat(email.value()).isEqualTo(expectedNormalized);
            assertThat(email).hasToString(expectedNormalized);
        }

        @Test
        @DisplayName("given random valid email, when creating Email, then email is created successfully")
        void givenRandomValidEmail_whenCreatingEmail_thenEmailIsCreatedSuccessfully() {
            // given
            final var email = EmployeeTestFactory.randomEmail();

            // when

            // then
            assertThat(email.value()).isNotNull().isNotBlank();
        }

        @Test
        @DisplayName("given null email, when creating Email, then throw InvalidEmailException")
        void givenNullEmail_whenCreatingEmail_thenThrowInvalidEmailException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> new Email(null))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("given blank email, when creating Email, then throw InvalidEmailException")
        void givenBlankEmail_whenCreatingEmail_thenThrowInvalidEmailException() {
            // given
            final var blank = " ".repeat(Instancio.gen().ints().range(1, 5).get());

            // when

            // then
            assertThatThrownBy(() -> new Email(blank))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @Test
        @DisplayName("given invalid email format without domain, when creating Email, then throw InvalidEmailException")
        void givenInvalidEmailFormatWithoutDomain_whenCreatingEmail_thenThrowInvalidEmailException() {
            // given
            final var invalidEmail = Instancio.gen().string().alphaNumeric().length(5, 10).get();

            // when

            // then
            assertThatThrownBy(() -> Email.of(invalidEmail))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessageContaining("Invalid email format");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given emails with same content but different cases, when comparing, then they are equal")
        void givenEmailsWithDifferentCase_whenComparing_thenTheyAreEqual() {
            // given
            final var user = EmployeeTestFactory.randomUsername();
            final var e1 = Email.of("%s@example.com".formatted(user));
            final var e2 = Email.of("  %s@EXAMPLE.COM ".formatted(user.toUpperCase()));

            // when

            // then
            assertThat(e1).isEqualTo(e2).hasSameHashCodeAs(e2);
        }

        @Test
        @DisplayName("given different emails, when comparing, then they are not equal")
        void givenDifferentEmails_whenComparing_thenTheyAreNotEqual() {
            // given
            final var e1 = EmployeeTestFactory.randomEmail();
            final var e2 = EmployeeTestFactory.randomEmail();

            // when

            // then
            assertThat(e1).isNotEqualTo(e2);
        }
    }
}
