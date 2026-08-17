package com.example.oulearning.organization.domain.employee;

import com.example.oulearning.organization.domain.employee.exception.InvalidEmployeeException;

/**
 * Value object representing an Employee.
 */
public record Employee(CorporateKey corporateKey, FullName fullName, Email email, EmployeeRole role) {

    public Employee {
        if (corporateKey == null) {
            throw new InvalidEmployeeException("CorporateKey cannot be null");
        }
        if (fullName == null) {
            throw new InvalidEmployeeException("FullName cannot be null");
        }
        if (email == null) {
            throw new InvalidEmployeeException("Email cannot be null");
        }
        if (role == null) {
            throw new InvalidEmployeeException("EmployeeRole cannot be null");
        }
    }

    public static Employee of(CorporateKey corporateKey, FullName fullName, Email email, EmployeeRole role) {
        return new Employee(corporateKey, fullName, email, role);
    }
}
