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

import com.example.oulearning.organization.domain.employee.exception.InvalidEmployeeException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FullNameTest {

    @Nested
    @DisplayName("Creation and Formatting")
    class CreationAndFormatting {

        @Test
        @DisplayName("given Name and Surname objects, when creating FullName, then create successfully")
        void givenNameAndSurnameObjects_whenCreatingFullName_thenCreateSuccessfully() {
            // given
            final var name = EmployeeTestFactory.randomName();
            final var surname = EmployeeTestFactory.randomSurname();

            // when
            final var fullName = FullName.of(name, surname);

            // then
            assertThat(fullName.name()).isEqualTo(name);
            assertThat(fullName.surname()).isEqualTo(surname);
            assertThat(fullName.formatted()).isEqualTo("%s %s".formatted(name.value(), surname.value()));
            assertThat(fullName).hasToString("%s %s".formatted(name.value(), surname.value()));
        }

        @Test
        @DisplayName("given raw strings with padding, when creating FullName, then trim and create successfully")
        void givenRawStringsWithPadding_whenCreatingFullName_thenTrimAndCreateSuccessfully() {
            // given
            final var rawName = EmployeeTestFactory.randomName().value();
            final var rawSurname = EmployeeTestFactory.randomSurname().value();

            // when
            final var fullName = FullName.of(" %s ".formatted(rawName), " %s ".formatted(rawSurname));

            // then
            assertThat(fullName.name().value()).isEqualTo(rawName);
            assertThat(fullName.surname().value()).isEqualTo(rawSurname);
            assertThat(fullName.formatted()).isEqualTo("%s %s".formatted(rawName, rawSurname));
        }

        @Test
        @DisplayName("given null name or surname, when creating FullName, then throw InvalidEmployeeException")
        void givenNullComponents_whenCreatingFullName_thenThrowInvalidEmployeeException() {
            // given
            final var surname = EmployeeTestFactory.randomSurname();
            final var name = EmployeeTestFactory.randomName();

            // when

            // then
            assertThatThrownBy(() -> new FullName(null, surname))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("First name cannot be null");

            assertThatThrownBy(() -> new FullName(name, null))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Surname cannot be null");
        }

        @Test
        @DisplayName("given blank name or surname string, when creating, then throw InvalidEmployeeException")
        void givenBlankComponents_whenCreating_thenThrowInvalidEmployeeException() {
            // given
            final var blank = " ".repeat(Instancio.gen().ints().range(1, 5).get());

            // when

            // then
            assertThatThrownBy(() -> Name.of(blank))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("cannot be blank");

            assertThatThrownBy(() -> Surname.of(blank))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @Test
        @DisplayName("given name or surname exceeding max length, when creating, then throw InvalidEmployeeException")
        void givenLengthExceeded_whenCreating_thenThrowInvalidEmployeeException() {
            // given
            final var longName = "A".repeat(EmployeeConstants.MAX_NAME_LENGTH + 1);

            // when

            // then
            assertThatThrownBy(() -> Name.of(longName))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Name length must be between");

            assertThatThrownBy(() -> Surname.of(longName))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Surname length must be between");
        }

        @Test
        @DisplayName("given name and surname exceeding max full name length, when creating, then throw InvalidEmployeeException")
        void givenCombinedLengthExceeded_whenCreating_thenThrowInvalidEmployeeException() {
            // given
            final var longName = Name.of("A".repeat(EmployeeConstants.MAX_NAME_LENGTH));
            final var longSurname = Surname.of("B".repeat(EmployeeConstants.MAX_SURNAME_LENGTH));

            // when

            // then
            assertThatThrownBy(() -> new FullName(longName, longSurname))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Full name length exceeds maximum of");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical names and surnames, when comparing FullName, then they are equal")
        void givenIdenticalNamesAndSurnames_whenComparingFullName_thenTheyAreEqual() {
            // given
            final var name = EmployeeTestFactory.randomName();
            final var surname = EmployeeTestFactory.randomSurname();
            final var fn1 = FullName.of(name, surname);
            final var fn2 = FullName.of(name, surname);

            // when

            // then
            assertThat(fn1).isEqualTo(fn2).hasSameHashCodeAs(fn2);
        }

        @Test
        @DisplayName("given different names or surnames, when comparing FullName, then they are not equal")
        void givenDifferentNamesOrSurnames_whenComparingFullName_thenTheyAreNotEqual() {
            // given
            final var fn1 = EmployeeTestFactory.randomFullName();
            final var fn2 = EmployeeTestFactory.randomFullName();

            // when

            // then
            assertThat(fn1).isNotEqualTo(fn2);
        }
    }
}
