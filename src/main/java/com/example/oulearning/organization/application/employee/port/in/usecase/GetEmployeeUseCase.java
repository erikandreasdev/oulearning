package com.example.oulearning.organization.application.employee.port.in;

import com.example.oulearning.organization.domain.employee.model.Employee;
import com.example.oulearning.organization.domain.employee.model.EmployeeId;

public interface GetEmployeeUseCase {
    Employee execute(EmployeeId id);
}
