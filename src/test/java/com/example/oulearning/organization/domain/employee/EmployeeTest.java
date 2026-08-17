package com.example.oulearning.organization.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.DomainGenerators;
import com.example.oulearning.organization.domain.employee.exception.InvalidEmployeeException;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InstancioExtension.class)
class EmployeeTest {

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("should create Employee with valid attributes")
        void should_createEmployee_when_validAttributes() {
            final var corporateKey = DomainGenerators.randomCorporateKey();
            final var fullName = DomainGenerators.randomFullName();
            final var email = DomainGenerators.randomEmail();
            final var role = DomainGenerators.randomEmployeeRole();

            final var employee = Employee.of(corporateKey, fullName, email, role);

            assertThat(employee.corporateKey()).isEqualTo(corporateKey);
            assertThat(employee.fullName()).isEqualTo(fullName);
            assertThat(employee.email()).isEqualTo(email);
            assertThat(employee.role()).isEqualTo(role);
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when corporateKey is null")
        void should_throwException_when_corporateKeyIsNull() {
            final var fullName = DomainGenerators.randomFullName();
            final var email = DomainGenerators.randomEmail();
            final var role = DomainGenerators.randomEmployeeRole();

            assertThatThrownBy(() -> Employee.of(null, fullName, email, role))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("CorporateKey cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when fullName is null")
        void should_throwException_when_fullNameIsNull() {
            final var corporateKey = DomainGenerators.randomCorporateKey();
            final var email = DomainGenerators.randomEmail();
            final var role = DomainGenerators.randomEmployeeRole();

            assertThatThrownBy(() -> Employee.of(corporateKey, null, email, role))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("FullName cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when email is null")
        void should_throwException_when_emailIsNull() {
            final var corporateKey = DomainGenerators.randomCorporateKey();
            final var fullName = DomainGenerators.randomFullName();
            final var role = DomainGenerators.randomEmployeeRole();

            assertThatThrownBy(() -> Employee.of(corporateKey, fullName, null, role))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Email cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when role is null")
        void should_throwException_when_roleIsNull() {
            final var corporateKey = DomainGenerators.randomCorporateKey();
            final var fullName = DomainGenerators.randomFullName();
            final var email = DomainGenerators.randomEmail();

            assertThatThrownBy(() -> Employee.of(corporateKey, fullName, email, null))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("EmployeeRole cannot be null");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when all fields match")
        void should_beEqual_when_allFieldsMatch() {
            final var ck = DomainGenerators.randomCorporateKey();
            final var fn = DomainGenerators.randomFullName();
            final var em = DomainGenerators.randomEmail();
            final var role = DomainGenerators.randomEmployeeRole();

            final var emp1 = Employee.of(ck, fn, em, role);
            final var emp2 = Employee.of(ck, fn, em, role);

            assertThat(emp1).isEqualTo(emp2);
            assertThat(emp1.hashCode()).isEqualTo(emp2.hashCode());
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var employee = DomainGenerators.randomEmployee();
            assertThat(employee.getClass().isRecord()).isTrue();
        }
    }
}
