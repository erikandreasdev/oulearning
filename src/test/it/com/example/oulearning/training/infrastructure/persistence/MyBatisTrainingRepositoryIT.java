package com.example.oulearning.training.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.oulearning.AbstractOracleIntegrationTest;
import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.training.domain.model.Cost;
import com.example.oulearning.training.domain.model.Hours;
import com.example.oulearning.training.domain.model.Training;
import com.example.oulearning.training.domain.model.TrainingId;
import com.example.oulearning.training.domain.model.TrainingName;
import com.example.oulearning.training.domain.model.TrainingPurpose;
import com.example.oulearning.training.domain.model.TrainingTestFactory;
import com.example.oulearning.training.domain.model.TypeId;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MyBatisTrainingRepository.class, FlywayAutoConfiguration.class})
class MyBatisTrainingRepositoryIT extends AbstractOracleIntegrationTest {

    @Autowired
    private MyBatisTrainingRepository trainingRepository;

    @Test
    @DisplayName("given valid training, when saving, then can be retrieved")
    @Sql(scripts = {"/sql/cleanup-all.sql", "/sql/insert-training.sql"})
    void givenValidTraining_whenSaving_thenCanBeRetrieved() {
        // given
        final var randomName = TrainingTestFactory.randomTrainingNameString();
        final var randomCost = TrainingTestFactory.randomBigDecimalCostAmount();
        final var randomHours = TrainingTestFactory.randomHoursValue();

        final var training = Training.create(
                new TrainingId(1L),
                new EmployeeId(10L),
                new OrganizationalUnitId(2L),
                new TrainingName(randomName),
                Cost.of(randomCost, "EUR"),
                new Hours(randomHours),
                TrainingPurpose.individualDevelopmentPlan(),
                new TypeId(5L),
                Instant.now());

        // when
        trainingRepository.save(training);

        // then
        final var retrieved = trainingRepository.findById(new TrainingId(1L));

        assertThat(retrieved).isPresent();
        final var tr = retrieved.orElseThrow();
        assertThat(tr.name().value()).isEqualTo(randomName);
    }

    @Test
    @DisplayName("given existing training with attendees, when updating, then attendees are persisted")
    @Sql(scripts = {"/sql/cleanup-all.sql", "/sql/insert-training.sql"})
    void givenExistingTrainingWithAttendees_whenUpdating_thenAttendeesArePersisted() {
        // given
        final var trainingId = new TrainingId(2L);

        var retrieved = trainingRepository.findById(trainingId).orElseThrow();

        retrieved = retrieved.addAttendee(new EmployeeId(10L), Instant.now());
        retrieved = retrieved.addAttendee(new EmployeeId(11L), Instant.now());

        // when
        trainingRepository.save(retrieved);

        // then
        final var updated = trainingRepository.findById(trainingId);
        assertThat(updated).isPresent();
        final var tr = updated.orElseThrow();
        assertThat(tr.attendees()).hasSize(2);
        assertThat(tr.attendees()).containsExactlyInAnyOrder(new EmployeeId(10L), new EmployeeId(11L));
    }

    @Test
    @DisplayName("given training with attendees, when removing attendees, then attendees are deleted")
    @Sql(scripts = {"/sql/cleanup-all.sql", "/sql/insert-training.sql"})
    void givenTrainingWithAttendees_whenRemovingAttendees_thenAttendeesAreDeleted() {
        // given
        final var trainingId = new TrainingId(2L);
        var retrieved = trainingRepository.findById(trainingId).orElseThrow();
        retrieved = retrieved.addAttendee(new EmployeeId(11L), Instant.now());
        trainingRepository.save(retrieved);

        retrieved = trainingRepository.findById(trainingId).orElseThrow();
        final var withoutAttendees = retrieved.removeAttendee(new EmployeeId(11L), Instant.now());

        // when
        trainingRepository.save(withoutAttendees);

        // then
        final var updated = trainingRepository.findById(trainingId);
        assertThat(updated).isPresent();
        final var tr = updated.orElseThrow();
        assertThat(tr.attendees()).isEmpty();
    }

    @Test
    @DisplayName("given active training, when deactivating, then active flag is updated")
    @Sql(scripts = {"/sql/cleanup-all.sql", "/sql/insert-training.sql"})
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
        final var tr = updated.orElseThrow();
        assertThat(tr.active()).isFalse();
    }
}
