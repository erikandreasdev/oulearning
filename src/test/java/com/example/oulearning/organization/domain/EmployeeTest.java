package com.example.oulearning.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

@ExtendWith(InstancioExtension.class)
class EmployeeTest {

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("should create employee from typed value objects")
        void should_createEmployee_from_typedObjects() {
            // given
            final var key = DomainGenerators.randomCorporateKey();
            final var fullName = DomainGenerators.randomFullName();
            final var email = DomainGenerators.randomEmail();
            final var role = DomainGenerators.randomEmployeeRole();

            // when
            final var employee = Employee.of(key, fullName, email, role);

            // then
            assertThat(employee.corporateKey()).isEqualTo(key);
            assertThat(employee.fullName()).isEqualTo(fullName);
            assertThat(employee.email()).isEqualTo(email);
            assertThat(employee.role()).isEqualTo(role);
        }

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should create employee from convenience factory method with enum role via InstancioSource")
        void should_createEmployee_from_convenienceFactory(
                @Given(DomainGivenProviders.ValidCorporateKeyProvider.class) final String keyStr,
                @Given(DomainGivenProviders.ValidNameProvider.class) final String nameStr,
                @Given(DomainGivenProviders.ValidSurnameProvider.class) final String surnameStr,
                @Given(DomainGivenProviders.ValidEmailProvider.class) final String emailStr) {
            // given
            final var role = EmployeeRole.EMPLOYEE;

            // when
            final var employee = Employee.of(keyStr, nameStr, surnameStr, emailStr, role);

            // then
            assertThat(employee.corporateKey().value()).isEqualTo(keyStr.strip().toUpperCase());
            assertThat(employee.fullName().formatted()).isEqualTo("%s %s".formatted(nameStr.strip(), surnameStr.strip()));
            assertThat(employee.email().value()).isEqualTo(emailStr.strip().toLowerCase());
            assertThat(employee.role()).isEqualTo(role);
        }

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should create employee from convenience factory method with string role via InstancioSource")
        void should_createEmployee_from_convenienceFactory_withStringRole(
                @Given(DomainGivenProviders.ValidCorporateKeyProvider.class) final String keyStr,
                @Given(DomainGivenProviders.ValidNameProvider.class) final String nameStr,
                @Given(DomainGivenProviders.ValidSurnameProvider.class) final String surnameStr,
                @Given(DomainGivenProviders.ValidEmailProvider.class) final String emailStr) {
            // when
            final var employee = Employee.of(keyStr, nameStr, surnameStr, emailStr, "manager");

            // then
            assertThat(employee.corporateKey().value()).isEqualTo(keyStr.strip().toUpperCase());
            assertThat(employee.fullName().formatted()).isEqualTo("%s %s".formatted(nameStr.strip(), surnameStr.strip()));
            assertThat(employee.email().value()).isEqualTo(emailStr.strip().toLowerCase());
            assertThat(employee.role()).isEqualTo(EmployeeRole.MANAGER);
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when corporate key is null")
        void should_throwException_when_corporateKeyIsNull() {
            final var fullName = DomainGenerators.randomFullName();
            final var email = DomainGenerators.randomEmail();

            assertThatThrownBy(() -> new Employee(null, fullName, email, EmployeeRole.EMPLOYEE))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Corporate key cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when full name is null")
        void should_throwException_when_fullNameIsNull() {
            final var key = DomainGenerators.randomCorporateKey();
            final var email = DomainGenerators.randomEmail();

            assertThatThrownBy(() -> new Employee(key, null, email, EmployeeRole.EMPLOYEE))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Full name cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when email is null")
        void should_throwException_when_emailIsNull() {
            final var key = DomainGenerators.randomCorporateKey();
            final var fullName = DomainGenerators.randomFullName();

            assertThatThrownBy(() -> new Employee(key, fullName, null, EmployeeRole.EMPLOYEE))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Email cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when role is null")
        void should_throwException_when_roleIsNull() {
            final var key = DomainGenerators.randomCorporateKey();
            final var fullName = DomainGenerators.randomFullName();
            final var email = DomainGenerators.randomEmail();

            assertThatThrownBy(() -> new Employee(key, fullName, email, null))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Employee role cannot be null");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when all fields match")
        void should_beEqual_when_allFieldsMatch() {
            // given
            final var key = DomainGenerators.randomCorporateKey();
            final var fullName = DomainGenerators.randomFullName();
            final var email = DomainGenerators.randomEmail();
            final var role = DomainGenerators.randomEmployeeRole();

            final var emp1 = Employee.of(key, fullName, email, role);
            final var emp2 = Employee.of(key, fullName, email, role);

            // then
            assertThat(emp1).isEqualTo(emp2);
            assertThat(emp1.hashCode()).isEqualTo(emp2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when fields differ")
        void should_notBeEqual_when_fieldsDiffer() {
            // given
            final var emp1 = DomainGenerators.randomEmployee();
            final var emp2 = DomainGenerators.randomEmployee();

            // then
            assertThat(emp1).isNotEqualTo(emp2);
        }

        @Test
        @DisplayName("should maintain immutability and record semantics")
        void should_maintainImmutability() {
            // given
            final var employee = DomainGenerators.randomEmployee();

            // then
            assertThat(employee.getClass().isRecord()).isTrue();
        }
    }
}
