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
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import com.example.oulearning.organization.domain.employee.model.EmployeeTestFactory;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetEmployeeServiceTest {

    private final EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
    private final GetEmployeeService service = new GetEmployeeService(employeeRepository);

    @Test
    @DisplayName("given existing employee id, when getting employee, then employee is returned")
    void givenExistingEmployeeId_whenGettingEmployee_thenEmployeeIsReturned() {
        // given
        final var employee = EmployeeTestFactory.randomEmployee();
        when(employeeRepository.findById(employee.id())).thenReturn(Optional.of(employee));

        // when
        final var result = service.execute(employee.id());

        // then
        assertThat(result).isEqualTo(employee);
    }

    @Test
    @DisplayName("given non-existing employee id, when getting employee, then throw EmployeeNotFoundException")
    void givenNonExistingEmployeeId_whenGettingEmployee_thenThrowEmployeeNotFoundException() {
        // given
        final var id = EmployeeTestFactory.randomEmployeeId();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> service.execute(id))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
