package com.example.oulearning.organization.application.employee.port.in.usecase;

import com.example.oulearning.organization.application.employee.port.in.command.CreateEmployeeCommand;
import com.example.oulearning.organization.domain.employee.model.EmployeeId;

public interface CreateEmployeeUseCase {
    EmployeeId execute(CreateEmployeeCommand command);
}
