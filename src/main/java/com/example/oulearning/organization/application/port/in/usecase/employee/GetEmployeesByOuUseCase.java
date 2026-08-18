package com.example.oulearning.organization.application.port.in.usecase.employee;

import com.example.oulearning.organization.domain.employee.Employee;
import java.util.List;
import com.example.oulearning.organization.application.port.in.query.GetEmployeesByOuQuery;

/**
 * Use case to retrieve all Employees belonging to an Organizational Unit, optionally including all descendant subtrees.
 */
public interface GetEmployeesByOuUseCase {

    List<Employee> execute(GetEmployeesByOuQuery query);
}
