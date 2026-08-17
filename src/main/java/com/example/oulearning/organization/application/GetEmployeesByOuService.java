package com.example.oulearning.organization.application;

import com.example.oulearning.organization.domain.employee.Employee;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuSearchCriteria;
import com.example.oulearning.organization.domain.unit.repository.OrganizationalUnitRepository;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service orchestrating {@link GetEmployeesByOuUseCase}.
 */
@Service
@Transactional(readOnly = true)
public class GetEmployeesByOuService implements GetEmployeesByOuUseCase {

    private final EmployeeRepository employeeRepository;
    private final OrganizationalUnitRepository unitRepository;

    public GetEmployeesByOuService(
            EmployeeRepository employeeRepository,
            OrganizationalUnitRepository unitRepository) {
        this.employeeRepository = Objects.requireNonNull(employeeRepository, "EmployeeRepository cannot be null");
        this.unitRepository = Objects.requireNonNull(unitRepository, "OrganizationalUnitRepository cannot be null");
    }

    @Override
    public List<Employee> execute(GetEmployeesByOuQuery query) {
        Objects.requireNonNull(query, "GetEmployeesByOuQuery cannot be null");

        final OuSearchCriteria criteria;
        if (query.findOuId().isPresent()) {
            criteria = OuSearchCriteria.byId(OuId.of(query.findOuId().get()), query.includeSubtree());
        } else if (query.findOuName().isPresent()) {
            criteria = OuSearchCriteria.byName(OuName.of(query.findOuName().get()), query.includeSubtree());
        } else {
            throw new IllegalArgumentException("Either ouId or ouName must be provided");
        }

        final var rootOu = unitRepository.find(criteria).orElseThrow(() -> new NoSuchElementException(
                "OrganizationalUnit not found for criteria: " + criteria));

        if (!query.includeSubtree()) {
            return employeeRepository.findByOuId(rootOu.id());
        }

        final var allOuIds = new HashSet<OuId>();
        collectOuIdsRecursively(rootOu, allOuIds);

        return employeeRepository.findByOuIds(allOuIds);
    }

    private void collectOuIdsRecursively(OrganizationalUnit unit, Set<OuId> accumulator) {
        accumulator.add(unit.id());
        for (final var child : unit.loadedChildren()) {
            collectOuIdsRecursively(child, accumulator);
        }
    }
}
