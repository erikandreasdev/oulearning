package com.example.oulearning.organization.domain.employee.repository;

import com.example.oulearning.organization.domain.employee.CorporateKey;
import com.example.oulearning.organization.domain.employee.Employee;
import com.example.oulearning.organization.domain.unit.OuId;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Domain Repository Port for managing {@link Employee} aggregates.
 */
public interface EmployeeRepository {

    Optional<Employee> findByCorporateKey(CorporateKey corporateKey);

    List<Employee> findByOuId(OuId ouId);

    List<Employee> findByOuIds(Collection<OuId> ouIds);

    void save(Employee employee);

    void delete(CorporateKey corporateKey);
}
