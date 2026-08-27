package com.example.oulearning.training.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.training.domain.model.Cost;
import com.example.oulearning.training.domain.model.Hours;
import com.example.oulearning.training.domain.model.Training;
import com.example.oulearning.training.domain.model.TrainingId;
import com.example.oulearning.training.domain.model.TrainingName;
import com.example.oulearning.training.domain.model.TrainingPurpose;
import com.example.oulearning.training.domain.model.TypeId;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MyBatisTrainingRepository.class, FlywayAutoConfiguration.class})
@Testcontainers
class MyBatisTrainingRepositoryIT {

    @Container
    @ServiceConnection
    static OracleContainer oracle = new OracleContainer("gvenzl/oracle-xe:21-slim");

    @Autowired
    private MyBatisTrainingRepository trainingRepository;

    @Test
    @DisplayName("given valid training, when saving, then can be retrieved")
    void givenValidTraining_whenSaving_thenCanBeRetrieved() {
        // given
        final var training = Training.create(
                new TrainingId(1L),
                new EmployeeId(10L),
                new OrganizationalUnitId(1L),
                new TrainingName("Java Masterclass"),
                Cost.of(new BigDecimal("1000.00"), "EUR"),
                new Hours(40),
                TrainingPurpose.individualDevelopmentPlan(),
                new TypeId(5L),
                Instant.now());

        // when
        trainingRepository.save(training);

        // then
        // Assuming ID is 1 for the first insert
        final var retrieved = trainingRepository.findById(new TrainingId(1L));

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().name().value()).isEqualTo("Java Masterclass");
        assertThat(retrieved.get().cost().amount()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(retrieved.get().cost().currency()).isEqualTo("EUR");
        assertThat(retrieved.get().hours().value()).isEqualTo(40);
        assertThat(retrieved.get().purpose().type().name()).isEqualTo("INDIVIDUAL_DEVELOPMENT_PLAN");
    }

    @Test
    @DisplayName("given existing training with attendees, when updating, then attendees are persisted")
    @Sql(scripts = "/sql/insert-training.sql")
    void givenExistingTrainingWithAttendees_whenUpdating_thenAttendeesArePersisted() {
        // given
        final var trainingId = new TrainingId(2L);

        var retrieved = trainingRepository.findById(trainingId).orElseThrow();

        retrieved = retrieved.addAttendee(new EmployeeId(100L), Instant.now());
        retrieved = retrieved.addAttendee(new EmployeeId(101L), Instant.now());

        // when
        trainingRepository.save(retrieved);

        // then
        final var updated = trainingRepository.findById(trainingId);
        assertThat(updated).isPresent();
        assertThat(updated.get().attendees()).hasSize(2);
        assertThat(updated.get().attendees()).containsExactlyInAnyOrder(new EmployeeId(100L), new EmployeeId(101L));
    }

    @Test
    @DisplayName("given training with attendees, when removing attendees, then attendees are deleted")
    @Sql(scripts = "/sql/insert-training.sql")
    void givenTrainingWithAttendees_whenRemovingAttendees_thenAttendeesAreDeleted() {
        // given
        final var trainingId = new TrainingId(2L);
        var retrieved = trainingRepository.findById(trainingId).orElseThrow();
        retrieved = retrieved.addAttendee(new EmployeeId(100L), Instant.now());
        trainingRepository.save(retrieved);

        retrieved = trainingRepository.findById(trainingId).orElseThrow();
        final var withoutAttendees = retrieved.removeAttendee(new EmployeeId(100L));

        // when
        trainingRepository.save(withoutAttendees);

        // then
        final var updated = trainingRepository.findById(trainingId);
        assertThat(updated).isPresent();
        assertThat(updated.get().attendees()).isEmpty();
    }

    @Test
    @DisplayName("given active training, when deactivating, then active flag is updated")
    @Sql(scripts = "/sql/insert-training.sql")
    void givenActiveTraining_whenDeactivating_thenActiveFlagIsUpdated() {
        // given
        final var trainingId = new TrainingId(2L);
        final var retrieved = trainingRepository.findById(trainingId).orElseThrow();
        final var deactivatedTraining = retrieved.deactivate();

        // when
        trainingRepository.save(deactivatedTraining);

        // then
        final var updated = trainingRepository.findById(trainingId);
        assertThat(updated).isPresent();
        assertThat(updated.get().active()).isFalse();
    }
}
