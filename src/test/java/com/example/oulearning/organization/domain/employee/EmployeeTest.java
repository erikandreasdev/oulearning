package com.example.oulearning.organization.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    private final EmployeeId id = EmployeeId.of("EMP-001");
    private final FullName fullName = FullName.of("Jane", "Doe");
    private final Email email = Email.of("jane.doe@example.com");

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("should create employee when valid fields provided")
        void should_createEmployee_when_validFields() {
            Employee employee = Employee.of(id, fullName, email);

            assertThat(employee.id()).isEqualTo(id);
            assertThat(employee.fullName()).isEqualTo(fullName);
            assertThat(employee.email()).isEqualTo(email);
        }

        @Test
        @DisplayName("should throw exception when creating employee with null parameters")
        void should_throwException_when_nullParams() {
            assertThatThrownBy(() -> Employee.of(null, fullName, email))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> Employee.of(id, null, email))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> Employee.of(id, fullName, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Identity and Equality")
    class IdentityAndEquality {

        @Test
        @DisplayName("should be equal when ids match regardless of other fields")
        void should_beEqual_when_idsMatch() {
            Employee employee1 = Employee.of(id, fullName, email);
            Employee employee2 = Employee.of(id, FullName.of("Other", "Name"), Email.of("other@example.com"));

            assertThat(employee1).isEqualTo(employee2);
            assertThat(employee1.hashCode()).isEqualTo(employee2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when ids differ")
        void should_notBeEqual_when_idsDiffer() {
            Employee employee1 = Employee.of(id, fullName, email);
            Employee employee2 = Employee.of(EmployeeId.of("EMP-002"), fullName, email);

            assertThat(employee1).isNotEqualTo(employee2);
        }
    }
}
