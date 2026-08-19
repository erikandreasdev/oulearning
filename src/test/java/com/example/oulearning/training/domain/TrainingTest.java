package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.Email;
import com.example.oulearning.training.domain.event.AttendeeAdded;
import com.example.oulearning.training.domain.event.AttendeeRemoved;
import com.example.oulearning.training.domain.event.TrainingApproved;
import com.example.oulearning.training.domain.event.TrainingRejected;
import com.example.oulearning.training.domain.event.TrainingRequested;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TrainingTest {

    private final Id id = Id.of(UUID.randomUUID());
    private final com.example.oulearning.organization.domain.employee.Id requestedBy =
            com.example.oulearning.organization.domain.employee.Id.of("EMP-001");
    private final com.example.oulearning.organization.domain.hierarchy.Id ouId =
            com.example.oulearning.organization.domain.hierarchy.Id.of(UUID.randomUUID());
    private final TrainingName name = TrainingName.of("Domain-Driven Design Masterclass");
    private final Cost cost = Cost.of(1200.0, "EUR");
    private final Hours hours = Hours.of(16);
    private final TrainingPurpose purpose = TrainingPurpose.idp();
    private final TypeId typeId = TypeId.of(UUID.randomUUID());
    private final Instant now = Instant.parse("2026-08-19T10:00:00Z");

    private final ManagerReview review = new ManagerReview(
            "Approved budget and schedule",
            Modality.VIRTUAL,
            Instant.parse("2026-09-01T09:00:00Z"),
            Instant.parse("2026-09-02T17:00:00Z"),
            ExternalProvider.of(
                    ExternalProviderName.of("DDD Academy"),
                    ExternalProviderContact.of(Email.of("contact@dddacademy.com"), Phone.of("+1234567890"))),
            Instant.parse("2026-08-20T10:00:00Z"));

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("should request training and register TrainingRequested event")
        void should_requestTraining_and_registerEvent() {
            Training training = Training.request(id, requestedBy, ouId, name, cost, hours, purpose, typeId, now);

            assertThat(training.id()).isEqualTo(id);
            assertThat(training.requestedBy()).isEqualTo(requestedBy);
            assertThat(training.ouId()).isEqualTo(ouId);
            assertThat(training.name()).isEqualTo(name);
            assertThat(training.cost()).isEqualTo(cost);
            assertThat(training.hours()).isEqualTo(hours);
            assertThat(training.purpose()).isEqualTo(purpose);
            assertThat(training.typeId()).isEqualTo(typeId);
            assertThat(training.status()).isEqualTo(TrainingStatus.REQUESTED);
            assertThat(training.managerReview()).isEmpty();
            assertThat(training.createdAt()).isEqualTo(now);
            assertThat(training.updatedAt()).isEqualTo(now);
            assertThat(training.attendees()).isEmpty();

            List<Object> events = training.pullDomainEvents();
            assertThat(events).containsExactly(
                    new TrainingRequested(id, requestedBy, ouId, name, cost, hours, purpose, typeId, now));
            assertThat(training.pullDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("should reconstitute training without registering events")
        void should_reconstituteTraining_withoutEvents() {
            com.example.oulearning.organization.domain.employee.Id attendee =
                    com.example.oulearning.organization.domain.employee.Id.of("EMP-002");

            Training training = Training.reconstitute(
                    id,
                    requestedBy,
                    ouId,
                    name,
                    cost,
                    hours,
                    purpose,
                    typeId,
                    TrainingStatus.APPROVED,
                    review,
                    now,
                    now.plusSeconds(3600),
                    Set.of(attendee));

            assertThat(training.id()).isEqualTo(id);
            assertThat(training.status()).isEqualTo(TrainingStatus.APPROVED);
            assertThat(training.managerReview()).contains(review);
            assertThat(training.attendees()).containsExactly(attendee);
            assertThat(training.pullDomainEvents()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Approval and Rejection Lifecycle")
    class Lifecycle {

        @Test
        @DisplayName("should approve requested training and register TrainingApproved event")
        void should_approveTraining_and_registerEvent() {
            Training training = Training.request(id, requestedBy, ouId, name, cost, hours, purpose, typeId, now);
            Instant approvalTime = now.plusSeconds(3600);

            training.approve(review, approvalTime);

            assertThat(training.status()).isEqualTo(TrainingStatus.APPROVED);
            assertThat(training.managerReview()).contains(review);
            assertThat(training.updatedAt()).isEqualTo(approvalTime);

            List<Object> events = training.pullDomainEvents();
            assertThat(events).hasSize(2); // TrainingRequested + TrainingApproved
            assertThat(events.get(1)).isEqualTo(new TrainingApproved(id, review, approvalTime));
        }

        @Test
        @DisplayName("should reject requested training and register TrainingRejected event")
        void should_rejectTraining_and_registerEvent() {
            Training training = Training.request(id, requestedBy, ouId, name, cost, hours, purpose, typeId, now);
            Instant rejectTime = now.plusSeconds(3600);

            training.reject(review, rejectTime);

            assertThat(training.status()).isEqualTo(TrainingStatus.REJECTED);
            assertThat(training.managerReview()).contains(review);
            assertThat(training.updatedAt()).isEqualTo(rejectTime);

            List<Object> events = training.pullDomainEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(1)).isEqualTo(new TrainingRejected(id, review, rejectTime));
        }

        @Test
        @DisplayName("should throw InvalidTrainingStateException when approving already approved training")
        void should_throwException_when_approvingAlreadyApproved() {
            Training training = Training.request(id, requestedBy, ouId, name, cost, hours, purpose, typeId, now);
            training.approve(review, now);

            assertThatThrownBy(() -> training.approve(review, now.plusSeconds(3600)))
                    .isInstanceOf(InvalidTrainingStateException.class)
                    .hasMessageContaining("Cannot approve training in APPROVED status");
        }

        @Test
        @DisplayName("should throw InvalidTrainingStateException when rejecting already approved training")
        void should_throwException_when_rejectingAlreadyApproved() {
            Training training = Training.request(id, requestedBy, ouId, name, cost, hours, purpose, typeId, now);
            training.approve(review, now);

            assertThatThrownBy(() -> training.reject(review, now.plusSeconds(3600)))
                    .isInstanceOf(InvalidTrainingStateException.class)
                    .hasMessageContaining("Cannot reject training in APPROVED status");
        }
    }

    @Nested
    @DisplayName("Attendee Management")
    class AttendeeManagement {

        @Test
        @DisplayName("should add attendee and register AttendeeAdded event")
        void should_addAttendee_and_registerEvent() {
            Training training = Training.request(id, requestedBy, ouId, name, cost, hours, purpose, typeId, now);
            training.pullDomainEvents(); // Clear creation event

            com.example.oulearning.organization.domain.employee.Id attendee =
                    com.example.oulearning.organization.domain.employee.Id.of("EMP-003");
            Instant addTime = now.plusSeconds(3600);

            training.addAttendee(attendee, addTime);

            assertThat(training.attendees()).containsExactly(attendee);
            assertThat(training.pullDomainEvents()).containsExactly(new AttendeeAdded(id, attendee, addTime));
        }

        @Test
        @DisplayName("should not register event when adding duplicate attendee")
        void should_notRegisterEvent_when_duplicateAttendee() {
            com.example.oulearning.organization.domain.employee.Id attendee =
                    com.example.oulearning.organization.domain.employee.Id.of("EMP-003");

            Training training = Training.reconstitute(
                    id, requestedBy, ouId, name, cost, hours, purpose, typeId,
                    TrainingStatus.REQUESTED, null, now, now, Set.of(attendee));

            training.addAttendee(attendee, now);

            assertThat(training.pullDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("should remove attendee and register AttendeeRemoved event")
        void should_removeAttendee_and_registerEvent() {
            com.example.oulearning.organization.domain.employee.Id attendee =
                    com.example.oulearning.organization.domain.employee.Id.of("EMP-003");

            Training training = Training.reconstitute(
                    id, requestedBy, ouId, name, cost, hours, purpose, typeId,
                    TrainingStatus.REQUESTED, null, now, now, Set.of(attendee));

            Instant removeTime = now.plusSeconds(3600);
            training.removeAttendee(attendee, removeTime);

            assertThat(training.attendees()).isEmpty();
            assertThat(training.pullDomainEvents()).containsExactly(new AttendeeRemoved(id, attendee, removeTime));
        }
    }

    @Nested
    @DisplayName("Identity and Equality")
    class IdentityAndEquality {

        @Test
        @DisplayName("should be equal when ids match")
        void should_beEqual_when_idsMatch() {
            Training t1 = Training.request(id, requestedBy, ouId, name, cost, hours, purpose, typeId, now);
            Training t2 = Training.request(
                    id,
                    com.example.oulearning.organization.domain.employee.Id.of("OTHER"),
                    com.example.oulearning.organization.domain.hierarchy.Id.of(UUID.randomUUID()),
                    TrainingName.of("Other Training"),
                    Cost.of(500.0, "EUR"),
                    Hours.of(8),
                    TrainingPurpose.departmentGoals(),
                    TypeId.of(UUID.randomUUID()),
                    now);

            assertThat(t1).isEqualTo(t2);
            assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when ids differ")
        void should_notBeEqual_when_idsDiffer() {
            Training t1 = Training.request(id, requestedBy, ouId, name, cost, hours, purpose, typeId, now);
            Training t2 = Training.request(
                    Id.of(UUID.randomUUID()), requestedBy, ouId, name, cost, hours, purpose, typeId, now);

            assertThat(t1).isNotEqualTo(t2);
        }
    }
}
