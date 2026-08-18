package com.example.oulearning.organization.application.port.in.usecase.employee;

import com.example.oulearning.organization.application.port.in.command.RegisterEmployeeCommand;
/**
 * Use case to register a new Employee and assign them to an OU.
 */
public interface RegisterEmployeeUseCase {

    String execute(RegisterEmployeeCommand command);
}
