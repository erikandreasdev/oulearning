package com.example.oulearning.organization.domain.employee.vo.contact;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.DomainGenerators;
import com.example.oulearning.organization.domain.DomainGivenProviders;
import com.example.oulearning.organization.domain.employee.exception.contact.InvalidPhoneException;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@ExtendWith(InstancioExtension.class)
class PhoneTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest(name = "should reject: {0}")
        @ArgumentsSource(DomainGivenProviders.InvalidPhones.class)
        @DisplayName("should throw InvalidPhoneException for invalid phone format")
        void should_throwException_when_formatIsInvalid(String invalidPhone) {
            assertThatThrownBy(() -> Phone.of(invalidPhone))
                    .isInstanceOf(InvalidPhoneException.class);
        }

        @Test
        @DisplayName("should throw InvalidPhoneException when value is null")
        void should_throwException_when_valueIsNull() {
            assertThatThrownBy(() -> Phone.of(null))
                    .isInstanceOf(InvalidPhoneException.class)
                    .hasMessageContaining("Phone cannot be null or blank");
        }

        @ParameterizedTest(name = "should normalize {0} to {1}")
        @ArgumentsSource(DomainGivenProviders.ValidPhones.class)
        @DisplayName("should create and normalize Phone for valid input")
        void should_createAndNormalizePhone_when_inputIsValid(String input, String expected) {
            final var phone = Phone.of(input);
            assertThat(phone.value()).isEqualTo(expected);
            assertThat(phone.toString()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when normalized values match")
        void should_beEqual_when_normalizedValuesMatch() {
            final var phone1 = Phone.of("+34 612 345 678");
            final var phone2 = Phone.of("+34-612-345-678");

            assertThat(phone1).isEqualTo(phone2);
            assertThat(phone1.hashCode()).isEqualTo(phone2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when values differ")
        void should_notBeEqual_when_valuesDiffer() {
            final var phone1 = Phone.of("+34612345678");
            final var phone2 = Phone.of("+34612345679");

            assertThat(phone1).isNotEqualTo(phone2);
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var phone = DomainGenerators.randomPhone();
            assertThat(phone.getClass().isRecord()).isTrue();
        }
    }
}
