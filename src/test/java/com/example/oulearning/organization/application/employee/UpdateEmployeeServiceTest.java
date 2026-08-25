package com.example.oulearning.organization.application.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.domain.employee.Employee;
import com.example.oulearning.organization.domain.employee.EmployeeRepository;
import com.example.oulearning.organization.domain.employee.EmployeeTestFactory;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class UpdateEmployeeServiceTest {

    private final EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
    private final UpdateEmployeeService service = new UpdateEmployeeService(employeeRepository);

    @Test
    @DisplayName("given existing employee, when updating, then updated employee is saved")
    void givenExistingEmployee_whenUpdating_thenUpdatedEmployeeIsSaved() {
        // given
        final var employee = EmployeeTestFactory.randomEmployee();
        final var newName = EmployeeTestFactory.randomNameString();
        final var newSurname = EmployeeTestFactory.randomSurnameString();
        final var newEmail = EmployeeTestFactory.randomEmailString();
        final var command = new UpdateEmployeeCommand(employee.id(), newName, newSurname, newEmail);
        when(employeeRepository.findById(employee.id())).thenReturn(Optional.of(employee));

        // when
        service.execute(command);

        // then
        final var captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(captor.capture());
        final var saved = captor.getValue();
        assertThat(saved.id()).isEqualTo(employee.id());
        assertThat(saved.fullName().name().value()).isEqualTo(newName);
        assertThat(saved.fullName().surname().value()).isEqualTo(newSurname);
        assertThat(saved.email().value()).isEqualTo(newEmail);
    }

    @Test
    @DisplayName("given non-existing employee, when updating, then throw EmployeeNotFoundException")
    void givenNonExistingEmployee_whenUpdating_thenThrowEmployeeNotFoundException() {
        // given
        final var id = EmployeeTestFactory.randomEmployeeId();
        final var command = new UpdateEmployeeCommand(
                id,
                EmployeeTestFactory.randomNameString(),
                EmployeeTestFactory.randomSurnameString(),
                EmployeeTestFactory.randomEmailString());
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(EmployeeNotFoundException.class);
    }
}
