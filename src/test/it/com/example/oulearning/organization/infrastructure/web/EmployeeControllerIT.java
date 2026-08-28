package com.example.oulearning.organization.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.oulearning.organization.OrganizationApiEndpoints;
import com.example.oulearning.organization.application.employee.port.in.command.CreateEmployeeCommand;
import com.example.oulearning.organization.application.employee.port.in.command.UpdateEmployeeCommand;
import com.example.oulearning.organization.application.employee.port.in.usecase.CreateEmployeeUseCase;
import com.example.oulearning.organization.application.employee.port.in.usecase.DeleteEmployeeUseCase;
import com.example.oulearning.organization.application.employee.port.in.usecase.GetEmployeeUseCase;
import com.example.oulearning.organization.application.employee.port.in.usecase.UpdateEmployeeUseCase;
import com.example.oulearning.organization.domain.employee.model.Email;
import com.example.oulearning.organization.domain.employee.model.Employee;
import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.employee.model.FullName;
import com.example.oulearning.organization.infrastructure.web.dto.CreateEmployeeRequest;
import com.example.oulearning.organization.infrastructure.web.dto.UpdateEmployeeRequest;
import com.example.oulearning.organization.domain.employee.model.EmployeeTestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateEmployeeUseCase createEmployeeUseCase;

    @MockitoBean
    private GetEmployeeUseCase getEmployeeUseCase;

    @MockitoBean
    private UpdateEmployeeUseCase updateEmployeeUseCase;

    @MockitoBean
    private DeleteEmployeeUseCase deleteEmployeeUseCase;

    @Test
    @DisplayName("given valid request, when creating employee, then returns 201 and employee data")
    void givenValidRequest_whenCreatingEmployee_thenReturns201() throws Exception {
        // given
        final var randomName = EmployeeTestFactory.randomNameString();
        final var randomSurname = EmployeeTestFactory.randomSurnameString();
        final var randomEmail = EmployeeTestFactory.randomEmailString();
        final var request = new CreateEmployeeRequest();
        request.setName(randomName);
        request.setSurname(randomSurname);
        request.setEmail(randomEmail);

        final var employeeId = new EmployeeId(EmployeeTestFactory.randomId());
        final var employee = Employee.reconstitute(
                employeeId,
                FullName.of(randomName, randomSurname),
                new Email(randomEmail),
                true);

        given(createEmployeeUseCase.execute(any(CreateEmployeeCommand.class))).willReturn(employeeId);
        given(getEmployeeUseCase.execute(employeeId)).willReturn(employee);

        // when
        final var result = mockMvc.perform(post(OrganizationApiEndpoints.EMPLOYEES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(employeeId.value()))
                .andExpect(jsonPath("$.name").value(randomName))
                .andExpect(jsonPath("$.surname").value(randomSurname))
                .andExpect(jsonPath("$.email").value(randomEmail))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("given existing employee id, when getting employee, then returns 200 and employee data")
    void givenExistingEmployeeId_whenGettingEmployee_thenReturns200() throws Exception {
        // given
        final var randomName = EmployeeTestFactory.randomNameString();
        final var randomSurname = EmployeeTestFactory.randomSurnameString();
        final var randomEmail = EmployeeTestFactory.randomEmailString();
        final var employeeId = new EmployeeId(EmployeeTestFactory.randomId());
        
        final var employee = Employee.reconstitute(
                employeeId,
                FullName.of(randomName, randomSurname),
                new Email(randomEmail),
                true);

        given(getEmployeeUseCase.execute(employeeId)).willReturn(employee);

        // when
        final var result = mockMvc.perform(get(OrganizationApiEndpoints.EMPLOYEE_BY_ID.formatted(employeeId.value())));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employeeId.value()))
                .andExpect(jsonPath("$.name").value(randomName))
                .andExpect(jsonPath("$.surname").value(randomSurname))
                .andExpect(jsonPath("$.email").value(randomEmail))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("given valid request, when updating employee, then returns 200 and updated employee data")
    void givenValidRequest_whenUpdatingEmployee_thenReturns200() throws Exception {
        // given
        final var randomName = EmployeeTestFactory.randomNameString();
        final var randomSurname = EmployeeTestFactory.randomSurnameString();
        final var randomEmail = EmployeeTestFactory.randomEmailString();
        final var request = new UpdateEmployeeRequest();
        request.setName(randomName);
        request.setSurname(randomSurname);
        request.setEmail(randomEmail);

        final var employeeId = new EmployeeId(EmployeeTestFactory.randomId());
        final var employee = Employee.reconstitute(
                employeeId,
                FullName.of(randomName, randomSurname),
                new Email(randomEmail),
                true);

        given(getEmployeeUseCase.execute(employeeId)).willReturn(employee);

        // when
        final var result = mockMvc.perform(put(OrganizationApiEndpoints.EMPLOYEE_BY_ID.formatted(employeeId.value()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employeeId.value()))
                .andExpect(jsonPath("$.name").value(randomName));

        verify(updateEmployeeUseCase).execute(any(UpdateEmployeeCommand.class));
    }

    @Test
    @DisplayName("given existing employee id, when deleting employee, then returns 204")
    void givenExistingEmployeeId_whenDeletingEmployee_thenReturns204() throws Exception {
        // given
        final var employeeId = new EmployeeId(EmployeeTestFactory.randomId());

        // when
        final var result = mockMvc.perform(delete(OrganizationApiEndpoints.EMPLOYEE_BY_ID.formatted(employeeId.value())));

        // then
        result.andExpect(status().isNoContent());

        verify(deleteEmployeeUseCase).execute(employeeId);
    }
}
