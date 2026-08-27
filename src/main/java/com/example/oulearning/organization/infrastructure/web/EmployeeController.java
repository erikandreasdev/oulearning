package com.example.oulearning.organization.infrastructure.web;

import com.example.oulearning.organization.application.employee.port.in.command.CreateEmployeeCommand;
import com.example.oulearning.organization.application.employee.port.in.command.UpdateEmployeeCommand;
import com.example.oulearning.organization.application.employee.port.in.usecase.CreateEmployeeUseCase;
import com.example.oulearning.organization.application.employee.port.in.usecase.DeleteEmployeeUseCase;
import com.example.oulearning.organization.application.employee.port.in.usecase.GetEmployeeUseCase;
import com.example.oulearning.organization.application.employee.port.in.usecase.UpdateEmployeeUseCase;
import com.example.oulearning.organization.domain.employee.model.Employee;
import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.infrastructure.web.api.EmployeesApi;
import com.example.oulearning.organization.infrastructure.web.dto.CreateEmployeeRequest;
import com.example.oulearning.organization.infrastructure.web.dto.EmployeeResponse;
import com.example.oulearning.organization.infrastructure.web.dto.UpdateEmployeeRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
class EmployeeController implements EmployeesApi {

    private final CreateEmployeeUseCase createEmployeeUseCase;
    private final GetEmployeeUseCase getEmployeeUseCase;
    private final UpdateEmployeeUseCase updateEmployeeUseCase;
    private final DeleteEmployeeUseCase deleteEmployeeUseCase;

    EmployeeController(
            final CreateEmployeeUseCase createEmployeeUseCase,
            final GetEmployeeUseCase getEmployeeUseCase,
            final UpdateEmployeeUseCase updateEmployeeUseCase,
            final DeleteEmployeeUseCase deleteEmployeeUseCase) {
        this.createEmployeeUseCase = createEmployeeUseCase;
        this.getEmployeeUseCase = getEmployeeUseCase;
        this.updateEmployeeUseCase = updateEmployeeUseCase;
        this.deleteEmployeeUseCase = deleteEmployeeUseCase;
    }

    @Override
    public ResponseEntity<EmployeeResponse> createEmployee(final CreateEmployeeRequest request) {
        final var command = new CreateEmployeeCommand(request.getName(), request.getSurname(), request.getEmail());
        final var id = createEmployeeUseCase.execute(command);
        final var employee = getEmployeeUseCase.execute(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(employee));
    }

    @Override
    public ResponseEntity<EmployeeResponse> getEmployee(final Long id) {
        final var employee = getEmployeeUseCase.execute(new EmployeeId(id));
        return ResponseEntity.ok(toResponse(employee));
    }

    @Override
    public ResponseEntity<EmployeeResponse> updateEmployee(final Long id, final UpdateEmployeeRequest request) {
        final var command = new UpdateEmployeeCommand(new EmployeeId(id), request.getName(), request.getSurname(), request.getEmail());
        updateEmployeeUseCase.execute(command);
        final var employee = getEmployeeUseCase.execute(new EmployeeId(id));
        return ResponseEntity.ok(toResponse(employee));
    }

    @Override
    public ResponseEntity<Void> deleteEmployee(final Long id) {
        deleteEmployeeUseCase.execute(new EmployeeId(id));
        return ResponseEntity.noContent().build();
    }

    private EmployeeResponse toResponse(final Employee employee) {
        final var response = new EmployeeResponse();
        response.setId(employee.id().value());
        response.setName(employee.fullName().name().value());
        response.setSurname(employee.fullName().surname().value());
        response.setEmail(employee.email().value());
        response.setActive(employee.active());
        return response;
    }
}
