package com.example.oulearning.organization.application.hierarchy.service;

import com.example.oulearning.organization.application.hierarchy.port.in.model.ImportOrganizationResult;
import com.example.oulearning.organization.application.hierarchy.port.in.model.ParsedEmployeeRecord;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.ImportOrganizationUseCase;
import com.example.oulearning.organization.application.hierarchy.port.out.OrganizationDocumentParser;
import com.example.oulearning.organization.domain.employee.model.Email;
import com.example.oulearning.organization.domain.employee.model.Employee;
import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.employee.model.FullName;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import com.example.oulearning.organization.domain.hierarchy.model.Name;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImportOrganizationService implements ImportOrganizationUseCase {

    private final OrganizationDocumentParser documentParser;
    private final EmployeeRepository employeeRepository;
    private final OrganizationalUnitRepository organizationalUnitRepository;
    private final com.example.oulearning.organization.domain.employee.model.IdGenerator employeeIdGenerator;
    private final com.example.oulearning.organization.domain.hierarchy.model.IdGenerator ouIdGenerator;

    public ImportOrganizationService(
            final OrganizationDocumentParser documentParser,
            final EmployeeRepository employeeRepository,
            final OrganizationalUnitRepository organizationalUnitRepository,
            final com.example.oulearning.organization.domain.employee.model.IdGenerator employeeIdGenerator,
            final com.example.oulearning.organization.domain.hierarchy.model.IdGenerator ouIdGenerator) {
        this.documentParser = documentParser;
        this.employeeRepository = employeeRepository;
        this.organizationalUnitRepository = organizationalUnitRepository;
        this.employeeIdGenerator = employeeIdGenerator;
        this.ouIdGenerator = ouIdGenerator;
    }

    @Override
    @Transactional
    public ImportOrganizationResult execute(final InputStream inputStream) {
        final var records = documentParser.parse(inputStream);
        final var employeeEmailToId = new HashMap<String, EmployeeId>();
        final var savedEmployees = processEmployees(records, employeeEmailToId);
        final var unitsMap = processUnitsAndAssignments(records, employeeEmailToId);

        var totalOwners = 0;
        var totalMembers = 0;
        final var savedUnits = new ArrayList<OrganizationalUnit>();
        for (final var unit : unitsMap.values()) {
            final var reloadedUnit = organizationalUnitRepository.findById(unit.id()).orElse(unit);
            savedUnits.add(reloadedUnit);
            totalOwners += reloadedUnit.owners().size();
            totalMembers += reloadedUnit.members().size();
        }

        return new ImportOrganizationResult(
                savedEmployees.size(),
                savedUnits.size(),
                totalOwners,
                totalMembers,
                savedEmployees,
                savedUnits);
    }

    @SuppressWarnings("PMD.LooseCoupling")
    private List<Employee> processEmployees(
            final List<ParsedEmployeeRecord> records,
            final Map<String, EmployeeId> emailToId) {
        final var savedEmployees = new ArrayList<Employee>();
        for (final var record : records) {
            final var emailStr = record.email().trim().toLowerCase();
            if (emailToId.containsKey(emailStr)) {
                continue;
            }
            final var email = Email.of(emailStr);
            final var fullName = FullName.of(record.name().trim(), record.surname().trim());
            final var existingOpt = employeeRepository.findByEmail(email);

            final Employee employee;
            if (existingOpt.isPresent()) {
                employee = existingOpt.get().updateFullName(fullName);
            } else {
                final var newId = EmployeeId.of(employeeIdGenerator.generate());
                employee = Employee.create(newId, fullName, email);
            }
            employeeRepository.save(employee);
            emailToId.put(emailStr, employee.id());
            savedEmployees.add(employee);
        }
        return savedEmployees;
    }

    @SuppressWarnings("PMD.LooseCoupling")
    private Map<String, OrganizationalUnit> processUnitsAndAssignments(
            final List<ParsedEmployeeRecord> records,
            final Map<String, EmployeeId> employeeEmailToId) {

        final var keyToUnit = new HashMap<String, OrganizationalUnit>();

        for (final var record : records) {
            if (record.hierarchyPath().isEmpty()) {
                continue;
            }

            OrganizationalUnitId currentParentId = null;
            OrganizationalUnit currentUnit = null;
            final var pathBuilder = new StringBuilder();

            for (int i = 0; i < record.hierarchyPath().size(); i++) {
                final var rawName = record.hierarchyPath().get(i).trim();
                if (rawName.isEmpty()) {
                    continue;
                }
                if (!pathBuilder.isEmpty()) {
                    pathBuilder.append(" -> ");
                }
                pathBuilder.append(rawName);
                final var pathKey = pathBuilder.toString();

                final var name = Name.of(rawName);
                final var parentOpt = Optional.ofNullable(currentParentId);

                if (keyToUnit.containsKey(pathKey)) {
                    currentUnit = keyToUnit.get(pathKey);
                } else {
                    final var existingDbOpt = organizationalUnitRepository.findByNameAndParentId(name, parentOpt);
                    if (existingDbOpt.isPresent()) {
                        currentUnit = existingDbOpt.get();
                    } else {
                        final var newOuId = OrganizationalUnitId.of(ouIdGenerator.generate());
                        currentUnit = OrganizationalUnit.create(newOuId, name, currentParentId);
                        organizationalUnitRepository.save(currentUnit);
                    }
                    keyToUnit.put(pathKey, currentUnit);
                }
                currentParentId = currentUnit.id();
            }

            if (currentUnit != null) {
                final var emailStr = record.email().trim().toLowerCase();
                final var employeeId = employeeEmailToId.get(emailStr);
                if (employeeId != null) {
                    var updatedUnit = currentUnit.addMember(employeeId);
                    if (record.isManager()) {
                        updatedUnit = updatedUnit.addOwner(employeeId);
                    }
                    if (!updatedUnit.equals(currentUnit)
                            || !updatedUnit.members().equals(currentUnit.members())
                            || !updatedUnit.owners().equals(currentUnit.owners())) {
                        organizationalUnitRepository.save(updatedUnit);
                        keyToUnit.put(pathBuilder.toString(), updatedUnit);
                    }
                }
            }
        }
        return keyToUnit;
    }
}
