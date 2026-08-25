package com.example.oulearning.organization.application.employee.service;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.port.in.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.port.in.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.port.in.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.domain.model.*;
import com.example.oulearning.training.application.port.in.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.domain.employee.model.Employee;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import com.example.oulearning.organization.domain.employee.model.EmployeeTestFactory;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class DeleteEmployeeServiceTest {

    private final EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
    private final DeleteEmployeeService service = new DeleteEmployeeService(employeeRepository);

    @Test
    @DisplayName("given existing employee, when deleting, then employee is deactivated and saved")
    void givenExistingEmployee_whenDeleting_thenEmployeeIsDeactivatedAndSaved() {
        // given
        final var employee = EmployeeTestFactory.randomEmployee();
        when(employeeRepository.findById(employee.id())).thenReturn(Optional.of(employee));

        // when
        service.execute(employee.id());

        // then
        final var captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(captor.capture());
        final var saved = captor.getValue();
        assertThat(saved.id()).isEqualTo(employee.id());
        assertThat(saved.active()).isFalse();
    }

    @Test
    @DisplayName("given non-existing employee, when deleting, then throw EmployeeNotFoundException")
    void givenNonExistingEmployee_whenDeleting_thenThrowEmployeeNotFoundException() {
        // given
        final var id = EmployeeTestFactory.randomEmployeeId();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> service.execute(id))
                .isInstanceOf(EmployeeNotFoundException.class);
    }
}
