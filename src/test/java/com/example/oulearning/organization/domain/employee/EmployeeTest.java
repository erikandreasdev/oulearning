package com.example.oulearning.organization.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.DomainGenerators;
import com.example.oulearning.organization.domain.employee.exception.InvalidEmployeeException;
import com.example.oulearning.organization.domain.unit.OuId;
import java.util.UUID;
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
        @DisplayName("should create Employee with valid attributes including optional Phone and required OuId")
        void should_createEmployee_when_validAttributes() {
            final var corporateKey = DomainGenerators.randomCorporateKey();
            final var fullName = DomainGenerators.randomFullName();
            final var email = DomainGenerators.randomEmail();
            final var phone = DomainGenerators.randomPhone();
            final var role = DomainGenerators.randomEmployeeRole();
            final var ouId = DomainGenerators.randomOuId();

            final var employee = Employee.of(corporateKey, fullName, email, phone, role, ouId);

            assertThat(employee.corporateKey()).isEqualTo(corporateKey);
            assertThat(employee.fullName()).isEqualTo(fullName);
            assertThat(employee.email()).isEqualTo(email);
            assertThat(employee.phone()).isEqualTo(phone);
            assertThat(employee.optionalPhone()).contains(phone);
            assertThat(employee.role()).isEqualTo(role);
            assertThat(employee.ouId()).isEqualTo(ouId);
        }

        @Test
        @DisplayName("should allow null Phone upon creation")
        void should_allowNullPhone() {
            final var corporateKey = DomainGenerators.randomCorporateKey();
            final var fullName = DomainGenerators.randomFullName();
            final var email = DomainGenerators.randomEmail();
            final var role = DomainGenerators.randomEmployeeRole();
            final var ouId = DomainGenerators.randomOuId();

            final var employee = Employee.of(corporateKey, fullName, email, role, ouId);

            assertThat(employee.phone()).isNull();
            assertThat(employee.optionalPhone()).isEmpty();
            assertThat(employee.ouId()).isEqualTo(ouId);
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when corporateKey is null")
        void should_throwException_when_corporateKeyIsNull() {
            final var fullName = DomainGenerators.randomFullName();
            final var email = DomainGenerators.randomEmail();
            final var role = DomainGenerators.randomEmployeeRole();
            final var ouId = DomainGenerators.randomOuId();

            assertThatThrownBy(() -> Employee.of(null, fullName, email, role, ouId))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("CorporateKey cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when fullName is null")
        void should_throwException_when_fullNameIsNull() {
            final var corporateKey = DomainGenerators.randomCorporateKey();
            final var email = DomainGenerators.randomEmail();
            final var role = DomainGenerators.randomEmployeeRole();
            final var ouId = DomainGenerators.randomOuId();

            assertThatThrownBy(() -> Employee.of(corporateKey, null, email, role, ouId))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("FullName cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when email is null")
        void should_throwException_when_emailIsNull() {
            final var corporateKey = DomainGenerators.randomCorporateKey();
            final var fullName = DomainGenerators.randomFullName();
            final var role = DomainGenerators.randomEmployeeRole();
            final var ouId = DomainGenerators.randomOuId();

            assertThatThrownBy(() -> Employee.of(corporateKey, fullName, null, role, ouId))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Email cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when role is null")
        void should_throwException_when_roleIsNull() {
            final var corporateKey = DomainGenerators.randomCorporateKey();
            final var fullName = DomainGenerators.randomFullName();
            final var email = DomainGenerators.randomEmail();
            final var ouId = DomainGenerators.randomOuId();

            assertThatThrownBy(() -> Employee.of(corporateKey, fullName, email, null, ouId))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("EmployeeRole cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when ouId is null (single OU membership invariant)")
        void should_throwException_when_ouIdIsNull() {
            final var corporateKey = DomainGenerators.randomCorporateKey();
            final var fullName = DomainGenerators.randomFullName();
            final var email = DomainGenerators.randomEmail();
            final var role = DomainGenerators.randomEmployeeRole();

            assertThatThrownBy(() -> Employee.of(corporateKey, fullName, email, role, null))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("OuId cannot be null");
        }
    }

    @Nested
    @DisplayName("Domain Behavior & State Evolution")
    class DomainBehavior {

        @Test
        @DisplayName("should reassign employee to a new OU enforcing single membership")
        void should_reassignToNewOu() {
            final var employee = DomainGenerators.randomEmployee();
            final var newOuId = OuId.of(UUID.randomUUID());

            final var reassigned = employee.assignToOu(newOuId);

            assertThat(reassigned.ouId()).isEqualTo(newOuId);
            assertThat(reassigned.corporateKey()).isEqualTo(employee.corporateKey());
            assertThat(reassigned.fullName()).isEqualTo(employee.fullName());
        }

        @Test
        @DisplayName("should update employee contact and role details")
        void should_updateDetails() {
            final var employee = DomainGenerators.randomEmployee();
            final var newFullName = DomainGenerators.randomFullName();
            final var newEmail = Email.of("newemail@corp.com");
            final var newPhone = Phone.of("+34911223344");
            final var newRole = EmployeeRole.MANAGER;

            final var updated = employee.updateDetails(newFullName, newEmail, newPhone, newRole);

            assertThat(updated.fullName()).isEqualTo(newFullName);
            assertThat(updated.email()).isEqualTo(newEmail);
            assertThat(updated.phone()).isEqualTo(newPhone);
            assertThat(updated.role()).isEqualTo(newRole);
            assertThat(updated.ouId()).isEqualTo(employee.ouId());
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
            final var phone = DomainGenerators.randomPhone();
            final var role = DomainGenerators.randomEmployeeRole();
            final var ouId = DomainGenerators.randomOuId();

            final var emp1 = Employee.of(ck, fn, em, phone, role, ouId);
            final var emp2 = Employee.of(ck, fn, em, phone, role, ouId);

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
