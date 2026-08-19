package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.EmployeeTestFactory;
import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ExternalProviderContactTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid email and phone, when creating ExternalProviderContact, then create successfully")
        void givenValidEmailAndPhone_whenCreatingExternalProviderContact_thenCreateSuccessfully() {

            final var email = EmployeeTestFactory.randomEmail();
            final var phone = TrainingTestFactory.randomPhone();


            final var contact = ExternalProviderContact.of(email, phone);


            assertThat(contact.email()).isEqualTo(email);
            assertThat(contact.phone()).isEqualTo(phone);
        }

        @Test
        @DisplayName("given null email, when creating ExternalProviderContact, then throw InvalidTrainingOperationException")
        void givenNullEmail_whenCreatingExternalProviderContact_thenThrowInvalidTrainingOperationException() {

            final var phone = TrainingTestFactory.randomPhone();




            assertThatThrownBy(() -> ExternalProviderContact.of(null, phone))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("Email cannot be null");
        }

        @Test
        @DisplayName("given null phone, when creating ExternalProviderContact, then throw InvalidTrainingOperationException")
        void givenNullPhone_whenCreatingExternalProviderContact_thenThrowInvalidTrainingOperationException() {

            final var email = EmployeeTestFactory.randomEmail();




            assertThatThrownBy(() -> ExternalProviderContact.of(email, null))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("Phone cannot be null");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical contacts, when comparing, then they are equal")
        void givenIdenticalContacts_whenComparing_thenTheyAreEqual() {

            final var email = EmployeeTestFactory.randomEmail();
            final var phone = TrainingTestFactory.randomPhone();
            final var c1 = ExternalProviderContact.of(email, phone);
            final var c2 = ExternalProviderContact.of(email, phone);




            assertThat(c1).isEqualTo(c2);
            assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
        }
    }
}
