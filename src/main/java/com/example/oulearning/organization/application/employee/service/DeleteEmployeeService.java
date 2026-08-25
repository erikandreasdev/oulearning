package com.example.oulearning.organization.application.employee.service;

import com.example.oulearning.organization.application.employee.port.in.*;
import com.example.oulearning.organization.application.employee.exception.*;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteEmployeeService implements DeleteEmployeeUseCase {

    private final EmployeeRepository employeeRepository;

    public DeleteEmployeeService(final EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void execute(final EmployeeId id) {
        final var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        employeeRepository.save(employee.deactivate());
    }
}
