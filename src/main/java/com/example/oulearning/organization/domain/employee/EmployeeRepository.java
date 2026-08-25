package com.example.oulearning.organization.domain.employee;

import java.util.Optional;

public interface EmployeeRepository {
    Optional<Employee> findById(EmployeeId id);

    void save(Employee employee);
}
