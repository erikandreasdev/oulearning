package com.example.oulearning.organization.application.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.domain.employee.Employee;
import com.example.oulearning.organization.domain.employee.EmployeeRepository;
import com.example.oulearning.organization.domain.employee.EmployeeTestFactory;
import com.example.oulearning.organization.domain.employee.IdGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CreateEmployeeServiceTest {

    private final EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
    private final IdGenerator idGenerator = mock(IdGenerator.class);
    private final CreateEmployeeService service = new CreateEmployeeService(employeeRepository, idGenerator);

    @Test
    @DisplayName("given valid command, when creating employee, then employee is saved and id is returned")
    void givenValidCommand_whenCreatingEmployee_thenEmployeeIsSavedAndIdReturned() {
        // given
        final var generatedId = EmployeeTestFactory.randomId();
        final var name = EmployeeTestFactory.randomNameString();
        final var surname = EmployeeTestFactory.randomSurnameString();
        final var email = EmployeeTestFactory.randomEmailString();
        final var command = new CreateEmployeeCommand(name, surname, email);
        when(idGenerator.generate()).thenReturn(generatedId);

        // when
        final var resultId = service.execute(command);

        // then
        assertThat(resultId.value()).isEqualTo(generatedId);
        final var captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(captor.capture());
        final var saved = captor.getValue();
        assertThat(saved.id().value()).isEqualTo(generatedId);
        assertThat(saved.fullName().name().value()).isEqualTo(name);
        assertThat(saved.fullName().surname().value()).isEqualTo(surname);
        assertThat(saved.email().value()).isEqualTo(email);
        assertThat(saved.active()).isTrue();
    }
}
