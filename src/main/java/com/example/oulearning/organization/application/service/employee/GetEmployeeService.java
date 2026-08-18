package com.example.oulearning.organization.application.service.employee;

import com.example.oulearning.organization.domain.employee.vo.identity.CorporateKey;
import com.example.oulearning.organization.domain.employee.Employee;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.oulearning.organization.application.port.in.query.GetEmployeeQuery;
import com.example.oulearning.organization.application.port.in.usecase.employee.GetEmployeeUseCase;

/**
 * Service orchestrating {@link GetEmployeeUseCase}.
 */
@Service
@Transactional(readOnly = true)
public class GetEmployeeService implements GetEmployeeUseCase {

    private final EmployeeRepository employeeRepository;

    public GetEmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = Objects.requireNonNull(employeeRepository, "EmployeeRepository cannot be null");
    }

    @Override
    public Optional<Employee> execute(GetEmployeeQuery query) {
        Objects.requireNonNull(query, "GetEmployeeQuery cannot be null");
        return employeeRepository.findByCorporateKey(CorporateKey.of(query.corporateKey()));
    }
}
