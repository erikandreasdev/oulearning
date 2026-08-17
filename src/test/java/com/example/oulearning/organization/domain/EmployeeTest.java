package com.example.oulearning.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("should create employee from typed value objects")
        void should_createEmployee_from_typedObjects() {
            // given
            CorporateKey key = CorporateKey.of("CK1234");
            FullName fullName = FullName.of("Jane", "Doe");
            Email email = Email.of("jane.doe@company.com");
            EmployeeRole role = EmployeeRole.MANAGER;

            // when
            Employee employee = Employee.of(key, fullName, email, role);

            // then
            assertThat(employee.corporateKey()).isEqualTo(key);
            assertThat(employee.fullName()).isEqualTo(fullName);
            assertThat(employee.email()).isEqualTo(email);
            assertThat(employee.role()).isEqualTo(EmployeeRole.MANAGER);
        }

        @Test
        @DisplayName("should create employee from convenience factory method with enum role")
        void should_createEmployee_from_convenienceFactory() {
            // when
            Employee employee = Employee.of("ck1234", "Jane", "Doe", "jane.doe@company.com", EmployeeRole.EMPLOYEE);

            // then
            assertThat(employee.corporateKey().value()).isEqualTo("CK1234");
            assertThat(employee.fullName().formatted()).isEqualTo("Jane Doe");
            assertThat(employee.email().value()).isEqualTo("jane.doe@company.com");
            assertThat(employee.role()).isEqualTo(EmployeeRole.EMPLOYEE);
        }

        @Test
        @DisplayName("should create employee from convenience factory method with string role")
        void should_createEmployee_from_convenienceFactory_withStringRole() {
            // when
            Employee employee = Employee.of("ck1234", "Jane", "Doe", "jane.doe@company.com", "manager");

            // then
            assertThat(employee.corporateKey().value()).isEqualTo("CK1234");
            assertThat(employee.fullName().formatted()).isEqualTo("Jane Doe");
            assertThat(employee.email().value()).isEqualTo("jane.doe@company.com");
            assertThat(employee.role()).isEqualTo(EmployeeRole.MANAGER);
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when corporate key is null")
        void should_throwException_when_corporateKeyIsNull() {
            FullName fullName = FullName.of("Jane", "Doe");
            Email email = Email.of("jane.doe@company.com");

            assertThatThrownBy(() -> new Employee(null, fullName, email, EmployeeRole.EMPLOYEE))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Corporate key cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when full name is null")
        void should_throwException_when_fullNameIsNull() {
            CorporateKey key = CorporateKey.of("CK1234");
            Email email = Email.of("jane.doe@company.com");

            assertThatThrownBy(() -> new Employee(key, null, email, EmployeeRole.EMPLOYEE))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Full name cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when email is null")
        void should_throwException_when_emailIsNull() {
            CorporateKey key = CorporateKey.of("CK1234");
            FullName fullName = FullName.of("Jane", "Doe");

            assertThatThrownBy(() -> new Employee(key, fullName, null, EmployeeRole.EMPLOYEE))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Email cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when role is null")
        void should_throwException_when_roleIsNull() {
            CorporateKey key = CorporateKey.of("CK1234");
            FullName fullName = FullName.of("Jane", "Doe");
            Email email = Email.of("jane.doe@company.com");

            assertThatThrownBy(() -> new Employee(key, fullName, email, null))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Employee role cannot be null");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("should be equal when all fields match")
        void should_beEqual_when_allFieldsMatch() {
            // given
            Employee emp1 = Employee.of("CK1234", "Jane", "Doe", "jane.doe@company.com", EmployeeRole.TRAINER);
            Employee emp2 = Employee.of("ck1234", "Jane", "Doe", "jane.doe@company.com", EmployeeRole.TRAINER);

            // then
            assertThat(emp1).isEqualTo(emp2);
            assertThat(emp1.hashCode()).isEqualTo(emp2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when fields differ")
        void should_notBeEqual_when_fieldsDiffer() {
            // given
            Employee emp1 = Employee.of("CK1234", "Jane", "Doe", "jane.doe@company.com", EmployeeRole.TRAINER);
            Employee emp2 = Employee.of("CK5678", "Jane", "Doe", "jane.doe@company.com", EmployeeRole.TRAINER);

            // then
            assertThat(emp1).isNotEqualTo(emp2);
        }
    }
}
