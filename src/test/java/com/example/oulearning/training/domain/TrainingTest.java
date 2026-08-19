package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.Email;
import com.example.oulearning.organization.domain.employee.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.OuId;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TrainingTest {

    private final TrainingId id = TrainingId.of(UUID.randomUUID());
    private final EmployeeId requestedBy = EmployeeId.of("EMP-001");
    private final OuId ouId = OuId.of(UUID.randomUUID());
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
        @DisplayName("should create training with all fields")
        void should_createTraining_withAllFields() {
            EmployeeId attendee = EmployeeId.of("EMP-002");

            Training training = Training.of(
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
            assertThat(training.requestedBy()).isEqualTo(requestedBy);
            assertThat(training.ouId()).isEqualTo(ouId);
            assertThat(training.name()).isEqualTo(name);
            assertThat(training.cost()).isEqualTo(cost);
            assertThat(training.hours()).isEqualTo(hours);
            assertThat(training.purpose()).isEqualTo(purpose);
            assertThat(training.typeId()).isEqualTo(typeId);
            assertThat(training.status()).isEqualTo(TrainingStatus.APPROVED);
            assertThat(training.managerReview()).contains(review);
            assertThat(training.createdAt()).isEqualTo(now);
            assertThat(training.updatedAt()).isEqualTo(now.plusSeconds(3600));
            assertThat(training.attendees()).containsExactly(attendee);
        }

        @Test
        @DisplayName("should throw NullPointerException when required parameters are null")
        void should_throwException_when_requiredNull() {
            assertThatThrownBy(() -> new Training(
                            null, requestedBy, ouId, name, cost, hours, purpose, typeId,
                            TrainingStatus.REQUESTED, null, now, now, Set.of()))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new Training(
                            id, null, ouId, name, cost, hours, purpose, typeId,
                            TrainingStatus.REQUESTED, null, now, now, Set.of()))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Identity and Equality")
    class IdentityAndEquality {

        @Test
        @DisplayName("should be equal when ids match")
        void should_beEqual_when_idsMatch() {
            Training t1 = Training.of(
                    id, requestedBy, ouId, name, cost, hours, purpose, typeId,
                    TrainingStatus.REQUESTED, null, now, now, Set.of());
            Training t2 = Training.of(
                    id,
                    EmployeeId.of("OTHER"),
                    OuId.of(UUID.randomUUID()),
                    TrainingName.of("Other Training"),
                    Cost.of(500.0, "EUR"),
                    Hours.of(8),
                    TrainingPurpose.departmentGoals(),
                    TypeId.of(UUID.randomUUID()),
                    TrainingStatus.APPROVED,
                    null,
                    now,
                    now,
                    Set.of());

            assertThat(t1).isEqualTo(t2);
            assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when ids differ")
        void should_notBeEqual_when_idsDiffer() {
            Training t1 = Training.of(
                    id, requestedBy, ouId, name, cost, hours, purpose, typeId,
                    TrainingStatus.REQUESTED, null, now, now, Set.of());
            Training t2 = Training.of(
                    TrainingId.of(UUID.randomUUID()), requestedBy, ouId, name, cost, hours, purpose, typeId,
                    TrainingStatus.REQUESTED, null, now, now, Set.of());

            assertThat(t1).isNotEqualTo(t2);
        }
    }
}
