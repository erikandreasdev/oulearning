package com.example.oulearning.organization.application.employee;

import com.example.oulearning.organization.domain.employee.Employee;
import com.example.oulearning.organization.domain.employee.EmployeeId;

public interface GetEmployeeUseCase {
    Employee execute(EmployeeId id);
}
