package com.example.oulearning.organization.application.employee;

import com.example.oulearning.organization.domain.employee.EmployeeId;

public interface CreateEmployeeUseCase {
    EmployeeId execute(CreateEmployeeCommand command);
}
