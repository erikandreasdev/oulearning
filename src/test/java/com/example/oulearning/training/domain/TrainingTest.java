package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.EmployeeId;
import com.example.oulearning.organization.domain.employee.EmployeeTestFactory;
import com.example.oulearning.organization.domain.hierarchy.HierarchyTestFactory;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;
import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TrainingTest {

    private final TrainingId id = TrainingTestFactory.randomTrainingId();
    private final OrganizationalUnitId organizationalUnitId = HierarchyTestFactory.randomOrganizationalUnitId();
    private final EmployeeId employeeId = EmployeeTestFactory.randomEmployeeId();
    private final TypeId typeId = TrainingTestFactory.randomTypeId();
    private final TrainingName name = TrainingTestFactory.randomTrainingName();
    private final Hours hours = TrainingTestFactory.randomHours();
    private final Cost cost = TrainingTestFactory.randomCost();
    private final TrainingPurpose purpose = TrainingTestFactory.randomTrainingPurpose();
    private final Instant now = Instant.now();

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName(
                "given required fields, when creating Training with REQUESTED status, then training is created successfully")
        void givenRequiredFields_whenCreatingTrainingWithRequestedStatus_thenTrainingIsCreatedSuccessfully() {
            // given

            // when
            final var training =
                    Training.create(id, employeeId, organizationalUnitId, name, cost, hours, purpose, typeId, now);

            // then
            assertThat(training.id()).isEqualTo(id);
            assertThat(training.requestedBy()).isEqualTo(employeeId);
            assertThat(training.organizationalUnitId()).isEqualTo(organizationalUnitId);
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
        }

        @Test
        @DisplayName(
                "given null required parameters, when creating Training, then throw InvalidTrainingOperationException")
        void givenNullRequiredParameters_whenCreatingTraining_thenThrowInvalidTrainingOperationException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> Training.create(
                            null, employeeId, organizationalUnitId, name, cost, hours, purpose, typeId, now))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() ->
                            Training.create(id, null, organizationalUnitId, name, cost, hours, purpose, typeId, now))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Training.create(id, employeeId, null, name, cost, hours, purpose, typeId, now))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Training.create(
                            id, employeeId, organizationalUnitId, null, cost, hours, purpose, typeId, now))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Training.create(
                            id, employeeId, organizationalUnitId, name, null, hours, purpose, typeId, now))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Training.create(
                            id, employeeId, organizationalUnitId, name, cost, null, purpose, typeId, now))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Training.create(
                            id, employeeId, organizationalUnitId, name, cost, hours, null, typeId, now))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Training.create(
                            id, employeeId, organizationalUnitId, name, cost, hours, purpose, null, now))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Training.create(
                            id, employeeId, organizationalUnitId, name, cost, hours, purpose, typeId, null))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
        }
    }

    @Nested
    @DisplayName("Identity and Equality")
    class IdentityAndEquality {

        @Test
        @DisplayName("given trainings with same id, when comparing, then they are equal")
        void givenTrainingsWithSameId_whenComparing_thenTheyAreEqual() {
            // given
            final var t1 =
                    Training.create(id, employeeId, organizationalUnitId, name, cost, hours, purpose, typeId, now);
            final var t2 = Training.reconstitute(
                    id,
                    EmployeeTestFactory.randomEmployeeId(),
                    HierarchyTestFactory.randomOrganizationalUnitId(),
                    TrainingTestFactory.randomTrainingName(),
                    TrainingTestFactory.randomCost(),
                    TrainingTestFactory.randomHours(),
                    TrainingPurpose.departmentGoals(),
                    TrainingTestFactory.randomTypeId(),
                    TrainingStatus.APPROVED,
                    null,
                    now,
                    now,
                    Set.of(EmployeeTestFactory.randomEmployeeId()));

            // when

            // then
            assertThat(t1).isEqualTo(t2).hasSameHashCodeAs(t2);
        }

        @Test
        @DisplayName("given trainings with different ids, when comparing, then they are not equal")
        void givenTrainingsWithDifferentIds_whenComparing_thenTheyAreNotEqual() {
            // given
            final var t1 =
                    Training.create(id, employeeId, organizationalUnitId, name, cost, hours, purpose, typeId, now);
            final var t2 = Training.create(
                    TrainingTestFactory.randomTrainingId(),
                    employeeId,
                    organizationalUnitId,
                    name,
                    cost,
                    hours,
                    purpose,
                    typeId,
                    now);

            // when

            // then
            assertThat(t1).isNotEqualTo(t2);
        }

        @Test
        @DisplayName("given same training instance, when comparing, then they are equal")
        void givenSameTrainingInstance_whenComparing_thenTheyAreEqual() {
            // given
            final var training =
                    Training.create(id, employeeId, organizationalUnitId, name, cost, hours, purpose, typeId, now);

            // when

            // then
            assertThat(training).isEqualTo(training);
        }

        @Test
        @DisplayName("given null or different object type, when comparing, then they are not equal")
        void givenNullOrDifferentType_whenComparing_thenTheyAreNotEqual() {
            // given
            final var training =
                    Training.create(id, employeeId, organizationalUnitId, name, cost, hours, purpose, typeId, now);

            // when

            // then
            assertThat(training).isNotEqualTo(null).isNotEqualTo(new Object());
        }
    }
}
