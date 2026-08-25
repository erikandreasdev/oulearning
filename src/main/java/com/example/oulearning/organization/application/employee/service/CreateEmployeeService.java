package com.example.oulearning.organization.application.employee.service;

import com.example.oulearning.organization.application.employee.port.in.*;
import com.example.oulearning.organization.application.employee.exception.*;

import com.example.oulearning.organization.domain.employee.model.Email;
import com.example.oulearning.organization.domain.employee.model.Employee;
import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import com.example.oulearning.organization.domain.employee.model.FullName;
import com.example.oulearning.organization.domain.employee.model.IdGenerator;
import org.springframework.stereotype.Service;

@Service
public class CreateEmployeeService implements CreateEmployeeUseCase {

    private final EmployeeRepository employeeRepository;
    private final IdGenerator idGenerator;

    public CreateEmployeeService(final EmployeeRepository employeeRepository, final IdGenerator idGenerator) {
        this.employeeRepository = employeeRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public EmployeeId execute(final CreateEmployeeCommand command) {
        final var id = EmployeeId.of(idGenerator.generate());
        final var fullName = FullName.of(command.name(), command.surname());
        final var email = Email.of(command.email());
        final var employee = Employee.create(id, fullName, email);
        employeeRepository.save(employee);
        return id;
    }
}
