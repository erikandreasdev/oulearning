package com.example.oulearning.organization.application;

import com.example.oulearning.organization.domain.employee.Employee;
import java.util.Optional;

/**
 * Use case to retrieve an Employee by CorporateKey.
 */
public interface GetEmployeeUseCase {

    Optional<Employee> execute(GetEmployeeQuery query);
}
