package com.example.oulearning.organization.application;

import com.example.oulearning.organization.domain.employee.Employee;
import java.util.List;

/**
 * Use case to retrieve all Employees belonging to an Organizational Unit, optionally including all descendant subtrees.
 */
public interface GetEmployeesByOuUseCase {

    List<Employee> execute(GetEmployeesByOuQuery query);
}
