package com.example.oulearning.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EmailTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "user@example.com",
                    "USER@EXAMPLE.COM",
                    "  user.name@domain.co.uk  ",
                    "user+tag@sub.domain.org",
                    "first_last@service.net"
                })
        @DisplayName("should create email when valid format provided")
        void should_createEmail_when_validFormatProvided(String rawEmail) {
            // when
            Email email = Email.of(rawEmail);

            // then
            assertThat(email.value()).isEqualTo(rawEmail.strip().toLowerCase());
            assertThat(email.toString()).isEqualTo(rawEmail.strip().toLowerCase());
        }

        @Test
        @DisplayName("should throw InvalidEmailException when email is null")
        void should_throwException_when_emailIsNull() {
            // when / then
            assertThatThrownBy(() -> new Email(null))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   ", "\t\n"})
        @DisplayName("should throw InvalidEmailException when email is blank")
        void should_throwException_when_emailIsBlank(String blankEmail) {
            // when / then
            assertThatThrownBy(() -> new Email(blankEmail))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "plainaddress",
                    "@missingusername.com",
                    "username@.com",
                    "username@domain..com",
                    "username@domain.c",
                    "user@domain",
                    "user space@domain.com",
                    "user..name@domain.com",
                    ".user@domain.com",
                    "user.@domain.com"
                })
        @DisplayName("should throw InvalidEmailException when format is invalid")
        void should_throwException_when_formatIsInvalid(String invalidEmail) {
            // when / then
            assertThatThrownBy(() -> Email.of(invalidEmail))
                    .isInstanceOfSatisfying(
                            InvalidEmailException.class,
                            ex -> assertThat(ex.getInvalidValue()).isEqualTo(invalidEmail.strip().toLowerCase()));
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("should be equal when values are identical after normalization")
        void should_beEqual_when_valuesAreIdenticalAfterNormalization() {
            // given
            Email email1 = Email.of("Test.User@Example.COM");
            Email email2 = Email.of("test.user@example.com");

            // then
            assertThat(email1).isEqualTo(email2);
            assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when emails differ")
        void should_notBeEqual_when_emailsDiffer() {
            // given
            Email email1 = Email.of("user1@example.com");
            Email email2 = Email.of("user2@example.com");

            // then
            assertThat(email1).isNotEqualTo(email2);
        }
    }
}
