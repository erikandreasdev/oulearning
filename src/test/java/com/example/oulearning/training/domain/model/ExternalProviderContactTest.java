package com.example.oulearning.training.domain.model;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.port.in.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.port.in.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.port.in.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.application.port.in.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.model.EmployeeTestFactory;
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
            // given
            final var email = EmployeeTestFactory.randomEmail();
            final var phone = TrainingTestFactory.randomPhone();

            // when
            final var contact = ExternalProviderContact.of(email, phone);

            // then
            assertThat(contact.email()).isEqualTo(email);
            assertThat(contact.phone()).isEqualTo(phone);
        }

        @Test
        @DisplayName("given null email, when creating ExternalProviderContact, then throw InvalidTrainingOperationException")
        void givenNullEmail_whenCreatingExternalProviderContact_thenThrowInvalidTrainingOperationException() {
            // given
            final var phone = TrainingTestFactory.randomPhone();

            // when

            // then
            assertThatThrownBy(() -> ExternalProviderContact.of(null, phone))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("Email cannot be null");
        }

        @Test
        @DisplayName("given null phone, when creating ExternalProviderContact, then throw InvalidTrainingOperationException")
        void givenNullPhone_whenCreatingExternalProviderContact_thenThrowInvalidTrainingOperationException() {
            // given
            final var email = EmployeeTestFactory.randomEmail();

            // when

            // then
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
            // given
            final var email = EmployeeTestFactory.randomEmail();
            final var phone = TrainingTestFactory.randomPhone();
            final var c1 = ExternalProviderContact.of(email, phone);
            final var c2 = ExternalProviderContact.of(email, phone);

            // when

            // then
            assertThat(c1).isEqualTo(c2).hasSameHashCodeAs(c2);
        }
    }
}
