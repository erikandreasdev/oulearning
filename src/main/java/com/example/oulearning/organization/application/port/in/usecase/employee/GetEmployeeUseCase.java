package com.example.oulearning.organization.application.port.in.usecase.employee;

import com.example.oulearning.organization.domain.employee.Employee;
import java.util.Optional;
import com.example.oulearning.organization.application.port.in.query.GetEmployeeQuery;

/**
 * Use case to retrieve an Employee by CorporateKey.
 */
public interface GetEmployeeUseCase {

    Optional<Employee> execute(GetEmployeeQuery query);
}
