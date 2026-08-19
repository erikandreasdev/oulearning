package com.example.oulearning.organization.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.event.EmailChanged;
import com.example.oulearning.organization.domain.employee.event.EmployeeCreated;
import com.example.oulearning.organization.domain.employee.event.FullNameChanged;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    private final Id id = Id.of("EMP-001");
    private final FullName fullName = FullName.of("Jane", "Doe");
    private final Email email = Email.of("jane.doe@example.com");
    private final Instant now = Instant.parse("2026-08-19T10:00:00Z");

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("should create employee and register EmployeeCreated event")
        void should_createEmployee_and_registerEvent() {
            Employee employee = Employee.create(id, fullName, email, now);

            assertThat(employee.id()).isEqualTo(id);
            assertThat(employee.fullName()).isEqualTo(fullName);
            assertThat(employee.email()).isEqualTo(email);

            List<Object> events = employee.pullDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.getFirst()).isEqualTo(new EmployeeCreated(id, fullName, email, now));

            assertThat(employee.pullDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("should reconstitute employee without registering events")
        void should_reconstituteEmployee_withoutEvents() {
            Employee employee = Employee.reconstitute(id, fullName, email);

            assertThat(employee.id()).isEqualTo(id);
            assertThat(employee.fullName()).isEqualTo(fullName);
            assertThat(employee.email()).isEqualTo(email);
            assertThat(employee.pullDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("should throw exception when creating employee with null parameters")
        void should_throwException_when_nullParams() {
            assertThatThrownBy(() -> Employee.create(null, fullName, email, now))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> Employee.create(id, null, email, now))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> Employee.create(id, fullName, null, now))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> Employee.create(id, fullName, email, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("State Changes")
    class StateChanges {

        @Test
        @DisplayName("should change full name and register FullNameChanged event")
        void should_changeFullName_and_registerEvent() {
            Employee employee = Employee.reconstitute(id, fullName, email);
            FullName newName = FullName.of("Janet", "Smith");
            Instant changeTime = now.plusSeconds(3600);

            employee.changeFullName(newName, changeTime);

            assertThat(employee.fullName()).isEqualTo(newName);
            List<Object> events = employee.pullDomainEvents();
            assertThat(events).containsExactly(new FullNameChanged(id, fullName, newName, changeTime));
        }

        @Test
        @DisplayName("should not register FullNameChanged event when name is identical")
        void should_notRegisterEvent_when_nameIdentical() {
            Employee employee = Employee.reconstitute(id, fullName, email);

            employee.changeFullName(fullName, now.plusSeconds(3600));

            assertThat(employee.pullDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("should change email and register EmailChanged event")
        void should_changeEmail_and_registerEvent() {
            Employee employee = Employee.reconstitute(id, fullName, email);
            Email newEmail = Email.of("janet.smith@example.com");
            Instant changeTime = now.plusSeconds(3600);

            employee.changeEmail(newEmail, changeTime);

            assertThat(employee.email()).isEqualTo(newEmail);
            List<Object> events = employee.pullDomainEvents();
            assertThat(events).containsExactly(new EmailChanged(id, email, newEmail, changeTime));
        }

        @Test
        @DisplayName("should not register EmailChanged event when email is identical")
        void should_notRegisterEvent_when_emailIdentical() {
            Employee employee = Employee.reconstitute(id, fullName, email);

            employee.changeEmail(email, now.plusSeconds(3600));

            assertThat(employee.pullDomainEvents()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Identity and Equality")
    class IdentityAndEquality {

        @Test
        @DisplayName("should be equal when ids match regardless of other fields")
        void should_beEqual_when_idsMatch() {
            Employee employee1 = Employee.reconstitute(id, fullName, email);
            Employee employee2 = Employee.reconstitute(id, FullName.of("Other", "Name"), Email.of("other@example.com"));

            assertThat(employee1).isEqualTo(employee2);
            assertThat(employee1.hashCode()).isEqualTo(employee2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when ids differ")
        void should_notBeEqual_when_idsDiffer() {
            Employee employee1 = Employee.reconstitute(id, fullName, email);
            Employee employee2 = Employee.reconstitute(Id.of("EMP-002"), fullName, email);

            assertThat(employee1).isNotEqualTo(employee2);
        }
    }
}
