package com.example.oulearning.organization.domain.employee.repository;

import com.example.oulearning.organization.domain.employee.model.*;

import java.util.Optional;

public interface EmployeeRepository {
    Optional<Employee> findById(EmployeeId id);

    void save(Employee employee);
}
