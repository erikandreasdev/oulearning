package com.example.oulearning.organization.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.DomainGenerators;
import com.example.oulearning.organization.domain.DomainGivenProviders;
import com.example.oulearning.organization.domain.employee.exception.InvalidEmailException;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@ExtendWith(InstancioExtension.class)
class EmailTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest(name = "should reject: {0}")
        @ArgumentsSource(DomainGivenProviders.InvalidEmails.class)
        @DisplayName("should throw InvalidEmailException for invalid email format")
        void should_throwException_when_formatIsInvalid(String invalidEmail) {
            assertThatThrownBy(() -> Email.of(invalidEmail))
                    .isInstanceOf(InvalidEmailException.class);
        }

        @Test
        @DisplayName("should throw InvalidEmailException when value is null")
        void should_throwException_when_valueIsNull() {
            assertThatThrownBy(() -> Email.of(null))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessageContaining("Email cannot be null or blank");
        }

        @ParameterizedTest(name = "should normalize {0} to {1}")
        @ArgumentsSource(DomainGivenProviders.ValidEmails.class)
        @DisplayName("should create and normalize Email for valid format")
        void should_createAndNormalizeEmail_when_formatIsValid(String input, String expected) {
            final var email = Email.of(input);
            assertThat(email.value()).isEqualTo(expected);
            assertThat(email.toString()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when values match regardless of case")
        void should_beEqual_when_valuesMatchCaseInsensitive() {
            final var email1 = Email.of("USER@DOMAIN.COM");
            final var email2 = Email.of("user@domain.com");

            assertThat(email1).isEqualTo(email2);
            assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when values differ")
        void should_notBeEqual_when_valuesDiffer() {
            final var email1 = Email.of("user1@domain.com");
            final var email2 = Email.of("user2@domain.com");

            assertThat(email1).isNotEqualTo(email2);
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var email = DomainGenerators.randomEmail();
            assertThat(email.getClass().isRecord()).isTrue();
        }
    }
}
