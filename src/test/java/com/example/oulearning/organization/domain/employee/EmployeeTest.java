package com.example.oulearning.organization.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.exception.InvalidEmployeeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    private final EmployeeId id = EmployeeTestFactory.randomEmployeeId();
    private final FullName fullName = EmployeeTestFactory.randomFullName();
    private final Email email = EmployeeTestFactory.randomEmail();

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("given valid fields, when creating Employee, then employee is created successfully")
        void givenValidFields_whenCreatingEmployee_thenEmployeeIsCreatedSuccessfully() {
            // given

            // when
            final var employee = Employee.of(id, fullName, email);

            // then
            assertThat(employee.id()).isEqualTo(id);
            assertThat(employee.fullName()).isEqualTo(fullName);
            assertThat(employee.email()).isEqualTo(email);
            assertThat(employee)
                    .hasToString("Employee[id=%s, fullName=%s, email=%s]".formatted(id, fullName, email));
        }

        @Test
        @DisplayName("given null parameters, when creating Employee, then throw InvalidEmployeeException")
        void givenNullParameters_whenCreatingEmployee_thenThrowInvalidEmployeeException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> Employee.of(null, fullName, email))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Employee.of(id, null, email))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Employee.of(id, fullName, null))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("cannot be null");
        }
    }

    @Nested
    @DisplayName("Identity and Equality")
    class IdentityAndEquality {

        @Test
        @DisplayName("given employees with same id, when comparing, then they are equal")
        void givenEmployeesWithSameId_whenComparing_thenTheyAreEqual() {
            // given
            final var employee1 = Employee.of(id, fullName, email);
            final var employee2 = Employee.of(
                    id, EmployeeTestFactory.randomFullName(), EmployeeTestFactory.randomEmail());

            // when

            // then
            assertThat(employee1).isEqualTo(employee2).hasSameHashCodeAs(employee2);
        }

        @Test
        @DisplayName("given employees with different ids, when comparing, then they are not equal")
        void givenEmployeesWithDifferentIds_whenComparing_thenTheyAreNotEqual() {
            // given
            final var employee1 = Employee.of(id, fullName, email);
            final var employee2 = Employee.of(
                    EmployeeTestFactory.randomEmployeeId(), fullName, email);

            // when

            // then
            assertThat(employee1).isNotEqualTo(employee2);
        }

        @Test
        @DisplayName("given same employee instance, when comparing, then they are equal")
        void givenSameEmployeeInstance_whenComparing_thenTheyAreEqual() {
            // given
            final var employee = Employee.of(id, fullName, email);

            // when

            // then
            assertThat(employee).isEqualTo(employee);
        }

        @Test
        @DisplayName("given null or different object type, when comparing, then they are not equal")
        void givenNullOrDifferentType_whenComparing_thenTheyAreNotEqual() {
            // given
            final var employee = Employee.of(id, fullName, email);

            // when

            // then
            assertThat(employee).isNotEqualTo(null).isNotEqualTo(new Object());
        }
    }
}
