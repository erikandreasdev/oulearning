package com.example.oulearning.organization.infrastructure.persistence.employee;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.oulearning.organization.domain.employee.model.Email;
import com.example.oulearning.organization.domain.employee.model.Employee;
import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.employee.model.FullName;
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
@Import({MyBatisEmployeeRepository.class, FlywayAutoConfiguration.class})
@Testcontainers
class MyBatisEmployeeRepositoryIT {

    @Container
    @ServiceConnection
    static OracleContainer oracle = new OracleContainer(org.testcontainers.utility.DockerImageName.parse("gvenzl/oracle-free:23-slim").asCompatibleSubstituteFor("gvenzl/oracle-xe"));

    @Autowired
    private MyBatisEmployeeRepository employeeRepository;

    @Test
    @DisplayName("given valid employee, when saving, then can be retrieved")
    void givenValidEmployee_whenSaving_thenCanBeRetrieved() {
        // given
        final var employee = Employee.create(
                new EmployeeId(1L),
                FullName.of("John", "Doe"),
                new Email("john.doe@example.com"));

        // when
        employeeRepository.save(employee);

        // then
        // Assuming ID is 1 for the first insert
        final var retrieved = employeeRepository.findById(new EmployeeId(1L));

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().fullName().name().value()).isEqualTo("John");
        assertThat(retrieved.get().fullName().surname().value()).isEqualTo("Doe");
        assertThat(retrieved.get().email().value()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("given existing employee, when updating, then changes are persisted")
    @Sql(scripts = "/sql/insert-employee.sql")
    void givenExistingEmployee_whenUpdating_thenChangesArePersisted() {
        // given
        final var employeeId = new EmployeeId(2L);
        final var retrieved = employeeRepository.findById(employeeId).orElseThrow();

        final var updatedEmployee = retrieved
                .updateFullName(FullName.of("Jane", "Doe"))
                .updateEmail(new Email("jane.doe@example.com"));

        // when
        employeeRepository.save(updatedEmployee);

        // then
        final var updated = employeeRepository.findById(employeeId);
        assertThat(updated).isPresent();
        assertThat(updated.get().fullName().surname().value()).isEqualTo("Doe");
        assertThat(updated.get().email().value()).isEqualTo("jane.doe@example.com");
    }

    @Test
    @DisplayName("given active employee, when deactivating, then active flag is updated")
    @Sql(scripts = "/sql/insert-employee.sql")
    void givenActiveEmployee_whenDeactivating_thenActiveFlagIsUpdated() {
        // given
        final var employeeId = new EmployeeId(2L);
        final var retrieved = employeeRepository.findById(employeeId).orElseThrow();
        final var deactivatedEmployee = retrieved.deactivate();

        // when
        employeeRepository.save(deactivatedEmployee);

        // then
        final var updated = employeeRepository.findById(employeeId);
        assertThat(updated).isPresent();
        assertThat(updated.get().active()).isFalse();
    }
}
