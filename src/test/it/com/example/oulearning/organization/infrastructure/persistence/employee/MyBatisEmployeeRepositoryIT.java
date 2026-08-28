package com.example.oulearning.organization.infrastructure.persistence.employee;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.oulearning.AbstractOracleIntegrationTest;
import com.example.oulearning.organization.domain.employee.model.Email;
import com.example.oulearning.organization.domain.employee.model.Employee;
import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.employee.model.EmployeeTestFactory;
import com.example.oulearning.organization.domain.employee.model.FullName;
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
@Import({MyBatisEmployeeRepository.class, FlywayAutoConfiguration.class})
class MyBatisEmployeeRepositoryIT extends AbstractOracleIntegrationTest {

    @Autowired
    private MyBatisEmployeeRepository employeeRepository;

    @Test
    @DisplayName("given valid employee, when saving, then can be retrieved")
    void givenValidEmployee_whenSaving_thenCanBeRetrieved() {
        // given
        final var randomName = EmployeeTestFactory.randomNameString();
        final var randomSurname = EmployeeTestFactory.randomSurnameString();
        final var randomEmail = EmployeeTestFactory.randomEmailString();
        final var employee = Employee.create(
                new EmployeeId(1L),
                FullName.of(randomName, randomSurname),
                new Email(randomEmail));

        // when
        employeeRepository.save(employee);

        // then
        final var retrieved = employeeRepository.findById(new EmployeeId(1L));

        assertThat(retrieved).isPresent();
        final var emp = retrieved.orElseThrow();
        assertThat(emp.fullName().name().value()).isEqualTo(randomName);
        assertThat(emp.fullName().surname().value()).isEqualTo(randomSurname);
        assertThat(emp.email().value()).isEqualTo(randomEmail);
    }

    @Test
    @DisplayName("given existing employee, when updating, then changes are persisted")
    @Sql(scripts = {"/sql/cleanup-all.sql", "/sql/insert-employee.sql"})
    void givenExistingEmployee_whenUpdating_thenChangesArePersisted() {
        // given
        final var employeeId = new EmployeeId(2L);
        final var retrieved = employeeRepository.findById(employeeId).orElseThrow();

        final var randomName = EmployeeTestFactory.randomNameString();
        final var randomSurname = EmployeeTestFactory.randomSurnameString();
        final var randomEmail = EmployeeTestFactory.randomEmailString();

        final var updatedEmployee = retrieved
                .updateFullName(FullName.of(randomName, randomSurname))
                .updateEmail(new Email(randomEmail));

        // when
        employeeRepository.save(updatedEmployee);

        // then
        final var updated = employeeRepository.findById(employeeId);
        assertThat(updated).isPresent();
        final var emp = updated.orElseThrow();
        assertThat(emp.fullName().surname().value()).isEqualTo(randomSurname);
        assertThat(emp.email().value()).isEqualTo(randomEmail);
    }

    @Test
    @DisplayName("given active employee, when deactivating, then active flag is updated")
    @Sql(scripts = {"/sql/cleanup-all.sql", "/sql/insert-employee.sql"})
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
