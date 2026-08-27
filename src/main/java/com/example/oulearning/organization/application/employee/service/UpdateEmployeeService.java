package com.example.oulearning.organization.application.employee.service;

import com.example.oulearning.organization.application.employee.exception.EmployeeNotFoundException;
import com.example.oulearning.organization.application.employee.port.in.command.UpdateEmployeeCommand;
import com.example.oulearning.organization.application.employee.port.in.usecase.UpdateEmployeeUseCase;
import com.example.oulearning.organization.domain.employee.model.Email;
import com.example.oulearning.organization.domain.employee.model.FullName;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateEmployeeService implements UpdateEmployeeUseCase {

    private final EmployeeRepository employeeRepository;

    public UpdateEmployeeService(final EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void execute(final UpdateEmployeeCommand command) {
        final var employee = employeeRepository.findById(command.id())
                .orElseThrow(() -> new EmployeeNotFoundException(command.id()));
        final var updated = employee
                .updateFullName(FullName.of(command.name(), command.surname()))
                .updateEmail(Email.of(command.email()));
        employeeRepository.save(updated);
    }
}
