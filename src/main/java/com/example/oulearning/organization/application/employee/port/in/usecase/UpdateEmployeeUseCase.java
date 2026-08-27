package com.example.oulearning.organization.application.employee.port.in.usecase;

import com.example.oulearning.organization.application.employee.port.in.command.UpdateEmployeeCommand;

public interface UpdateEmployeeUseCase {
    void execute(UpdateEmployeeCommand command);
}
