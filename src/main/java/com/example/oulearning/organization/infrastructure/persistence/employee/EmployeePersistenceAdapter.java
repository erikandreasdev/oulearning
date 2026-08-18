package com.example.oulearning.organization.infrastructure.persistence.employee;

import com.example.oulearning.organization.domain.employee.vo.identity.CorporateKey;
import com.example.oulearning.organization.domain.employee.Employee;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import com.example.oulearning.organization.domain.unit.OuId;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence adapter implementing {@link EmployeeRepository} using MyBatis and {@link EmployeeEntityMapper}.
 */
@Repository
public class EmployeePersistenceAdapter implements EmployeeRepository {

    private final EmployeeMyBatisMapper mapper;
    private final EmployeeEntityMapper entityMapper;

    public EmployeePersistenceAdapter(EmployeeMyBatisMapper mapper, EmployeeEntityMapper entityMapper) {
        this.mapper = Objects.requireNonNull(mapper, "EmployeeMyBatisMapper cannot be null");
        this.entityMapper = Objects.requireNonNull(entityMapper, "EmployeeEntityMapper cannot be null");
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Employee> findByCorporateKey(CorporateKey corporateKey) {
        Objects.requireNonNull(corporateKey, "CorporateKey cannot be null");
        final var entity = mapper.findEmployeeByCorporateKey(corporateKey.value());
        return Optional.ofNullable(entityMapper.toDomain(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findByOuId(OuId ouId) {
        Objects.requireNonNull(ouId, "OuId cannot be null");
        final var entities = mapper.findEmployeesByOuId(ouId.value().toString());
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        return entities.stream().map(entityMapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findByOuIds(Collection<OuId> ouIds) {
        if (ouIds == null || ouIds.isEmpty()) {
            return List.of();
        }
        final var ouIdStrings = ouIds.stream().map(id -> id.value().toString()).toList();
        final var entities = mapper.findEmployeesByOuIds(ouIdStrings);
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        return entities.stream().map(entityMapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void save(Employee employee) {
        Objects.requireNonNull(employee, "Employee cannot be null");

        final var corporateKeyStr = employee.corporateKey().value();
        final var existing = mapper.findEmployeeByCorporateKey(corporateKeyStr);

        if (existing == null) {
            final var entity = entityMapper.toEntity(employee, 0L);
            mapper.insertEmployee(entity);
        } else {
            final var entity = entityMapper.toEntity(employee, existing.version());
            final var updatedRows = mapper.updateEmployee(entity);
            if (updatedRows == 0) {
                throw new OptimisticLockingFailureException(
                        "Concurrent modification detected for employee '%s'".formatted(corporateKeyStr));
            }
        }
    }

    @Override
    @Transactional
    public void delete(CorporateKey corporateKey) {
        Objects.requireNonNull(corporateKey, "CorporateKey cannot be null");
        mapper.deleteEmployee(corporateKey.value());
    }
}
