package com.example.oulearning.organization.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.exception.InvalidEmailException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
            assertThat(email.toString()).isEqualTo(expectedNormalized);
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

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t\n"})
        @DisplayName("given blank email, when creating Email, then throw InvalidEmailException")
        void givenBlankEmail_whenCreatingEmail_thenThrowInvalidEmailException(final String blank) {
            // given

            // when

            // then
            assertThatThrownBy(() -> new Email(blank))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "plainaddress",
                    "missing@domain",
                    "@missingusername.com",
                    "user@.com",
                    "user@domain..com"
                })
        @DisplayName("given invalid email format, when creating Email, then throw InvalidEmailException")
        void givenInvalidEmailFormat_whenCreatingEmail_thenThrowInvalidEmailException(final String invalidEmail) {
            // given

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
            assertThat(e1).isEqualTo(e2);
            assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
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
