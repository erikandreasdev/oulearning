package com.example.oulearning.organization.application.employee.port.in;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;

public interface DeleteEmployeeUseCase {
    void execute(EmployeeId id);
}
