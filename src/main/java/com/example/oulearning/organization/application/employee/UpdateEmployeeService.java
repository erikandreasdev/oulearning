package com.example.oulearning.organization.application.employee;

import com.example.oulearning.organization.domain.employee.Email;
import com.example.oulearning.organization.domain.employee.EmployeeRepository;
import com.example.oulearning.organization.domain.employee.FullName;
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
