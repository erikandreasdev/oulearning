package com.example.oulearning.organization.application.employee;

import com.example.oulearning.organization.domain.employee.Email;
import com.example.oulearning.organization.domain.employee.Employee;
import com.example.oulearning.organization.domain.employee.EmployeeId;
import com.example.oulearning.organization.domain.employee.EmployeeRepository;
import com.example.oulearning.organization.domain.employee.FullName;
import com.example.oulearning.organization.domain.employee.IdGenerator;
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
