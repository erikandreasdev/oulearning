package com.example.oulearning.organization.infrastructure.persistence.employee;

import com.example.oulearning.organization.domain.employee.model.Email;
import com.example.oulearning.organization.domain.employee.model.Employee;
import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.employee.model.FullName;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
class MyBatisEmployeeRepository implements EmployeeRepository {

    private final EmployeeMapper employeeMapper;

    MyBatisEmployeeRepository(final EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @Override
    public Optional<Employee> findById(final EmployeeId id) {
        return employeeMapper.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Employee> findByEmail(final Email email) {
        return employeeMapper.findByEmail(email.value()).map(this::toDomain);
    }

    @Override
    public void save(final Employee employee) {
        final var entity = toEntity(employee);
        if (employeeMapper.findById(entity.id()).isEmpty()) {
            employeeMapper.insert(entity);
        } else {
            employeeMapper.update(entity);
        }
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private Employee toDomain(final EmployeeEntity entity) {
        return Employee.reconstitute(
                new EmployeeId(entity.id()),
                FullName.of(entity.name(), entity.surname()),
                new Email(entity.email()),
                entity.active());
    }

    private EmployeeEntity toEntity(final Employee employee) {
        return new EmployeeEntity(
                employee.id().value(),
                employee.fullName().name().value(),
                employee.fullName().surname().value(),
                employee.email().value(),
                employee.active());
    }
}
